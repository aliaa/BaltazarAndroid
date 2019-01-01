package com.mybaltazar.baltazar2.fragments;


import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.support.v4.widget.SwipeRefreshLayout;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import com.mybaltazar.baltazar2.R;

import butterknife.BindView;
import butterknife.ButterKnife;
import butterknife.OnClick;

public class QAFragment extends Fragment implements SwipeRefreshLayout.OnRefreshListener {
    public QAFragment() { }

    @BindView(R.id.recycler)
    RecyclerView recycler;

    @BindView(R.id.swipe)
    SwipeRefreshLayout swipe;

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        View root = inflater.inflate(R.layout.fragment_qa, container, false);
        ButterKnife.bind(this, root);
        swipe.setOnRefreshListener(this);

        return root;
    }

    @OnClick(R.id.btnAdd)
    protected void btnAdd_Click()
    {

    }

    @Override
    public void onRefresh() {

    }
}
