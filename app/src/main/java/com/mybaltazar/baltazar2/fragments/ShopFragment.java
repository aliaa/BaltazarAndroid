package com.mybaltazar.baltazar2.fragments;


import android.app.ProgressDialog;
import android.os.Bundle;
import android.support.v4.widget.SwipeRefreshLayout;
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
import com.mybaltazar.baltazar2.adapters.OnItemClickListener;
import com.mybaltazar.baltazar2.adapters.ShopItemsAdapter;
import com.mybaltazar.baltazar2.models.ShopItem;
import com.mybaltazar.baltazar2.utils.DataListener;
import com.mybaltazar.baltazar2.webservices.CommonData;
import com.mybaltazar.baltazar2.webservices.CommonResponse;
import com.mybaltazar.baltazar2.webservices.DataResponse;
import com.mybaltazar.baltazar2.webservices.RetryableCallback;
import com.mybaltazar.baltazar2.webservices.Services;

import java.util.List;

import butterknife.BindView;
import butterknife.ButterKnife;
import retrofit2.Call;
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
            call.enqueue(new RetryableCallback<DataResponse<List<ShopItem>>>(call) {
                @Override
                public void onResponse(Call<DataResponse<List<ShopItem>>> call, Response<DataResponse<List<ShopItem>>> response) {
                    swipe.setRefreshing(false);
                    DataResponse<List<ShopItem>> resp = response.body();
                    if (resp != null && resp.data != null)
                    {
                        CommonData commonData = BaseActivity.loadCache(activity, BaseActivity.PREF_COMMON, CommonData.class);
                        if(commonData == null)
                        {
                            onFinalFailure(call, new Exception("اطلاعات عمومی موجود نیست! لطفا دوباره وارد برنامه شوید."));
                            return;
                        }

                        adapter = new ShopItemsAdapter(activity, resp.data);
                        adapter.setOnItemClickListener(ShopFragment.this);
                        recycler.setAdapter(adapter);
                        lastUpdated = System.currentTimeMillis();
                    }
                    else if (resp != null && resp.message != null)
                        Toast.makeText(activity, resp.message, Toast.LENGTH_SHORT).show();
                    else
                        Toast.makeText(activity, R.string.server_problem, Toast.LENGTH_SHORT).show();
                }

                @Override
                public void onFinalFailure(Call<DataResponse<List<ShopItem>>> call, Throwable t) {
                    swipe.setRefreshing(false);
                    Toast.makeText(getActivity(), t.getMessage(), Toast.LENGTH_LONG).show();
                    Log.e(ShopFragment.class.getName(), t.getMessage());
                }
            });
        }
        else
        {
            recycler.setAdapter(adapter);
        }
    }

    @Override
    public void onItemClick(final ShopItem item) {
        if (BaseActivity.getCoinCount() < item.coinCost) {
            Toast.makeText(getContext(), "تعداد سکه شما برای خرید کافی نیست!", Toast.LENGTH_SHORT).show();
            return;
        }
        final BaseActivity activity = (BaseActivity)getActivity();
        if(activity == null)
            return;
        final ProgressDialog progress = activity.showProgress();
        Call<CommonResponse> call = activity.createWebService(Services.class).addOrder(BaseActivity.getToken(), item.id);
        call.enqueue(new RetryableCallback<CommonResponse>(call) {
            @Override
            public void onFinalFailure(Call<CommonResponse> call, Throwable t) {
                Toast.makeText(getContext(), R.string.no_network, Toast.LENGTH_LONG).show();
                progress.dismiss();
            }

            @Override
            public void onResponse(Call<CommonResponse> call, Response<CommonResponse> response) {
                progress.dismiss();
                CommonResponse resp = response.body();
                if(resp != null)
                {
                    if(resp.message != null)
                        Toast.makeText(getContext(), resp.message, Toast.LENGTH_LONG).show();
                    if(resp.success)
                    {
                        lblCoinCount.setText(String.valueOf(BaseActivity.getCoinCount() - item.coinCost));
                        Toast.makeText(getContext(), R.string.order_done, Toast.LENGTH_LONG).show();
                        activity.loadCommonData(true, new DataListener<CommonData>() {
                            @Override
                            public void onCallBack(CommonData data) {
                                if(data.me != null)
                                    lblCoinCount.setText(String.valueOf(data.me.coins));
                            }

                            @Override
                            public void onFailure() { }
                        });
                    }
                }
                else
                    onFinalFailure(call, null);
            }
        });
    }
}
