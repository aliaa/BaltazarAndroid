package com.mybaltazar.baltazar2.fragments;


import android.content.Intent;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import com.mybaltazar.baltazar2.R;
import com.mybaltazar.baltazar2.activities.TransactionsActivity;

import butterknife.ButterKnife;
import butterknife.OnClick;

public class ProfileFragment extends BaseFragment
{
    public ProfileFragment() { }

    @Override
    public int getTitleId() {
        return R.string.profile;
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        View root = inflater.inflate(R.layout.fragment_profile, container, false);
        ButterKnife.bind(this, root);
        return  root;
    }

    @OnClick(R.id.btnFillProfile)
    protected void btnFillProfile_Click()
    {

    }

    @OnClick(R.id.btnShowTransaction)
    protected void btnShowTransaction_Click()
    {
        startActivity(new Intent(getContext(), TransactionsActivity.class));
    }
}
