package com.mybaltazar.baltazar2.adapters;

import android.support.v7.widget.RecyclerView;
import android.view.View;
import android.widget.ImageView;
import android.widget.TextView;

import com.mybaltazar.baltazar2.R;
import com.mybaltazar.baltazar2.activities.BaseActivity;
import com.mybaltazar.baltazar2.models.Question;
import com.mybaltazar.baltazar2.utils.StringUtils;

import java.util.Collection;

import butterknife.BindView;
import butterknife.ButterKnife;
import ozaydin.serkan.com.image_zoom_view.ImageViewZoom;

class QuestionItemViewHolder extends RecyclerView.ViewHolder
{
    @BindView(R.id.lblLevel)        TextView lblLevel;
    @BindView(R.id.lblLessonName)   TextView lblLessonName;
    @BindView(R.id.lblDate)         TextView lblDate;
    @BindView(R.id.lblCount)        TextView lblCount;
    @BindView(R.id.imgIcon)         ImageView imgIcon;
    @BindView(R.id.lblText)         TextView lblText;

    QuestionItemViewHolder(View itemView) {
        super(itemView);
        ButterKnife.bind(this, itemView);
    }
}

class MyQuestionItemViewHolder extends QuestionItemViewHolder
{
    @BindView(R.id.imgQuestionImage)
    ImageViewZoom imgQuestionImage;

    MyQuestionItemViewHolder(View itemView) {
        super(itemView);
    }
}

public class QuestionsAdapter extends BaseRecyclerViewAdapter<QuestionItemViewHolder, Question>
{
    private final boolean myQuestion;

    public QuestionsAdapter(BaseActivity activity, Collection<Question> list, boolean myQuestion) {
        super(activity, list, myQuestion ? R.layout.item_my_question : R.layout.item_question);
        this.myQuestion = myQuestion;
    }

    @Override
    protected QuestionItemViewHolder createViewHolder(View view) {
        if(myQuestion)
            return new MyQuestionItemViewHolder(view);
        return new QuestionItemViewHolder(view);
    }

    @Override
    protected void onBindViewHolder(QuestionItemViewHolder vh, Question item) {
        vh.lblLevel.setText(item.getLevelTitle());
        vh.lblLessonName.setText(item.getCourseTitle());
        vh.lblText.setText(item.context);
        vh.lblDate.setText(StringUtils.getPersianDate(item.created_at));
        // TODO: set icon

        if(myQuestion) {
            MyQuestionItemViewHolder mvh = (MyQuestionItemViewHolder)vh;
            mvh.lblCount.setText(String.valueOf(item.new_answers));
            BaseActivity activity = activityRef.get();
            if(activity != null) {
                String url = activity.getString(R.string.media_base_url) + activity.getString(R.string.image_dir) + item.image;
                BaseActivity.loadImage(url, mvh.imgQuestionImage);
            }
        }
        else {
            vh.lblCount.setText(String.valueOf(item.prize));
        }
    }
}
