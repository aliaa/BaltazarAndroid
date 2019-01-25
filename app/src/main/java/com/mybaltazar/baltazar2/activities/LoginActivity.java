package com.mybaltazar.baltazar2.activities;

import android.app.ProgressDialog;
import android.content.Intent;
import android.util.Log;
import android.widget.EditText;
import android.widget.Toast;

import com.mybaltazar.baltazar2.R;
import com.mybaltazar.baltazar2.models.Student;
import com.mybaltazar.baltazar2.webservices.DataResponse;
import com.mybaltazar.baltazar2.webservices.RetryableCallback;
import com.mybaltazar.baltazar2.webservices.Services;

import butterknife.BindView;
import butterknife.OnClick;
import eu.inmite.android.lib.validations.form.annotations.NotEmpty;
import retrofit2.Call;
import retrofit2.Response;

public class LoginActivity extends BaseActivity
{
    @NotEmpty(messageId = R.string.is_empty)
    @BindView(R.id.txtMobileNum)
    EditText txtMobileNum;

    @NotEmpty(messageId = R.string.is_empty)
    @BindView(R.id.txtMelliCode)
    EditText txtMelliCode;

    public LoginActivity()
    {
        super(R.layout.activity_login, true);
    }

    @OnClick(R.id.btnLogin)
    protected void btnLogin_Click()
    {
        if(!validateForm())
            return;

        final ProgressDialog progress = showProgress();
        String phone = txtMobileNum.getText().toString();
        String password = txtMelliCode.getText().toString();
        Call<DataResponse<Student>> response = createWebService(Services.class).login(phone, password);
        response.enqueue(new RetryableCallback<DataResponse<Student>>(response) {
            @Override
            public void onResponse(Call<DataResponse<Student>> call, Response<DataResponse<Student>> response) {
                progress.dismiss();
                DataResponse<Student> resp = response.body();
                switch (response.code()) {
                    case 200:
                    case 201:
                        if (resp != null && resp.data != null) {
                            setToken(resp.data.token);
                            setCoinCount(resp.data.coins);
                            cacheItem(resp.data, PREF_PROFILE);
                            Intent intent = new Intent(LoginActivity.this, MainActivity.class);
                            startActivity(intent);
                            finish();
                        }
                        else
                        {
                            if (resp == null)
                                Toast.makeText(LoginActivity.this, R.string.server_problem, Toast.LENGTH_LONG).show();
                            else
                                Toast.makeText(LoginActivity.this, resp.message, Toast.LENGTH_LONG).show();
                        }
                        break;
                    case 400:
                    case 401:
                        Toast.makeText(LoginActivity.this, R.string.wrong_credentials, Toast.LENGTH_LONG).show();
                        break;
                    default:
                        if (resp == null)
                            Toast.makeText(LoginActivity.this, R.string.server_problem, Toast.LENGTH_LONG).show();
                        else
                            Toast.makeText(LoginActivity.this, resp.message, Toast.LENGTH_LONG).show();
                        break;
                }
            }

            @Override
            public void onFinalFailure(Call<DataResponse<Student>> call, Throwable t) {
                Toast.makeText(LoginActivity.this, t.getMessage(), Toast.LENGTH_LONG).show();
                progress.dismiss();
                Log.d("error:", t.getMessage());
            }
        });

    }

    @OnClick(R.id.lblRegister)
    protected void lblRegister_Click()
    {
        Intent i = new Intent(this, RegisterActivity.class);
        startActivity(i);
    }
}
