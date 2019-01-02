package com.mybaltazar.baltazar2.fragments;


import android.os.Bundle;
import android.support.v4.app.Fragment;
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
import com.mybaltazar.baltazar2.adapters.OnItemClickListener;
import com.mybaltazar.baltazar2.adapters.QuestionsAdapter;
import com.mybaltazar.baltazar2.models.Question;
import com.mybaltazar.baltazar2.web.QuestionListResponse;
import com.mybaltazar.baltazar2.web.Requests;
import com.mybaltazar.baltazar2.web.RetryableCallback;
import com.mybaltazar.baltazar2.web.ServerRequest;
import com.mybaltazar.baltazar2.web.ServerResponse;

import java.util.ArrayList;

import butterknife.BindView;
import butterknife.ButterKnife;
import butterknife.OnClick;
import retrofit2.Call;
import retrofit2.Response;

public class QAFragment extends Fragment implements SwipeRefreshLayout.OnRefreshListener, OnItemClickListener<Question> {
    public QAFragment() { }

    @BindView(R.id.recycler)
    RecyclerView recycler;

    @BindView(R.id.swipe)
    SwipeRefreshLayout swipe;

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        View root = inflater.inflate(R.layout.fragment_qa, container, false);
        ButterKnife.bind(this, root);

        recycler.setLayoutManager(new LinearLayoutManager(getContext()));

        swipe.setOnRefreshListener(this);
        swipe.setColorSchemeColors(
                getResources().getColor(R.color.blue),
                getResources().getColor(R.color.green),
                getResources().getColor(R.color.red));
        onRefresh();
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
//        recycler.setAdapter(null);
        swipe.setRefreshing(true);
        final BaseActivity activity = (BaseActivity)getActivity();
        Call<QuestionListResponse> call = activity.createWebService(Requests.class).questionList(BaseActivity.getSessionId());
        call.enqueue(new RetryableCallback<QuestionListResponse>(call) {
            @Override
            public void onFinalResponse(Call<QuestionListResponse> call, Response<QuestionListResponse> response) {
                swipe.setRefreshing(false);
                QuestionListResponse resp = response.body();
                if(response.code() == 200 && resp != null) {
                    QuestionsAdapter adapter = new QuestionsAdapter(activity, resp.list);
                    adapter.setOnItemClickListener(QAFragment.this);
                    recycler.setAdapter(adapter);
                }
                else if(resp != null && resp.message != null)
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

    @Override
    public void onItemClick(Question item)
    {
        MainActivity activity = (MainActivity)getActivity();
        activity.openQuestionDetailsFragment(item);
    }
}
