package com.mybaltazar.baltazar2.adapters;

import android.support.v7.widget.RecyclerView;
import android.view.View;
import android.widget.ImageView;
import android.widget.TextView;

import com.mybaltazar.baltazar2.utils.JalaliCalendar;
import com.mybaltazar.baltazar2.R;
import com.mybaltazar.baltazar2.activities.BaseActivity;
import com.mybaltazar.baltazar2.models.Question;
import com.mybaltazar.baltazar2.utils.StringUtils;

import java.text.SimpleDateFormat;
import java.util.Calendar;
import java.util.Collection;
import java.util.Date;
import java.util.Locale;

import butterknife.BindView;
import butterknife.ButterKnife;

class QuestionItemViewHolder extends RecyclerView.ViewHolder
{
    @BindView(R.id.lblLevel)        TextView lblLevel;
    @BindView(R.id.lblLessonName)   TextView lblLessonName;
    @BindView(R.id.lblDate)         TextView lblDate;
    @BindView(R.id.lblCoinCount)    TextView lblCoinCount;
    @BindView(R.id.imgIcon)         ImageView imgIcon;
    @BindView(R.id.lblText)         TextView lblText;

    QuestionItemViewHolder(View itemView) {
        super(itemView);
        ButterKnife.bind(this, itemView);
    }
}

public class QuestionsAdapter extends BaseRecyclerViewAdapter<QuestionItemViewHolder, Question>
{
    public QuestionsAdapter(BaseActivity activity, Collection<Question> list) {
        super(activity, list, R.layout.item_question);
    }

    @Override
    protected QuestionItemViewHolder createViewHolder(View view) {
        return new QuestionItemViewHolder(view);
    }

    @Override
    protected void onBindViewHolder(QuestionItemViewHolder vh, Question item) {
        vh.lblLevel.setText(item.getLevelTitle());
        vh.lblLessonName.setText(item.getCourseTitle());
        vh.lblCoinCount.setText(String.valueOf(item.prize));
        vh.lblText.setText(item.context);
        vh.lblDate.setText(StringUtils.getPersianDate(item.created_at));

        // TODO: set icon
    }
}
