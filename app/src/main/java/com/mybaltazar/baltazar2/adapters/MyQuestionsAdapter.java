package com.mybaltazar.baltazar2.adapters;

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

import butterknife.BindView;
import butterknife.ButterKnife;
import ozaydin.serkan.com.image_zoom_view.ImageViewZoom;

class MyQuestionItemViewHolder extends RecyclerView.ViewHolder
{
    @BindView(R.id.lblLevel)        TextView lblLevel;
    @BindView(R.id.lblLessonName)   TextView lblLessonName;
    @BindView(R.id.lblDate)         TextView lblDate;
    @BindView(R.id.lblCount)        TextView lblCount;
    @BindView(R.id.imgIcon)         ImageView imgIcon;
    @BindView(R.id.lblText)         TextView lblText;
    @BindView(R.id.imgQuestionImage) ImageViewZoom imgQuestionImage;
    @BindView(R.id.btnShowAnswer)   Button btnShowAnswer;
    @BindView(R.id.layoutHaveAnswer) View layoutHaveAnswer;
    @BindView(R.id.lblStatus)       TextView lblStatus;

    MyQuestionItemViewHolder(View itemView) {
        super(itemView);
        ButterKnife.bind(this, itemView);
    }
}

public class MyQuestionsAdapter extends BaseRecyclerViewAdapter<MyQuestionItemViewHolder, Question>
{
    public MyQuestionsAdapter(BaseActivity activity, Collection<Question> list)
    {
        super(activity, list, R.layout.item_my_question);
    }

    @Override
    protected MyQuestionItemViewHolder createViewHolder(View view)
    {
        MyQuestionItemViewHolder mvh = new MyQuestionItemViewHolder(view);
        mvh.btnShowAnswer.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Question item = (Question)view.getTag();
                MainActivity activity = (MainActivity)activityRef.get();
                if(activity != null)
                    activity.openMyQuestionDetailsFragment(item);
            }
        });
        return mvh;
    }

    @Override
    protected void onBindViewHolder(MyQuestionItemViewHolder vh, Question item) {
        BaseActivity activity = activityRef.get();
        if(activity == null)
            return;
        vh.lblLevel.setText(item.getLevelTitle());
        vh.lblLessonName.setText(item.getCourseTitle());
        vh.lblText.setText(item.context);
        vh.lblDate.setText(StringUtils.getPersianDate(item.created_at));
        // TODO: set icon

        int statusStrId = 0;
        int statusColorId = 0;
        switch (item.status) {
            case unpublished:
                statusColorId = R.color.red;
                statusStrId = R.string.unpublished;
                break;
            case published:
                statusColorId = R.color.colorAccent;
                statusStrId = R.string.published;
                break;
            case answered:
                statusColorId = R.color.green;
                statusStrId = R.string.has_answer;  break;
        }
        vh.lblStatus.setText(statusStrId);
        vh.lblStatus.setTextColor(activity.getResources().getColor(statusColorId));

        vh.layoutHaveAnswer.setVisibility(item.status == Question.Status.answered ? View.VISIBLE : View.GONE);
        vh.lblCount.setText(String.valueOf(item.new_answers));
        vh.btnShowAnswer.setVisibility(item.new_answers > 0 ? View.VISIBLE : View.GONE);
        vh.btnShowAnswer.setTag(item);
        String url = activity.getString(R.string.media_base_url) + activity.getString(R.string.image_dir) + item.image;
        BaseActivity.loadImage(url, vh.imgQuestionImage);
    }
}
