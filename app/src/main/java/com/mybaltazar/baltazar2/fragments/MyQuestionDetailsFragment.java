package com.mybaltazar.baltazar2.fragments;

import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import com.mybaltazar.baltazar2.R;

public class MyQuestionDetailsFragment extends BaseFragment {

    public MyQuestionDetailsFragment() { }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        return inflater.inflate(R.layout.fragment_my_question_details, container, false);
    }
}
