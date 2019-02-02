package com.mybaltazar.baltazar2.fragments;


import android.content.DialogInterface;
import android.os.Bundle;
import android.support.v4.widget.SwipeRefreshLayout;
import android.support.v7.app.AlertDialog;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Toast;

import com.mybaltazar.baltazar2.R;
import com.mybaltazar.baltazar2.activities.BaseActivity;
import com.mybaltazar.baltazar2.activities.MainActivity;
import com.mybaltazar.baltazar2.adapters.MyQuestionsAdapter;
import com.mybaltazar.baltazar2.adapters.OnItemClickListener;
import com.mybaltazar.baltazar2.events.DeleteQuestionClickEvent;
import com.mybaltazar.baltazar2.models.BaseEntity;
import com.mybaltazar.baltazar2.models.Question;
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
import retrofit2.Response;

public class MyQuestionsFragment extends BaseFragment implements SwipeRefreshLayout.OnRefreshListener, OnItemClickListener<Question>
{
    public MyQuestionsFragment() { }

    @BindView(R.id.recycler)    RecyclerView recycler;
    @BindView(R.id.swipe)       SwipeRefreshLayout swipe;

    private static final int TIME_TO_SAVE_CACHE_MILLIS = 60000;

    private MyQuestionsAdapter adapter;
    private long lastUpdated = 0;

    @Override
    public int getTitleId() {
        return R.string.my_questions;
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState)
    {
        View root = inflater.inflate(R.layout.fragment_qa, container, false);
        ButterKnife.bind(this, root);

        recycler.setLayoutManager(new LinearLayoutManager(getContext()));

        swipe.setOnRefreshListener(this);
        ((BaseActivity)getActivity()).setupSwipe(swipe);
        loadList(true);
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

    @OnClick(R.id.btnAdd)
    protected void btnAdd_Click()
    {
        MainActivity activity = (MainActivity)getActivity();
        activity.openNewQuestionFragment();
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
            Call<DataResponse<List<Question>>> call = activity.createWebService(Services.class).myQuestions(BaseActivity.getToken());
            call.enqueue(new RetryableCallback<DataResponse<List<Question>>>(call) {
                @Override
                public void onResponse(Call<DataResponse<List<Question>>> call, Response<DataResponse<List<Question>>> response)
                {
                    final DataResponse<List<Question>> resp = response.body();
                    if (resp != null && resp.data != null)
                    {
                        activity.loadCommonData(false, new DataListener<CommonData>() {
                            @Override
                            public void onCallBack(CommonData commonData)
                            {
                                swipe.setRefreshing(false);
                                adapter = new MyQuestionsAdapter(activity, resp.data, commonData.getCoursesMap());
//                              adapter.setOnItemClickListener(MyQuestionsFragment.this);
                                recycler.setAdapter(adapter);
                                lastUpdated = System.currentTimeMillis();
                            }

                            @Override
                            public void onFailure()
                            {
                                swipe.setRefreshing(false);
                                Toast.makeText(getContext(), R.string.no_network, Toast.LENGTH_LONG).show();
                            }
                        });

                    }
                    else if (resp != null && resp.message != null) {
                        Toast.makeText(activity, resp.message, Toast.LENGTH_SHORT).show();
                        swipe.setRefreshing(false);
                    }
                    else {
                        Toast.makeText(activity, R.string.server_problem, Toast.LENGTH_SHORT).show();
                        swipe.setRefreshing(false);
                    }
                }

                @Override
                public void onFinalFailure(Call<DataResponse<List<Question>>> call, Throwable t) {
                    swipe.setRefreshing(false);
                    Toast.makeText(getActivity(), t.getMessage(), Toast.LENGTH_LONG).show();
                    Log.e(MyQuestionsFragment.class.getName(), t.getMessage());
                }
            });
        }
        else
        {
            recycler.setAdapter(adapter);
        }
    }

    @Override
    public void onItemClick(Question item)
    {
        MainActivity activity = (MainActivity)getActivity();
        activity.openMyQuestionDetailsFragment(item);
    }

    @Subscribe(threadMode = ThreadMode.MAIN)
    public void onDeleteButtonClick(final DeleteQuestionClickEvent event)
    {
        new AlertDialog.Builder(getContext())
                .setMessage(R.string.sure_to_delete_question)
                .setNegativeButton(R.string.cancel, null)
                .setPositiveButton(R.string.yes, new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialog, int which)
                    {
                        Call<CommonResponse> call = ((BaseActivity)getActivity()).createWebService(Services.class).deleteQuestion(BaseActivity.getToken(), event.item.id);
                        call.enqueue(new RetryableCallback<CommonResponse>(call)
                        {
                            @Override
                            public void onFinalFailure(Call<CommonResponse> call, Throwable t) {
                                Toast.makeText(getContext(), R.string.no_network, Toast.LENGTH_LONG).show();
                            }

                            @Override
                            public void onResponse(Call<CommonResponse> call, Response<CommonResponse> response) {
                                CommonResponse resp = response.body();
                                if(resp == null) {
                                    onFinalFailure(call, null);
                                    return;
                                }
                                if(resp.message != null)
                                    Toast.makeText(getContext(), resp.message, Toast.LENGTH_LONG).show();
                                if(resp.success)
                                    loadList(true);
                            }
                        });
                    }
                })
                .create().show();
    }
}
