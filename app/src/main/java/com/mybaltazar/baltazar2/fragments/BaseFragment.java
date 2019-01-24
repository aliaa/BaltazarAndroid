package com.mybaltazar.baltazar2.fragments;

import android.support.v4.app.Fragment;
import android.support.v4.widget.SwipeRefreshLayout;

import com.mybaltazar.baltazar2.R;

public abstract class BaseFragment extends Fragment
{
    public int getTitleId() {
        return 0;
    }

    protected void setupSwipe(SwipeRefreshLayout swipe)
    {
        swipe.setColorSchemeColors(
                getResources().getColor(R.color.blue),
                getResources().getColor(R.color.green),
                getResources().getColor(R.color.red));
    }
}
