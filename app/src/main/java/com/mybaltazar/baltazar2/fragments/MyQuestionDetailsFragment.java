package com.mybaltazar.baltazar2.fragments;

import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;

import com.mybaltazar.baltazar2.R;
import com.mybaltazar.baltazar2.models.Question;

import butterknife.BindView;
import butterknife.ButterKnife;
import butterknife.OnClick;
import ozaydin.serkan.com.image_zoom_view.ImageViewZoom;

public class MyQuestionDetailsFragment extends BaseFragment
{
    @BindView(R.id.lblUserName)             TextView lblUserName;
    @BindView(R.id.lblDate)                 TextView lblDate;
    @BindView(R.id.imgAnswerImage)          ImageViewZoom imgAnswerImage;
    @BindView(R.id.lblAnswerDescription)    TextView lblAnswerDescription;


    public MyQuestionDetailsFragment() { }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.fragment_my_question_details, container, false);
        ButterKnife.bind(this, view);
        loadFromObject((Question)getArguments().getSerializable("item"));
        return view;
    }

    private void loadFromObject(Question item)
    {
//        item.answers
    }

    @OnClick(R.id.btnCorrectAnswer)
    protected void btnCorrectAnswer_Click()
    {

    }

    @OnClick(R.id.btnUnclearAnswer)
    protected void btnUnclearAnswer_Click()
    {

    }

    @OnClick(R.id.btnIrrelevantAnswer)
    protected void btnIrrelevantAnswer_Click()
    {

    }
}
