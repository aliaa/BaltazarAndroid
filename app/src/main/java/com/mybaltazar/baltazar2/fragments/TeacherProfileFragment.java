package com.mybaltazar.baltazar2.fragments;

import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import com.mybaltazar.baltazar2.R;

public class TeacherProfileFragment extends Fragment
{
    public TeacherProfileFragment() { }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState)
    {
        return inflater.inflate(R.layout.fragment_teacher_profile, container, false);
    }

}
