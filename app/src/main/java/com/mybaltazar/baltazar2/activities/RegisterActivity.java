package com.mybaltazar.baltazar2.activities;

import android.app.ProgressDialog;
import android.content.Intent;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.view.View;
import android.widget.ArrayAdapter;
import android.widget.EditText;
import android.widget.Spinner;
import android.widget.TextView;
import android.widget.Toast;

import com.mybaltazar.baltazar2.R;
import com.mybaltazar.baltazar2.models.Student;
import com.mybaltazar.baltazar2.models.StudyField;
import com.mybaltazar.baltazar2.utils.DataListener;
import com.mybaltazar.baltazar2.webservices.CommonData;
import com.mybaltazar.baltazar2.webservices.DataResponse;
import com.mybaltazar.baltazar2.webservices.RetryableCallback;
import com.mybaltazar.baltazar2.webservices.Services;

import java.util.List;

import butterknife.BindArray;
import butterknife.BindView;
import butterknife.OnClick;
import butterknife.OnItemSelected;
import co.ronash.pushe.Pushe;
import eu.inmite.android.lib.validations.form.annotations.NotEmpty;
import eu.inmite.android.lib.validations.form.annotations.RegExp;
import retrofit2.Call;
import retrofit2.Response;

public class RegisterActivity extends BaseActivity
{
    @NotEmpty(messageId = R.string.is_empty)
    @BindView(R.id.txtFirstName)
    EditText txtFirstName;

    @NotEmpty(messageId = R.string.is_empty)
    @BindView(R.id.txtLastName)
    EditText txtLastName;

    @NotEmpty(messageId = R.string.is_empty)
    @RegExp(value = "^09\\d{9}$", messageId = R.string.not_correct)
    @BindView(R.id.txtMobileNum)
    EditText txtMobileNum;

    @NotEmpty(messageId = R.string.is_empty)
    @RegExp(value = "^\\d{10}$", messageId = R.string.not_correct)
    @BindView(R.id.txtMelliCode)
    EditText txtMelliCode;

    @NotEmpty(messageId = R.string.is_empty)
    @BindView(R.id.txtNickName)
    EditText txtNickName;

    @BindView(R.id.spinnerGrade)        Spinner spinnerGrade;
    @BindView(R.id.lblStudyField)       TextView lblStudyField;
    @BindView(R.id.spinnerStudyField)   Spinner spinnerStudyField;
    @BindView(R.id.txtInvitationCode)   EditText txtInvitationCode;

    @BindArray(R.array.grades)  String[] grades;

    public RegisterActivity() {
        super(R.layout.activity_register, true, false);
    }

    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState)
    {
        super.onCreate(savedInstanceState);

        spinnerGrade.setAdapter(new ArrayAdapter<>(this, android.R.layout.simple_spinner_dropdown_item, grades));

        final ProgressDialog progress = showProgress();
        loadCommonData(false, new DataListener<CommonData>(this) {
            @Override
            public void onCallBack(CommonData data) {
                setUiData(data.studyFields);
                progress.dismiss();
            }
            @Override
            public void onFailure() {
                super.onFailure();
                onBackPressed();
                progress.dismiss();
            }
        });
    }

    @OnItemSelected(R.id.spinnerGrade)
    protected void spinnerGrade_ItemSelected(int position)
    {
        int grade = position + 1;
        int visibility = grade >= 10 ? View.VISIBLE : View.GONE;
        lblStudyField.setVisibility(visibility);
        spinnerStudyField.setVisibility(visibility);
    }

    private void setUiData(List<StudyField> studyFieldList) {
        ArrayAdapter<StudyField> adapter = new ArrayAdapter<>(this, android.R.layout.simple_spinner_dropdown_item, studyFieldList);
        spinnerStudyField.setAdapter(adapter);
    }

    @OnClick(R.id.btnRegister)
    protected void btnRegister_Click()
    {
        if(!validateForm())
            return;

        final ProgressDialog progress = showProgress();

        Student student = new Student();
        student.firstName = txtFirstName.getText().toString();
        student.lastName = txtLastName.getText().toString();
        student.nickName = txtNickName.getText().toString();
        student.phone = txtMobileNum.getText().toString();
        student.password = txtMelliCode.getText().toString();
        student.grade = spinnerGrade.getSelectedItemPosition()+1;
        student.invitedFromCode = txtInvitationCode.getText().toString().toUpperCase();
        if(student.grade >= 10)
            student.studyFieldId = ((StudyField)spinnerStudyField.getSelectedItem()).id;

        try {
            student.pusheId = Pushe.getPusheId(this);
        }
        catch (Throwable ignored){}

        Call<DataResponse<Student>> call = createWebService(Services.class).registerStudent(student);
        call.enqueue(new RetryableCallback<DataResponse<Student>>(this, progress)
        {
            @Override
            public void onFinalSuccess(DataResponse<Student> response) {
                setToken(response.data.token);
                setProfile(response.data);
                loadCommonData(true, new DataListener<CommonData>(RegisterActivity.this) {
                    @Override
                    public void onCallBack(CommonData data) {
                        MainActivity.open(RegisterActivity.this, data.notification, data.me.isTeacher);
                        finish();
                    }
                });
            }
        });
    }
}
