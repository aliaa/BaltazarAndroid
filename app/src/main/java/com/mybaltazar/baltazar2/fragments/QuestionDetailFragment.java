package com.mybaltazar.baltazar2.fragments;


import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import com.mybaltazar.baltazar2.R;

public class QuestionDetailFragment extends BaseFragment
{
    public QuestionDetailFragment() { }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        return inflater.inflate(R.layout.fragment_question_detail, container, false);
    }

}