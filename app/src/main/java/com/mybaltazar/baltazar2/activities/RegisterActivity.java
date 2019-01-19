package com.mybaltazar.baltazar2.activities;

import android.app.ProgressDialog;
import android.content.Intent;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.view.View;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.EditText;
import android.widget.Spinner;
import android.widget.TextView;
import android.widget.Toast;

import com.mybaltazar.baltazar2.R;
import com.mybaltazar.baltazar2.models.Student;
import com.mybaltazar.baltazar2.models.StudyField;
import com.mybaltazar.baltazar2.webservices.CommonData;
import com.mybaltazar.baltazar2.webservices.DataResponse;
import com.mybaltazar.baltazar2.webservices.RetryableCallback;
import com.mybaltazar.baltazar2.webservices.Services;

import java.util.List;

import butterknife.BindView;
import butterknife.OnClick;
import eu.inmite.android.lib.validations.form.annotations.NotEmpty;
import eu.inmite.android.lib.validations.form.annotations.RegExp;
import retrofit2.Call;
import retrofit2.Callback;
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

    @BindView(R.id.spinnerGrade)
    Spinner spinnerGrade;

    @BindView(R.id.lblStudyField)
    TextView lblStudyField;

    @BindView(R.id.spinnerStudyField)
    Spinner spinnerStudyField;

    @BindView(R.id.txtInvitationCode)
    EditText txtInvitationCode;

    public RegisterActivity() {
        super(R.layout.activity_register, true);
    }

    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState)
    {
        super.onCreate(savedInstanceState);

        ArrayAdapter<String> gradesAdapter = new ArrayAdapter<>(this, android.R.layout.simple_spinner_dropdown_item,
                getResources().getStringArray(R.array.grades));
        spinnerGrade.setAdapter(gradesAdapter);
        spinnerGrade.setOnItemClickListener(new AdapterView.OnItemClickListener() {
            @Override
            public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
                int visibility = position >= 10 ? View.VISIBLE : View.GONE;
                lblStudyField.setVisibility(visibility);
                spinnerStudyField.setVisibility(visibility);
            }
        });

        final ProgressDialog progress = showProgress();
        CommonData commonData = loadCache("common", CommonData.class);
        if(commonData != null)
            setUiData(commonData.studyFields);
        else
        {
            Call<DataResponse<CommonData>> call = createWebService(Services.class).getCommonData();
            call.enqueue(new Callback<DataResponse<CommonData>>()
            {
                @Override
                public void onResponse(Call<DataResponse<CommonData>> call, Response<DataResponse<CommonData>> response)
                {
                    if(response.body() != null && response.body().data != null) {
                        cacheItem(response.body(), "common");
                        setUiData(response.body().data.studyFields);
                        progress.dismiss();
                    }
                    else
                        onFailure(call, null);
                }

                @Override
                public void onFailure(Call<DataResponse<CommonData>> call, Throwable t) {
                    Toast.makeText(RegisterActivity.this, R.string.no_network, Toast.LENGTH_LONG).show();
                    progress.dismiss();
                }
            });
        }
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
        student.phone = txtMobileNum.getText().toString();
        student.password = txtMelliCode.getText().toString();
        student.grade = spinnerGrade.getSelectedItemPosition()+1;
        if(student.grade >= 10)
            student.studyFieldId = ((StudyField)spinnerStudyField.getSelectedItem()).id;
        Call<DataResponse<Student>> call = createWebService(Services.class).registerStudent(student);
        call.enqueue(new RetryableCallback<DataResponse<Student>>(call)
        {
            @Override
            public void onFinalFailure(Call<DataResponse<Student>> call, Throwable t) {
                progress.dismiss();
                Toast.makeText(RegisterActivity.this, R.string.server_problem, Toast.LENGTH_LONG).show();
            }

            @Override
            public void onResponse(Call<DataResponse<Student>> call, Response<DataResponse<Student>> response) {
                progress.dismiss();
                if(response.body() != null && response.body().data != null) {
                    cacheItem(response.body().data, "user");
                    Intent intent = new Intent(RegisterActivity.this, MainActivity.class);
                    startActivity(intent);
                    finish();
                }
                else if(response.body() != null && response.body().message != null)
                    Toast.makeText(RegisterActivity.this, response.body().message, Toast.LENGTH_LONG).show();
                else
                    Toast.makeText(RegisterActivity.this, R.string.server_problem, Toast.LENGTH_LONG).show();
            }
        });
    }
}
