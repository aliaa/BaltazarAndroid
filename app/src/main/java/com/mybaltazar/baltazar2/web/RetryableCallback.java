package com.mybaltazar.baltazar2.web;

import android.util.Log;

import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;

public abstract class RetryableCallback<T> implements Callback<T>
{
    private static final int TOTAL_RETRIES = 3;
    private static final String TAG = RetryableCallback.class.getSimpleName();

    private int retryCount = 0;
    private final Call<T> call;

    public RetryableCallback(Call<T> call)
    {
        this.call = call;
    }

    @Override
    public void onResponse(Call<T> call, Response<T> response)
    {
        if(isCallSuccess(response)) {
            if(retryCount++ < TOTAL_RETRIES) {
                Log.v(TAG, "Retrying API Call -  (" + retryCount + " / " + TOTAL_RETRIES + ")");
                retry();
            }
            else
                onFinalResponse(call, response);
        }
    }

    @Override
    public void onFailure(Call<T> call, Throwable t)
    {
        Log.e(TAG, t.getMessage());
        if (retryCount++ < TOTAL_RETRIES) {
            Log.v(TAG, "Retrying API Call -  (" + retryCount + " / " + TOTAL_RETRIES + ")");
            retry();
        }
        else
            onFinalFailure(call, t);
    }

    private void retry() {
        call.clone().enqueue(this);
    }

    private static boolean isCallSuccess(Response response)
    {
        if(response == null)
            return false;
        int code = response.code();
        return (code >= 200 && code < 400);
    }

    public abstract void onFinalResponse(Call<T> call, Response<T> response);

    public abstract void onFinalFailure(Call<T> call, Throwable t);
}
