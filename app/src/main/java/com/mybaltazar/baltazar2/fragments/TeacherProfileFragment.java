package com.mybaltazar.baltazar2.fragments;

import android.app.AlertDialog;
import android.app.ProgressDialog;
import android.content.DialogInterface;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.EditText;
import android.widget.TextView;

import com.mybaltazar.baltazar2.R;
import com.mybaltazar.baltazar2.activities.BaseActivity;
import com.mybaltazar.baltazar2.models.Student;
import com.mybaltazar.baltazar2.models.TeacherStatus;
import com.mybaltazar.baltazar2.webservices.CommonData;
import com.mybaltazar.baltazar2.webservices.CommonResponse;
import com.mybaltazar.baltazar2.webservices.DataResponse;
import com.mybaltazar.baltazar2.webservices.RetryableCallback;
import com.mybaltazar.baltazar2.webservices.Services;

import butterknife.BindView;
import butterknife.ButterKnife;
import butterknife.OnClick;
import retrofit2.Call;

public class TeacherProfileFragment extends BaseFragment
{
    @BindView(R.id.lblUserName)                 TextView lblUserName;
    @BindView(R.id.lblNickName)                 TextView lblNickName;
    @BindView(R.id.lblMyMembershipDuration)     TextView lblMyMembershipDuration;
    @BindView(R.id.lblMyAnswersCount)           TextView lblMyAnswersCount;
    @BindView(R.id.lblMyAcceptedAnswersCount)   TextView lblMyAcceptedAnswersCount;

    public TeacherProfileFragment() { }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState)
    {
        View root = inflater.inflate(R.layout.fragment_teacher_profile, container, false);
        ButterKnife.bind(this, root);
        final BaseActivity activity = (BaseActivity)getActivity();
        ProgressDialog progress = activity.showProgress();
        Call<DataResponse<TeacherStatus>> call = activity.createWebService(Services.class).teacherStats(BaseActivity.getToken());
        call.enqueue(new RetryableCallback<DataResponse<TeacherStatus>>(activity, progress) {
            @Override
            public void onFinalSuccess(DataResponse<TeacherStatus> response) {
                loadUI(activity.getProfile(), response.data);
            }
        });
        return  root;
    }

    private void loadUI(Student profile, TeacherStatus status)
    {
        lblUserName.setText(profile.firstName + " " + profile.lastName);
        lblNickName.setText(profile.nickName);
        lblMyMembershipDuration.setText(String.valueOf(profile.membershipDurationDays) + " " + getString(R.string.day));
        if(status != null) {
            lblMyAnswersCount.setText(String.valueOf(status.answersCount));
            lblMyAcceptedAnswersCount.setText(String.valueOf(status.acceptedAnswersCount));
        }
    }

    @OnClick(R.id.btnEditNickName)
    protected void btnEditNickName_Click()
    {
        final EditText editText = (EditText)getLayoutInflater().inflate(R.layout.edit_text, null);
        editText.setText(lblNickName.getText());
        new AlertDialog.Builder(getContext())
                .setTitle(R.string.nickName)
                .setView(editText)
                .setPositiveButton(R.string.change, new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialog, int which) {
                        Student update = new Student();
                        update.nickName = editText.getText().toString();
                        callUpdate(update);
                    }
                })
                .setNegativeButton(R.string.cancel, null)
                .create().show();
    }

    private void callUpdate(Student update)
    {
        Call<DataResponse<Student>> call = ((BaseActivity)getActivity()).createWebService(Services.class).updateStudent(BaseActivity.getToken(), update);
        call.enqueue(new RetryableCallback<DataResponse<Student>>(getContext())
        {
            @Override
            public void onFinalSuccess(DataResponse<Student> response)
            {
                BaseActivity activity = (BaseActivity)getActivity();
                if(activity != null) {
                    activity.setProfile(response.data);
                    CommonData commonData = activity.loadCommonData(false, null);
                    loadUI(response.data, null);
                }
            }
        });
    }

    @OnClick(R.id.btnRequestWithdrawal)
    protected void btnRequestWithdrawal_Click()
    {
        final EditText editText = (EditText)getLayoutInflater().inflate(R.layout.edit_text, null);
        new AlertDialog.Builder(getContext())
                .setTitle(R.string.enter_card_num)
                .setView(editText)
                .setPositiveButton(R.string.change, new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialog, int which)
                    {
                        BaseActivity activity = (BaseActivity)getActivity();
                        ProgressDialog progress = activity.showProgress();
                        String card = editText.getText().toString().trim().replace("-", "").replace(" ", "");
                        Call<CommonResponse> call = activity.createWebService(Services.class).requestWithdrawal(BaseActivity.getToken(), card);
                        call.enqueue(new RetryableCallback<CommonResponse>(activity, progress) {
                            @Override
                            public void onFinalSuccess(CommonResponse response) { }
                        });
                    }
                })
                .setNegativeButton(R.string.cancel, null)
                .create().show();
    }
}
