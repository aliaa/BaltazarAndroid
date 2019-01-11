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
import com.mybaltazar.baltazar2.activities.MainActivity;
import com.mybaltazar.baltazar2.adapters.MyQuestionsAdapter;
import com.mybaltazar.baltazar2.adapters.OnItemClickListener;
import com.mybaltazar.baltazar2.adapters.QuestionsAdapter;
import com.mybaltazar.baltazar2.models.Question;
import com.mybaltazar.baltazar2.web.QuestionListResponse;
import com.mybaltazar.baltazar2.web.Requests;
import com.mybaltazar.baltazar2.web.RetryableCallback;

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
        swipe.setColorSchemeColors(
                getResources().getColor(R.color.blue),
                getResources().getColor(R.color.green),
                getResources().getColor(R.color.red));
        loadList(false);
        return root;
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

    public void loadList(boolean force)
    {
        if(adapter == null || force || System.currentTimeMillis() - lastUpdated > TIME_TO_SAVE_CACHE_MILLIS)
        {
            swipe.setRefreshing(true);
            final BaseActivity activity = (BaseActivity) getActivity();
            Call<QuestionListResponse> call = activity.createWebService(Requests.class).myQuestions(BaseActivity.getSessionId());
            call.enqueue(new RetryableCallback<QuestionListResponse>(call) {
                @Override
                public void onResponse(Call<QuestionListResponse> call, Response<QuestionListResponse> response) {
                    swipe.setRefreshing(false);
                    QuestionListResponse resp = response.body();
                    if (response.code() == 200 && resp != null)
                    {
                        adapter = new MyQuestionsAdapter(activity, resp.questions);
//                        adapter.setOnItemClickListener(MyQuestionsFragment.this);
                        recycler.setAdapter(adapter);
                        lastUpdated = System.currentTimeMillis();
                    }
                    else if (resp != null && resp.message != null)
                        Toast.makeText(activity, resp.message, Toast.LENGTH_SHORT).show();
                    else
                        Toast.makeText(activity, R.string.server_problem, Toast.LENGTH_SHORT).show();
                }

                @Override
                public void onFinalFailure(Call<QuestionListResponse> call, Throwable t) {
                    swipe.setRefreshing(false);
                    Toast.makeText(getActivity(), t.getMessage(), Toast.LENGTH_SHORT).show();
                    Log.e(QAFragment.class.getName(), t.getMessage());
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
}
