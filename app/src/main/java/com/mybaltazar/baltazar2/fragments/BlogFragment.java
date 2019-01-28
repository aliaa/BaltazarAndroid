package com.mybaltazar.baltazar2.fragments;


import android.os.Bundle;
import android.support.v4.widget.SwipeRefreshLayout;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Toast;

import com.mybaltazar.baltazar2.R;
import com.mybaltazar.baltazar2.activities.BaseActivity;
import com.mybaltazar.baltazar2.adapters.BlogAdapter;
import com.mybaltazar.baltazar2.models.Blog;
import com.mybaltazar.baltazar2.webservices.DataResponse;
import com.mybaltazar.baltazar2.webservices.RetryableCallback;
import com.mybaltazar.baltazar2.webservices.Services;

import java.util.List;

import butterknife.BindView;
import butterknife.ButterKnife;
import retrofit2.Call;
import retrofit2.Response;

public class BlogFragment extends BaseFragment implements SwipeRefreshLayout.OnRefreshListener {
    @BindView(R.id.recycler)    RecyclerView recycler;
    @BindView(R.id.swipe)       SwipeRefreshLayout swipe;

    private static final int TIME_TO_SAVE_CACHE_MILLIS = 60000;

    private BlogAdapter adapter;
    private long lastUpdated = 0;

    public BlogFragment() { }

    @Override
    public int getTitleId() {
        return R.string.blog;
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        View root = inflater.inflate(R.layout.fragment_blog, container, false);
        ButterKnife.bind(this, root);

        recycler.setLayoutManager(new LinearLayoutManager(getContext()));

        swipe.setOnRefreshListener(this);
        ((BaseActivity)getActivity()).setupSwipe(swipe);
        loadList(true);
        return root;
    }

    @Override
    public void onRefresh()
    {
        loadList(true);
    }

    private void loadList(boolean force)
    {
        if(adapter == null || force || System.currentTimeMillis() - lastUpdated > TIME_TO_SAVE_CACHE_MILLIS)
        {
            swipe.setRefreshing(true);
            final BaseActivity activity = (BaseActivity) getActivity();
            Call<DataResponse<List<Blog>>> call = activity.createWebService(Services.class).blogList();
            call.enqueue(new RetryableCallback<DataResponse<List<Blog>>>(call) {
                @Override
                public void onResponse(Call<DataResponse<List<Blog>>> call, Response<DataResponse<List<Blog>>> response) {
                    swipe.setRefreshing(false);
                    DataResponse<List<Blog>> resp = response.body();
                    if (resp != null && resp.data != null)
                    {
                        adapter = new BlogAdapter(activity, resp.data);
                        recycler.setAdapter(adapter);
                        lastUpdated = System.currentTimeMillis();
                    }
                    else if (resp != null && resp.message != null)
                        Toast.makeText(activity, resp.message, Toast.LENGTH_SHORT).show();
                    else
                        Toast.makeText(activity, R.string.server_problem, Toast.LENGTH_SHORT).show();
                }

                @Override
                public void onFinalFailure(Call<DataResponse<List<Blog>>> call, Throwable t) {
                    swipe.setRefreshing(false);
                    Toast.makeText(getActivity(), t.getMessage(), Toast.LENGTH_LONG).show();
                    Log.e(BlogFragment.class.getName(), t.getMessage());
                }
            });
        }
        else
        {
            recycler.setAdapter(adapter);
        }
    }
}
