package com.mybaltazar.baltazar2.fragments;


import android.app.ProgressDialog;
import android.content.DialogInterface;
import android.content.Intent;
import android.os.Bundle;
import android.support.v4.widget.SwipeRefreshLayout;
import android.support.v7.app.AlertDialog;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;
import android.widget.Toast;

import com.mybaltazar.baltazar2.R;
import com.mybaltazar.baltazar2.activities.BaseActivity;
import com.mybaltazar.baltazar2.activities.MyOrdersActivity;
import com.mybaltazar.baltazar2.adapters.OnItemClickListener;
import com.mybaltazar.baltazar2.adapters.ShopItemsAdapter;
import com.mybaltazar.baltazar2.events.CoinChangedEvent;
import com.mybaltazar.baltazar2.models.ShopItem;
import com.mybaltazar.baltazar2.models.Student;
import com.mybaltazar.baltazar2.utils.DataListener;
import com.mybaltazar.baltazar2.webservices.CommonData;
import com.mybaltazar.baltazar2.webservices.CommonResponse;
import com.mybaltazar.baltazar2.webservices.DataResponse;
import com.mybaltazar.baltazar2.webservices.RetryableCallback;
import com.mybaltazar.baltazar2.webservices.Services;

import org.greenrobot.eventbus.EventBus;
import org.greenrobot.eventbus.Subscribe;
import org.greenrobot.eventbus.ThreadMode;

import java.util.List;

import butterknife.BindView;
import butterknife.ButterKnife;
import butterknife.OnClick;
import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;

public class ShopFragment extends BaseFragment implements SwipeRefreshLayout.OnRefreshListener, OnItemClickListener<ShopItem> {
    public ShopFragment() { }

    @BindView(R.id.recycler)        RecyclerView recycler;
    @BindView(R.id.swipe)           SwipeRefreshLayout swipe;
    @BindView(R.id.lblCoinCount)    TextView lblCoinCount;

    private static final int TIME_TO_SAVE_CACHE_MILLIS = 60000;

    private ShopItemsAdapter adapter;
    private long lastUpdated = 0;

    @Override
    public int getTitleId() {
        return R.string.shop;
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        View root = inflater.inflate(R.layout.fragment_shop, container, false);
        ButterKnife.bind(this, root);

        recycler.setLayoutManager(new LinearLayoutManager(getContext()));
        lblCoinCount.setText(String.valueOf(BaseActivity.getCoinCount()));
        swipe.setOnRefreshListener(this);
        ((BaseActivity)getActivity()).setupSwipe(swipe);
        loadList(false);
        return root;
    }

    @Override
    public void onStart() {
        super.onStart();
        EventBus.getDefault().register(this);
    }

    @Override
    public void onStop() {
        super.onStop();
        EventBus.getDefault().unregister(this);
    }

    @Subscribe(threadMode = ThreadMode.MAIN)
    public void coinCountChanged(CoinChangedEvent event) {
        lblCoinCount.setText(String.valueOf(event.amount));
    }

    @Override
    public void onRefresh() {
        loadList(true);
    }

    private void loadList(boolean force)
    {
        if(adapter == null || force || System.currentTimeMillis() - lastUpdated > TIME_TO_SAVE_CACHE_MILLIS)
        {
            swipe.setRefreshing(true);
            final BaseActivity activity = (BaseActivity) getActivity();
            if(activity == null)
                return;
            Call<DataResponse<List<ShopItem>>> call = activity.createWebService(Services.class).listShopItems(BaseActivity.getToken());
            call.enqueue(new RetryableCallback<DataResponse<List<ShopItem>>>(activity, swipe)
            {
                @Override
                public void onFinalSuccess(DataResponse<List<ShopItem>> response)
                {
                    adapter = new ShopItemsAdapter(activity, response.data);
                    adapter.setOnItemClickListener(ShopFragment.this);
                    recycler.setAdapter(adapter);
                    lastUpdated = System.currentTimeMillis();
                }
            });
        }
        else
        {
            recycler.setAdapter(adapter);
        }
    }

    @Override
    public void onItemClick(final ShopItem item)
    {
        if (BaseActivity.getCoinCount() < item.coinCost) {
            Toast.makeText(getContext(), R.string.not_enough_coins, Toast.LENGTH_SHORT).show();
            return;
        }
        final BaseActivity activity = (BaseActivity)getActivity();
        if(activity == null)
            return;

        new AlertDialog.Builder(getContext())
                .setMessage(R.string.sure_to_buy)
                .setNegativeButton(R.string.cancell, null)
                .setPositiveButton(R.string.yes, new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialog, int which) {
                        callBuy(item);
                    }
                })
                .create().show();


    }

    private void callBuy(final ShopItem item)
    {
        final BaseActivity activity = (BaseActivity)getActivity();
        final ProgressDialog progress = activity.showProgress();
        Call<CommonResponse> call = activity.createWebService(Services.class).addOrder(BaseActivity.getToken(), item.id);
        call.enqueue(new RetryableCallback<CommonResponse>(activity, progress)
        {
            @Override
            public void onFinalSuccess(CommonResponse response) {
                lblCoinCount.setText(String.valueOf(BaseActivity.getCoinCount() - item.coinCost));
                Toast.makeText(getContext(), R.string.order_done, Toast.LENGTH_LONG).show();
                reloadProfile();
            }
        });
    }

    private void reloadProfile()
    {
        Call<DataResponse<Student>> call = ((BaseActivity)getActivity()).createWebService(Services.class).getProfile(BaseActivity.getToken());
        call.enqueue(new RetryableCallback<DataResponse<Student>>(getContext())
        {
            @Override
            public void onFinalSuccess(DataResponse<Student> response) {
                ((BaseActivity)getActivity()).setProfile(response.data);
            }
        });
    }
}
