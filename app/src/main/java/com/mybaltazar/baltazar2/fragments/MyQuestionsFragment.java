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
import com.mybaltazar.baltazar2.models.BaseUserContent;
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
            call.enqueue(new RetryableCallback<DataResponse<List<Question>>>(activity, swipe)
            {
                @Override
                public void onFinalSuccess(final DataResponse<List<Question>> response) {
                    activity.loadCommonData(false, new DataListener<CommonData>(activity) {
                        @Override
                        public void onCallBack(CommonData commonData)
                        {
                            adapter = new MyQuestionsAdapter(activity, response.data, commonData.getCoursesMap());
//                              adapter.setOnItemClickListener(MyQuestionsFragment.this);
                            recycler.setAdapter(adapter);
                            lastUpdated = System.currentTimeMillis();
                        }
                    });
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
        if(event.item.publishStatus != BaseUserContent.PublishStatusEnum.WaitForApprove)
            return;

        new AlertDialog.Builder(getContext())
                .setMessage(R.string.sure_to_delete_question)
                .setNegativeButton(R.string.cancell, null)
                .setPositiveButton(R.string.yes, new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialog, int which)
                    {
                        Call<CommonResponse> call = ((BaseActivity)getActivity()).createWebService(Services.class).deleteQuestion(BaseActivity.getToken(), event.item.id);
                        call.enqueue(new RetryableCallback<CommonResponse>(getActivity())
                        {
                            @Override
                            public void onFinalSuccess(CommonResponse response) {
                                loadList(true);
                            }
                        });
                    }
                })
                .create().show();
    }
}
