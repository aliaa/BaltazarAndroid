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
        call.enqueue(new RetryableCallback<DataResponse<List<CoinTransaction>>>(call)
        {
            @Override
            public void onFinalFailure(Call<DataResponse<List<CoinTransaction>>> call, Throwable t) {
                Toast.makeText(TransactionsActivity.this, R.string.no_network, Toast.LENGTH_LONG).show();
            }

            @Override
            public void onResponse(Call<DataResponse<List<CoinTransaction>>> call, Response<DataResponse<List<CoinTransaction>>> response) {
                swipe.setRefreshing(false);
                DataResponse<List<CoinTransaction>> resp = response.body();
                if(resp != null && resp.data != null)
                {
                    TransactionsAdapter adapter = new TransactionsAdapter(TransactionsActivity.this, resp.data);
                    recycler.setAdapter(adapter);
                }
                else if (resp != null && resp.message != null)
                    Toast.makeText(TransactionsActivity.this, resp.message, Toast.LENGTH_SHORT).show();
                else
                    Toast.makeText(TransactionsActivity.this, R.string.server_problem, Toast.LENGTH_SHORT).show();
            }
        });
    }

}
