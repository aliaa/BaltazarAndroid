package com.mybaltazar.baltazar2.activities;

import android.app.ProgressDialog;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.widget.Toast;

import com.mybaltazar.baltazar2.R;
import com.mybaltazar.baltazar2.web.RetryableCallback;
import com.mybaltazar.baltazar2.models.Degree;
import com.mybaltazar.baltazar2.models.Field;
import com.mybaltazar.baltazar2.web.Requests;
import com.mybaltazar.baltazar2.web.ServerResponse;

import java.util.ArrayList;

import retrofit2.Call;
import retrofit2.Response;

public class RegisterActivity extends BaseActivity
{
    private ArrayList<Field> fields;
    private ArrayList<Degree> degrees;

    public RegisterActivity() {
        super(R.layout.activity_register, true);
    }

    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState)
    {
        super.onCreate(savedInstanceState);

        final ProgressDialog progress = new ProgressDialog(this);
        progress.setMessage(getString(R.string.connecting_to_server));
        progress.setProgressStyle(ProgressDialog.STYLE_SPINNER);
        progress.setIndeterminate(true);
        progress.setProgress(0);
        progress.show();

        Call<ServerResponse> call = createWebService(Requests.class).registerTools();
        call.enqueue(new RetryableCallback<ServerResponse>(call) {
            @Override
            public void onFinalResponse(Call<ServerResponse> call, Response<ServerResponse> response) {
                progress.dismiss();
                ServerResponse resp = response.body();
                if(resp != null && response.code() == 200) {
                    fields = resp.fields;
                    degrees = resp.degrees;
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
}
