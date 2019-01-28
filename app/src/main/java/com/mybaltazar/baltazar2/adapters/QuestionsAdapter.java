package com.mybaltazar.baltazar2.adapters;

import android.support.v7.widget.CardView;
import android.support.v7.widget.RecyclerView;
import android.view.View;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.TextView;

import com.mybaltazar.baltazar2.R;
import com.mybaltazar.baltazar2.activities.BaseActivity;
import com.mybaltazar.baltazar2.activities.MainActivity;
import com.mybaltazar.baltazar2.models.Question;
import com.mybaltazar.baltazar2.utils.StringUtils;

import java.util.Collection;
import java.util.List;
import java.util.Map;

import butterknife.BindView;
import butterknife.ButterKnife;
import ozaydin.serkan.com.image_zoom_view.ImageViewZoom;

class QuestionItemViewHolder extends RecyclerView.ViewHolder
{
    @BindView(R.id.cardView)        CardView cardView;
    @BindView(R.id.lblBaltazarQuestion) TextView lblBaltazarQuestion;
    @BindView(R.id.lblGradeText)    TextView lblGradeText;
    @BindView(R.id.lblGrade)        TextView lblGrade;
    @BindView(R.id.lblCourseText)   TextView lblCourseText;
    @BindView(R.id.lblCourseName)   TextView lblCourseName;
    @BindView(R.id.lblDate)         TextView lblDate;
    @BindView(R.id.lblCount)        TextView lblCount;
    @BindView(R.id.imgIcon)         ImageView imgIcon;
    @BindView(R.id.lblText)         TextView lblText;

    QuestionItemViewHolder(View itemView) {
        super(itemView);
        ButterKnife.bind(this, itemView);
    }
}



public class QuestionsAdapter extends BaseRecyclerViewAdapter<QuestionItemViewHolder, Question>
{
    private final String[] grades;
    private final Map<String, String> courses;

    public QuestionsAdapter(BaseActivity activity, List<Question> list, Map<String, String> courses) {
        super(activity, list, R.layout.item_question);
        this.grades = activity.getResources().getStringArray(R.array.grades);
        this.courses = courses;
    }

    @Override
    protected QuestionItemViewHolder createViewHolder(View view) {
        return new QuestionItemViewHolder(view);
    }

    @Override
    protected void onBindViewHolder(QuestionItemViewHolder vh, Question item) {
        BaseActivity activity = activityRef.get();
        if (activity == null)
            return;

        vh.lblBaltazarQuestion.setVisibility(item.fromBaltazar ? View.VISIBLE : View.GONE);
        vh.lblGradeText.setVisibility(item.fromBaltazar ? View.GONE : View.VISIBLE);
        vh.lblGrade.setVisibility(item.fromBaltazar ? View.GONE : View.VISIBLE);
        vh.lblCourseName.setVisibility(item.fromBaltazar ? View.GONE : View.VISIBLE);
        vh.lblCourseText.setVisibility(item.fromBaltazar ? View.GONE : View.VISIBLE);

        vh.lblGrade.setText(grades[item.grade-1]);
        if(courses.containsKey(item.courseId))
            vh.lblCourseName.setText(courses.get(item.courseId));
        vh.lblText.setText(item.text);
        vh.lblDate.setText(StringUtils.getPersianDate(item.createDate));
        // TODO: set icon


        vh.lblCount.setText(String.valueOf(item.prize));
        if(item.fromBaltazar)
            vh.cardView.setCardBackgroundColor(activity.getResources().getColor(R.color.lightGreen));
        else if (item.hot)
            vh.cardView.setCardBackgroundColor(activity.getResources().getColor(R.color.yellow));
    }
}
