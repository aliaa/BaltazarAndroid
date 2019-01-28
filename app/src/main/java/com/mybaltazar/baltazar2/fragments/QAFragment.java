package com.mybaltazar.baltazar2.fragments;

import android.app.AlertDialog;
import android.app.Dialog;
import android.content.DialogInterface;
import android.content.Intent;
import android.os.Bundle;
import android.support.v4.widget.SwipeRefreshLayout;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.Spinner;
import android.widget.Toast;

import com.mybaltazar.baltazar2.R;
import com.mybaltazar.baltazar2.activities.BaseActivity;
import com.mybaltazar.baltazar2.activities.LoginActivity;
import com.mybaltazar.baltazar2.activities.MainActivity;
import com.mybaltazar.baltazar2.adapters.OnItemClickListener;
import com.mybaltazar.baltazar2.adapters.QuestionsAdapter;
import com.mybaltazar.baltazar2.models.Course;
import com.mybaltazar.baltazar2.models.Question;
import com.mybaltazar.baltazar2.models.StudyField;
import com.mybaltazar.baltazar2.webservices.CommonData;
import com.mybaltazar.baltazar2.webservices.DataResponse;
import com.mybaltazar.baltazar2.webservices.RetryableCallback;
import com.mybaltazar.baltazar2.webservices.Services;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

import butterknife.BindView;
import butterknife.ButterKnife;
import butterknife.OnClick;
import retrofit2.Call;
import retrofit2.Response;

public class QAFragment extends BaseFragment implements SwipeRefreshLayout.OnRefreshListener, OnItemClickListener<Question>
{
    public QAFragment() { }

    @BindView(R.id.recycler)    RecyclerView recycler;
    @BindView(R.id.swipe)       SwipeRefreshLayout swipe;

    private static final int TIME_TO_SAVE_CACHE_MILLIS = 60000;

    private QuestionsAdapter adapter;
    private long lastUpdated = 0;

    private Dialog filterDialog;
    private Spinner spinnerGrade;
    private Spinner spinnerStudyField;
    private Spinner spinnerCourse;

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState)
    {
        View root = inflater.inflate(R.layout.fragment_qa, container, false);
        ButterKnife.bind(this, root);

        recycler.setLayoutManager(new LinearLayoutManager(getContext()));

        swipe.setOnRefreshListener(this);
        ((BaseActivity)getActivity()).setupSwipe(swipe);
        if(filterDialog == null)
            createFilterDialog();
        loadList(false);
        return root;
    }

    private void createFilterDialog()
    {
        final View content = getLayoutInflater().inflate(R.layout.dialog_question_list_filter, null);

        spinnerGrade = content.findViewById(R.id.spinnerGrade);
        spinnerStudyField = content.findViewById(R.id.spinnerStudyField);
        spinnerCourse = content.findViewById(R.id.spinnerCourse);
        final CommonData commonData = BaseActivity.loadCache(getContext(), BaseActivity.PREF_COMMON, CommonData.class);

        List<String> gradesList = new ArrayList<>(13);
        gradesList.add(getString(R.string.all));
        gradesList.addAll(Arrays.asList(getResources().getStringArray(R.array.grades)));
        spinnerGrade.setAdapter(new ArrayAdapter<>(getContext(), android.R.layout.simple_spinner_dropdown_item, gradesList));
        spinnerGrade.setOnItemSelectedListener(new AdapterView.OnItemSelectedListener() {
            @Override
            public void onItemSelected(AdapterView<?> parent, View view, int position, long id) {
                content.findViewById(R.id.layoutStudyField).setVisibility(position >= 10 ? View.VISIBLE : View.GONE);
                content.findViewById(R.id.layoutCourse).setVisibility(position > 0 ? View.VISIBLE : View.GONE);
                if(position > 0)
                    setCourseSpinnerAdapter(commonData.courses, spinnerCourse, position, (StudyField)spinnerStudyField.getSelectedItem());
            }

            @Override
            public void onNothingSelected(AdapterView<?> parent) { }
        });

        spinnerStudyField.setAdapter(new ArrayAdapter<>(getContext(), android.R.layout.simple_spinner_dropdown_item, commonData.studyFields));
        spinnerStudyField.setOnItemSelectedListener(new AdapterView.OnItemSelectedListener() {
            @Override
            public void onItemSelected(AdapterView<?> parent, View view, int position, long id) {
                setCourseSpinnerAdapter(commonData.courses, spinnerCourse, spinnerGrade.getSelectedItemPosition(), (StudyField)spinnerStudyField.getSelectedItem());
            }

            @Override
            public void onNothingSelected(AdapterView<?> parent) { }
        });

        AlertDialog.Builder builder = new AlertDialog.Builder(getContext())
                .setTitle(R.string.filter)
                .setView(content)
                .setNegativeButton(R.string.cancel, null)
                .setPositiveButton(R.string.ok, new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialog, int which) {
                        loadList(true);
                    }
                });
        filterDialog = builder.create();
    }

    private void setCourseSpinnerAdapter(List<Course> courses, Spinner spinnerCourse, int grade, StudyField studyField)
    {
        List<Course> list = new ArrayList<>();
        list.add(Course.ALL);
        for (Course c : courses)
            if(c.grade == grade && (grade < 10 || studyField.id.equals(c.studyFieldId)))
                list.add(c);
        spinnerCourse.setAdapter(new ArrayAdapter<>(getContext(), android.R.layout.simple_spinner_dropdown_item, list));
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
            final MainActivity activity = (MainActivity) getActivity();

            Integer grade = null;
            if(spinnerGrade.getSelectedItemPosition() > 0)
                grade = spinnerGrade.getSelectedItemPosition();
            Course course = Course.ALL;
            if(grade != null && spinnerCourse.getCount() > 0)
                course = (Course)spinnerCourse.getSelectedItem();
            int page = 0;
            activity.setFilterMenuItemActive(grade != null);

            Call<DataResponse<List<Question>>> call = activity.createWebService(Services.class).questionList(
                    BaseActivity.getToken(), grade, course.id, null, page);
            call.enqueue(new RetryableCallback<DataResponse<List<Question>>>(call) {
                @Override
                public void onFinalFailure(Call<DataResponse<List<Question>>> call, Throwable t) {
                    Toast.makeText(getContext(), R.string.no_network, Toast.LENGTH_LONG).show();
                    swipe.setRefreshing(false);
                }

                @Override
                public void onResponse(Call<DataResponse<List<Question>>> call, Response<DataResponse<List<Question>>> response) {
                    swipe.setRefreshing(false);
                    DataResponse<List<Question>> resp = response.body();
                    if(response.code() == 401) // unauthorized
                    {
                        startActivity(new Intent(getContext(), LoginActivity.class));
                        getActivity().finish();
                    }
                    else if(resp == null)
                        onFinalFailure(call, new Exception("null body!"));
                    else if(resp.data == null && resp.message != null)
                        Toast.makeText(getContext(), resp.message, Toast.LENGTH_LONG).show();
                    else
                    {
                        CommonData commonData = BaseActivity.loadCache(activity, BaseActivity.PREF_COMMON, CommonData.class);
                        adapter = new QuestionsAdapter(activity, resp.data, commonData.getCoursesMap());
                        adapter.setOnItemClickListener(QAFragment.this);
                        recycler.setAdapter(adapter);
                        lastUpdated = System.currentTimeMillis();
                    }
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
        activity.openQuestionDetailsFragment(item);
    }

    public void showFilterDialog()
    {
        filterDialog.show();
    }
}
