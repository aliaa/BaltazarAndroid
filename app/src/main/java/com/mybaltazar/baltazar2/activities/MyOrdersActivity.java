package com.mybaltazar.baltazar2.activities;

import android.support.annotation.Nullable;
import android.support.v4.widget.SwipeRefreshLayout;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;

import com.mybaltazar.baltazar2.R;
import com.mybaltazar.baltazar2.adapters.OrderItemsAdapter;
import com.mybaltazar.baltazar2.events.DeleteOrderClickEvent;
import com.mybaltazar.baltazar2.events.DeleteQuestionClickEvent;
import com.mybaltazar.baltazar2.models.ShopOrder;
import com.mybaltazar.baltazar2.models.Student;
import com.mybaltazar.baltazar2.webservices.CommonResponse;
import com.mybaltazar.baltazar2.webservices.DataResponse;
import com.mybaltazar.baltazar2.webservices.RetryableCallback;
import com.mybaltazar.baltazar2.webservices.Services;

import org.greenrobot.eventbus.EventBus;
import org.greenrobot.eventbus.Subscribe;
import org.greenrobot.eventbus.ThreadMode;

import java.util.List;

import butterknife.BindView;
import retrofit2.Call;

public class MyOrdersActivity extends BaseActivity implements SwipeRefreshLayout.OnRefreshListener
{
    @BindView(R.id.recycler)    RecyclerView recycler;
    @BindView(R.id.swipe)       SwipeRefreshLayout swipe;

    public MyOrdersActivity()
    {
        super(R.layout.activity_my_orders, false, true);
    }

    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        swipe.setOnRefreshListener(this);
        setupSwipe(swipe);
        recycler.setLayoutManager(new LinearLayoutManager(this));
    }

    @Override
    public void onStart() {
        super.onStart();
        EventBus.getDefault().register(this);
        onRefresh();
    }

    @Override
    public void onStop() {
        super.onStop();
        EventBus.getDefault().unregister(this);
    }

    @Subscribe(threadMode = ThreadMode.MAIN)
    public void onDeleteButtonClick(final DeleteOrderClickEvent event)
    {
        if(event.order.status != ShopOrder.OrderStatus.WaitForApprove)
            return;
        swipe.setRefreshing(true);
        Call<CommonResponse> call = createWebService(Services.class).cancelOrder(getToken(), event.order.id);
        call.enqueue(new RetryableCallback<CommonResponse>(this, swipe) {
            @Override
            public void onFinalSuccess(CommonResponse response) {
                onRefresh();
                refreshProfile();
            }
        });
    }

    @Override
    public void onRefresh()
    {
        swipe.setRefreshing(true);
        Call<DataResponse<List<ShopOrder>>> call = createWebService(Services.class).myOrders(getToken());
        call.enqueue(new RetryableCallback<DataResponse<List<ShopOrder>>>(this, swipe) {
            @Override
            public void onFinalSuccess(DataResponse<List<ShopOrder>> response) {
                recycler.setAdapter(new OrderItemsAdapter(MyOrdersActivity.this, response.data));
            }
        });
    }

    private void refreshProfile()
    {
        Call<DataResponse<Student>> call = createWebService(Services.class).getProfile(BaseActivity.getToken());
        call.enqueue(new RetryableCallback<DataResponse<Student>>(this)
        {
            @Override
            public void onFinalSuccess(DataResponse<Student> response) {
                setProfile(response.data);
            }
        });
    }
}
