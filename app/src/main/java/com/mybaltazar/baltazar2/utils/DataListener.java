package com.mybaltazar.baltazar2.utils;

import android.content.Context;
import android.widget.Toast;

import com.mybaltazar.baltazar2.R;

public abstract class DataListener<T>
{
    private final Context context;
    public DataListener(Context context)
    {
        this.context = context;
    }

    public abstract void onCallBack(T data);
    public void onFailure()
    {
        Toast.makeText(context, R.string.no_network, Toast.LENGTH_LONG).show();
    }
}
