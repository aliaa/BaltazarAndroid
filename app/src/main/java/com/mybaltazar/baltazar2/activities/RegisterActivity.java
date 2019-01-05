package com.mybaltazar.baltazar2.activities;

import android.app.ProgressDialog;
import android.content.Intent;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.widget.ArrayAdapter;
import android.widget.EditText;
import android.widget.Spinner;
import android.widget.TextView;
import android.widget.Toast;

import com.mybaltazar.baltazar2.R;
import com.mybaltazar.baltazar2.models.Field;
import com.mybaltazar.baltazar2.models.Level;
import com.mybaltazar.baltazar2.web.Requests;
import com.mybaltazar.baltazar2.web.RetryableCallback;
import com.mybaltazar.baltazar2.web.ServerRequest;
import com.mybaltazar.baltazar2.web.ServerResponse;

import java.util.ArrayList;

import butterknife.BindView;
import butterknife.OnClick;
import eu.inmite.android.lib.validations.form.annotations.NotEmpty;
import eu.inmite.android.lib.validations.form.annotations.RegExp;
import khangtran.preferenceshelper.PrefHelper;
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

    @BindView(R.id.spinnerLevel)
    Spinner spinnerLevel;

    @BindView(R.id.lblField)
    TextView lblField;

    @BindView(R.id.spinnerField)
    Spinner spinnerField;

    @BindView(R.id.txtInvitationCode)
    EditText txtInvitationCode;

    public RegisterActivity() {
        super(R.layout.activity_register, true);
    }

    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState)
    {
        super.onCreate(savedInstanceState);

        final ProgressDialog progress = showProgress();
        Call<ServerResponse> call = createWebService(Requests.class).registerTools();
        call.enqueue(new RetryableCallback<ServerResponse>(call) {
            @Override
            public void onFinalResponse(Call<ServerResponse> call, Response<ServerResponse> response) {
                progress.dismiss();
                ServerResponse resp = response.body();
                if(resp != null && response.code() == 200) {
                    setLevels(resp.levels);
                    setFields(resp.fields);
                    cacheItem(resp, "tools");
                }
                else {
                    Toast.makeText(RegisterActivity.this, R.string.server_problem, Toast.LENGTH_SHORT).show();
                }
            }

            @Override
            public void onFinalFailure(Call<ServerResponse> call, Throwable t) {
                progress.dismiss();
                Toast.makeText(RegisterActivity.this, t.getMessage(), Toast.LENGTH_SHORT).show();
            }
        });
    }

    private void setLevels(ArrayList<Level> levels)
    {
        ArrayAdapter<Level> adapter = new ArrayAdapter<>(this, android.R.layout.simple_spinner_dropdown_item, levels);
        spinnerLevel.setAdapter(adapter);
    }

    private void setFields(ArrayList<Field> fields)
    {
        ArrayAdapter<Field> adapter = new ArrayAdapter<>(this, android.R.layout.simple_spinner_dropdown_item, fields);
        spinnerField.setAdapter(adapter);
    }

    @OnClick(R.id.btnRegister)
    protected void btnRegister_Click()
    {
        if(!validateForm())
            return;

        final ProgressDialog progress = showProgress();

        ServerRequest req = new ServerRequest();
        req.first_name = txtFirstName.getText().toString();
        req.last_name = txtLastName.getText().toString();
        req.phone = txtMobileNum.getText().toString();
        req.password = txtMelliCode.getText().toString();
        req.level_id = ((Level)spinnerLevel.getSelectedItem()).id;
        req.field_id = ((Field)spinnerField.getSelectedItem()).id;
        req.invite_code = txtInvitationCode.getText().toString();

        Call<ServerResponse> call = createWebService(Requests.class).register(req);
        call.enqueue(new RetryableCallback<ServerResponse>(call) {
            @Override
            public void onFinalResponse(Call<ServerResponse> call, Response<ServerResponse> response) {
                progress.dismiss();
                ServerResponse resp = response.body();
                if(resp != null && response.code() == 200 && resp.code == 200)
                {
                    PrefHelper.setVal(PREF_SESSION_ID, "bearer " + resp.access_token);
                    cacheItem(resp.user, "user");
                    Intent intent = new Intent(RegisterActivity.this, MainActivity.class);
                    startActivity(intent);
                    finish();
                }
                else {
                    if(resp == null || resp.message == null)
                        Toast.makeText(RegisterActivity.this, R.string.server_problem, Toast.LENGTH_SHORT).show();
                    else
                        Toast.makeText(RegisterActivity.this, resp.message, Toast.LENGTH_SHORT).show();
                }
            }

            @Override
            public void onFinalFailure(Call<ServerResponse> call, Throwable t) {
                progress.dismiss();
                Toast.makeText(RegisterActivity.this, R.string.server_problem, Toast.LENGTH_SHORT).show();
            }
        });
    }
}
