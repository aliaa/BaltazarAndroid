package com.mybaltazar.baltazar2.fragments;

import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import com.mybaltazar.baltazar2.R;

public class NewQuestionFragment extends BaseFragment
{
    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        return inflater.inflate(R.layout.fragment_new_question, container, false);
    }

    @Override
    public int getTitleId() {
        return R.string.new_question;
    }
}
