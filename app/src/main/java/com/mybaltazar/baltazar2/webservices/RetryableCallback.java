package com.mybaltazar.baltazar2.webservices;

import android.app.AlertDialog;
import android.app.ProgressDialog;
import android.content.Context;
import android.support.v4.widget.SwipeRefreshLayout;
import android.util.Log;
import android.widget.Toast;

import com.mybaltazar.baltazar2.R;
import com.mybaltazar.baltazar2.activities.BaseActivity;

import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;

public abstract class RetryableCallback<T extends CommonResponse> implements Callback<T>
{
    private static final int TOTAL_RETRIES = 3;
    private static final String TAG = RetryableCallback.class.getSimpleName();

    private int retryCount = 0;
    private final Context context;
    private ProgressDialog progress = null;
    private SwipeRefreshLayout swipe = null;

    public RetryableCallback(Context context) {
        this.context = context;
    }

    public RetryableCallback(Context context, ProgressDialog progress) {
        this.context = context;
        this.progress = progress;
    }

    public RetryableCallback(Context context, SwipeRefreshLayout swipe)
    {
        this.context = context;
        this.swipe = swipe;
    }

    private void cancelProgress() {
        if(progress != null)
            progress.dismiss();
        if(swipe != null)
            swipe.setRefreshing(false);
    }

    @Override
    public void onResponse(Call<T> call, Response<T> response)
    {
        cancelProgress();
        T resp = response.body();
        if(resp == null) {
            if(response.code() == 401)
                Toast.makeText(context, R.string.wrong_credentials, Toast.LENGTH_LONG).show();
            else
                onFinalFailure(call, new Exception("null body!"));
            return;
        }
        if(resp.message != null)
        {
            if (resp.success)
                Toast.makeText(context, resp.message, Toast.LENGTH_LONG).show();
            else {
                new AlertDialog.Builder(context)
                        .setTitle(R.string.error)
                        .setMessage(resp.message)
                        .setPositiveButton(R.string.ok, null)
                        .create().show();
            }
        }
        if(resp.success)
            onFinalSuccess(resp);
    }

    @Override
    public void onFailure(Call<T> call, Throwable t)
    {
        Log.e(TAG, t.getMessage());
        if (retryCount++ < TOTAL_RETRIES) {
            Log.v(TAG, "Retrying API Call -  (" + retryCount + " / " + TOTAL_RETRIES + ")");
            retry(call);
        }
        else
            onFinalFailure(call, t);
    }

    private void retry(Call<T> call) {
        call.clone().enqueue(this);
    }

    public abstract void onFinalSuccess(T response);

    public void onFinalFailure(Call<T> call, Throwable t)
    {
        cancelProgress();
        Toast.makeText(context, R.string.no_network, Toast.LENGTH_LONG).show();
        if(t != null)
            Log.d("network error", t.getMessage());
    }

}
