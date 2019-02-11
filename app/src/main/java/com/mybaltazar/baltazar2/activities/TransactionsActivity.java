package com.mybaltazar.baltazar2.activities;

import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.v4.widget.SwipeRefreshLayout;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.widget.TextView;
import android.widget.Toast;

import com.mybaltazar.baltazar2.R;
import com.mybaltazar.baltazar2.adapters.TransactionsAdapter;
import com.mybaltazar.baltazar2.models.CoinTransaction;
import com.mybaltazar.baltazar2.webservices.DataResponse;
import com.mybaltazar.baltazar2.webservices.RetryableCallback;
import com.mybaltazar.baltazar2.webservices.Services;

import java.util.List;

import butterknife.BindView;
import retrofit2.Call;
import retrofit2.Response;

public class TransactionsActivity extends BaseActivity implements SwipeRefreshLayout.OnRefreshListener {
    @BindView(R.id.recycler)        RecyclerView recycler;
    @BindView(R.id.swipe)           SwipeRefreshLayout swipe;
    @BindView(R.id.lblCoinCount)    TextView lblCoinCount;

    public TransactionsActivity() {
        super(R.layout.activity_transactions, false);
    }

    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        recycler.setLayoutManager(new LinearLayoutManager(this));
        swipe.setOnRefreshListener(this);
        setupSwipe(swipe);
    }

    @Override
    protected void onStart()
    {
        super.onStart();
        lblCoinCount.setText(String.valueOf(BaseActivity.getCoinCount()));
        loadList();
    }

    @Override
    public void onRefresh() {
        loadList();
    }


    private void loadList()
    {
        swipe.setRefreshing(true);
        Call<DataResponse<List<CoinTransaction>>> call = createWebService(Services.class).myCoinTransactions(getToken());
        call.enqueue(new RetryableCallback<DataResponse<List<CoinTransaction>>>(this, swipe)
        {
            @Override
            public void onFinalSuccess(DataResponse<List<CoinTransaction>> response) {
                TransactionsAdapter adapter = new TransactionsAdapter(TransactionsActivity.this, response.data);
                recycler.setAdapter(adapter);
            }
        });
    }

}
