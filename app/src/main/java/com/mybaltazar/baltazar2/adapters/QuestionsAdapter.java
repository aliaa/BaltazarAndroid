package com.mybaltazar.baltazar2.adapters;

import android.support.annotation.NonNull;
import android.support.v7.widget.RecyclerView;
import android.view.View;

import com.mybaltazar.baltazar2.R;
import com.mybaltazar.baltazar2.activities.BaseActivity;
import com.mybaltazar.baltazar2.models.Question;

import java.util.Collection;

import butterknife.ButterKnife;

class QuestionItemViewHolder extends RecyclerView.ViewHolder
{

    public QuestionItemViewHolder(View itemView) {
        super(itemView);
        ButterKnife.bind(this, itemView);
    }
}

public class QuestionsAdapter extends BaseRecyclerViewAdapter<QuestionItemViewHolder, Question> implements View.OnClickListener {
    public QuestionsAdapter(BaseActivity activity, Collection<Question> list) {
        super(activity, list, R.layout.item_question);
    }

    @Override
    protected QuestionItemViewHolder createViewHolder(View view) {
        view.setOnClickListener(this);
        return  new QuestionItemViewHolder(view);
    }

    @Override
    public void onBindViewHolder(@NonNull QuestionItemViewHolder holder, int position) {
        BaseActivity activity = activityRef.get();
        if(activity == null)
            return;
        Question item = list.get(position);

    }

    @Override
    public void onClick(View v) {
        BaseActivity activity = activityRef.get();
        if(activity == null)
            return;

    }
}
