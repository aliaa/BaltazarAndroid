package com.mybaltazar.baltazar2.activities;

import android.app.AlertDialog;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.net.ConnectivityManager;
import android.net.NetworkInfo;
import android.os.Bundle;
import android.provider.Settings;

import com.mybaltazar.baltazar2.R;
import com.mybaltazar.baltazar2.utils.DataListener;
import com.mybaltazar.baltazar2.webservices.CommonData;

public class SplashActivity extends BaseActivity
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
        if(isConnected) {
            loadCommonData(true, new DataListener<CommonData>(this) {
                @Override
                public void onCallBack(final CommonData data) {
                    if (data.upgrade != null) {
                        AlertDialog.Builder builder = new AlertDialog.Builder(SplashActivity.this)
                                .setTitle(R.string.upgrade)
                                .setMessage(data.upgrade.message)
                                .setCancelable(false);
                        if (data.upgrade.forceUpgrade) {
                            builder.setPositiveButton(R.string.exit, new DialogInterface.OnClickListener() {
                                @Override
                                public void onClick(DialogInterface dialog, int which) {
                                    finish();
                                }
                            });
                        } else {
                            builder.setPositiveButton(R.string.continue_app, new DialogInterface.OnClickListener() {
                                @Override
                                public void onClick(DialogInterface dialog, int which) {
                                    openMainActivity(data.notification);
                                }
                            });
                        }
                        builder.create().show();
                    }
                    else if(getToken() == null)
                        openLoginActivity();
                    else
                        openMainActivity(data.notification);
                }

                @Override
                public void onFailure() {
                    openLoginActivity();
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
                            finish();
                        }
                    })
                    .setNegativeButton(R.string.mobile_data_settings, new DialogInterface.OnClickListener() {
                        @Override
                        public void onClick(DialogInterface dialog, int which) {
                            Intent intent = new Intent(Intent.ACTION_MAIN);
                            intent.setClassName("com.android.phone", "com.android.phone.NetworkSetting");
                            startActivity(intent);
                            finish();
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

    private void openMainActivity(CommonData.Notifications notification)
    {
        Intent intent = new Intent(this, MainActivity.class);
        if(notification != null)
        {
            intent.putExtra(MainActivity.NEW_BLOGS, notification.newBlogs);
            intent.putExtra(MainActivity.NEW_ANSWERS, notification.newAnswers);
            intent.putExtra(MainActivity.NEW_SHOPS, notification.newShops);
        }
        startActivity(intent);
        finish();
    }

    private void openLoginActivity()
    {
        startActivity(new Intent(SplashActivity.this, LoginActivity.class));
        finish();
    }
}
