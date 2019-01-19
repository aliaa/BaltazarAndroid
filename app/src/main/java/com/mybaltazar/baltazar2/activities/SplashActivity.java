package com.mybaltazar.baltazar2.activities;

import android.app.AlertDialog;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.net.ConnectivityManager;
import android.net.NetworkInfo;
import android.os.Bundle;
import android.os.Handler;
import android.provider.Settings;
import android.widget.Toast;

import com.mybaltazar.baltazar2.R;
import com.mybaltazar.baltazar2.webservices.CommonData;
import com.mybaltazar.baltazar2.webservices.DataResponse;
import com.mybaltazar.baltazar2.webservices.Services;

import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;

public class SplashActivity extends BaseActivity implements Runnable
{
    static final int DELAY_MILLIS = 1000;

    public SplashActivity() {
        super(R.layout.activity_splash, false);
    }

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
    }

    @Override
    protected void onResume() {
        super.onResume();

        ConnectivityManager cm = (ConnectivityManager)getSystemService(Context.CONNECTIVITY_SERVICE);
        NetworkInfo activeNetwork = cm.getActiveNetworkInfo();
        boolean isConnected = activeNetwork != null && activeNetwork.isConnectedOrConnecting();
        if(isConnected)
        {
            Call<DataResponse<CommonData>> call = createWebService(Services.class).getCommonData();
            call.enqueue(new Callback<DataResponse<CommonData>>()
            {
                @Override
                public void onResponse(Call<DataResponse<CommonData>> call, Response<DataResponse<CommonData>> response) {
                    if(response.body() != null && response.body().data != null) {
                        cacheItem(response.body().data, "common");
                        run();
                    }
                    else
                        onFailure(call, null);
                }

                @Override
                public void onFailure(Call<DataResponse<CommonData>> call, Throwable t) {
                    Toast.makeText(SplashActivity.this, R.string.no_network, Toast.LENGTH_LONG).show();
                    run();
                }
            });
        }
        else
        {
            new AlertDialog.Builder(this)
                    .setTitle(R.string.error)
                    .setMessage(R.string.no_network)
                    .setPositiveButton(R.string.wifi_settings, new DialogInterface.OnClickListener() {
                        @Override
                        public void onClick(DialogInterface dialog, int which) {
                            startActivity(new Intent(Settings.ACTION_WIFI_SETTINGS));
                        }
                    })
                    .setNegativeButton(R.string.mobile_data_settings, new DialogInterface.OnClickListener() {
                        @Override
                        public void onClick(DialogInterface dialog, int which) {
                            Intent intent = new Intent(Intent.ACTION_MAIN);
                            intent.setClassName("com.android.phone", "com.android.phone.NetworkSetting");
                            startActivity(intent);
                        }
                    })
                    .setNeutralButton(R.string.exit, new DialogInterface.OnClickListener() {
                        @Override
                        public void onClick(DialogInterface dialog, int which) {
                            finish();
                        }
                    })
                    .setCancelable(false)
                    .show();
        }
    }

    private void runDelayed(int millis) {
        Handler handler = new Handler();
        handler.postDelayed(this, millis);
    }

    @Override
    public void run() {
        if(getToken() == null)
            startActivity(new Intent(this, LoginActivity.class));
        else
            startActivity(new Intent(this, MainActivity.class));
        finish();
    }
}
