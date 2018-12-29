package com.mybaltazar.baltazar2.activities;

import android.app.ProgressDialog;
import android.content.Intent;
import android.util.Log;
import android.widget.EditText;
import android.widget.Toast;

import com.mybaltazar.baltazar2.BaseActivity;
import com.mybaltazar.baltazar2.R;
import com.mybaltazar.baltazar2.RetryableCallback;
import com.mybaltazar.baltazar2.web.Requests;
import com.mybaltazar.baltazar2.web.ServerRequest;
import com.mybaltazar.baltazar2.web.ServerResponse;

import butterknife.BindView;
import butterknife.OnClick;
import khangtran.preferenceshelper.PrefHelper;
import retrofit2.Call;
import retrofit2.Response;

public class LoginActivity extends BaseActivity
{
    @BindView(R.id.txtMobileNum)
    EditText txtMobileNum;

    @BindView(R.id.txtMelliCode)
    EditText txtMelliCode;

    public LoginActivity()
    {
        super(R.layout.activity_login, true);
    }

    @OnClick(R.id.btnLogin)
    protected void btnLogin_Click()
    {
        final ProgressDialog progress = new ProgressDialog(this);
        progress.setMessage(getString(R.string.connecting_to_server));
        progress.setProgressStyle(ProgressDialog.STYLE_SPINNER);
        progress.setIndeterminate(true);
        progress.setProgress(0);
        progress.show();

        ServerRequest req = new ServerRequest();
        req.phone = txtMobileNum.getText().toString();
        req.password = txtMelliCode.getText().toString();
        Call<ServerResponse> response = createWebService(Requests.class).login(req);
        response.enqueue(new RetryableCallback<ServerResponse>(response) {
            @Override
            public void onFinalResponse(Call<ServerResponse> call, Response<ServerResponse> response) {
                progress.dismiss();
                ServerResponse resp = response.body();
                switch (response.code()) {
                    case 200:
                        if (resp != null) {
                            PrefHelper.setVal(PREF_SESSION_ID, resp.access_token);
                            cacheItem(resp.user, "user");
                            Intent intent = new Intent(LoginActivity.this, MainActivity.class);
                            startActivity(intent);
                            finish();
                        }
                        break;
                    case 400:
                        Toast.makeText(LoginActivity.this, R.string.wrong_credentials, Toast.LENGTH_SHORT).show();
                        break;
                    default:
                        if (resp == null) {
                            Toast.makeText(LoginActivity.this, R.string.server_problem, Toast.LENGTH_SHORT).show();
                        } else {
                            Toast.makeText(LoginActivity.this, resp.message, Toast.LENGTH_SHORT).show();
                        }
                        break;
                }
            }

            @Override
            public void onFinalFailure(Call<ServerResponse> call, Throwable t) {
                Toast.makeText(LoginActivity.this, t.getMessage(), Toast.LENGTH_SHORT).show();
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
