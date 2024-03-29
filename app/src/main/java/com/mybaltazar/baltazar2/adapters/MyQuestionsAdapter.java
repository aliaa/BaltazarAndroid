package com.mybaltazar.baltazar2.adapters;

import android.support.v7.widget.RecyclerView;
import android.view.View;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.TextView;

import com.mybaltazar.baltazar2.R;
import com.mybaltazar.baltazar2.activities.BaseActivity;
import com.mybaltazar.baltazar2.activities.MainActivity;
import com.mybaltazar.baltazar2.events.DeleteQuestionClickEvent;
import com.mybaltazar.baltazar2.models.BaseEntity;
import com.mybaltazar.baltazar2.models.BaseUserContent;
import com.mybaltazar.baltazar2.models.Question;
import com.mybaltazar.baltazar2.utils.StringUtils;

import org.greenrobot.eventbus.EventBus;

import java.util.List;
import java.util.Map;

import butterknife.BindView;
import butterknife.ButterKnife;
import ozaydin.serkan.com.image_zoom_view.ImageViewZoom;

class MyQuestionItemViewHolder extends RecyclerView.ViewHolder
{
    @BindView(R.id.lblGrade)        TextView lblGrade;
    @BindView(R.id.lblCourseName)   TextView lblCourseName;
    @BindView(R.id.lblDate)         TextView lblDate;
    @BindView(R.id.lblCount)        TextView lblCount;
    @BindView(R.id.btnDelete)       ImageView btnDelete;
    @BindView(R.id.lblText)         TextView lblText;
    @BindView(R.id.imgQuestionImage) ImageViewZoom imgQuestionImage;
    @BindView(R.id.btnShowAnswer)   Button btnShowAnswer;
    @BindView(R.id.layoutAnswersCount) View layoutAnswersCount;
    @BindView(R.id.lblStatus)       TextView lblStatus;

    MyQuestionItemViewHolder(View itemView) {
        super(itemView);
        ButterKnife.bind(this, itemView);
    }
}

public class MyQuestionsAdapter extends BaseRecyclerViewAdapter<MyQuestionItemViewHolder, Question>
{
    private final String[] grades;
    private final Map<String, String> courses;

    public MyQuestionsAdapter(BaseActivity activity, List<Question> list, Map<String, String> courses)
    {
        super(activity, list, R.layout.item_my_question);
        this.grades = activity.getResources().getStringArray(R.array.grades);
        this.courses = courses;
    }

    @Override
    protected MyQuestionItemViewHolder createViewHolder(View view)
    {
        MyQuestionItemViewHolder vh = new MyQuestionItemViewHolder(view);
        vh.btnShowAnswer.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Question item = (Question)view.getTag();
                MainActivity activity = (MainActivity)activityRef.get();
                if(activity != null)
                    activity.openMyQuestionDetailsFragment(item);
            }
        });
        vh.btnDelete.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Question item = (Question)view.getTag();
                EventBus.getDefault().post(new DeleteQuestionClickEvent(item));
            }
        });
        return vh;
    }

    @Override
    protected void onBindViewHolder(MyQuestionItemViewHolder vh, Question item) {
        BaseActivity activity = activityRef.get();
        if(activity == null)
            return;
        vh.lblGrade.setText(grades[item.grade-1]);
        if(courses.containsKey(item.courseId))
            vh.lblCourseName.setText(courses.get(item.courseId));
        vh.lblText.setText(item.text);
        vh.lblDate.setText(StringUtils.getPersianDateString(item.createDate));
        // TODO: set icon

        int statusStrId = 0;
        int statusColorId = 0;
        if(item.answers.size() == 0) {
            switch (item.publishStatus) {
                case WaitForApprove:
                    statusColorId = R.color.red;
                    statusStrId = R.string.unpublished;
                    break;
                case Published:
                    statusColorId = R.color.colorAccent;
                    statusStrId = R.string.published;
                    break;
                case Rejected:
                    statusColorId = R.color.red;
                    statusStrId = R.string.rejected;
                    break;
            }
            vh.btnShowAnswer.setBackgroundColor(activity.getResources().getColor(R.color.gray));
            vh.btnShowAnswer.setTextColor(activity.getResources().getColor(R.color.black));
        }
        else {
            statusColorId = R.color.green;
            statusStrId = R.string.has_answer;
            vh.btnShowAnswer.setBackgroundColor(activity.getResources().getColor(R.color.colorAccent));
            vh.btnShowAnswer.setTextColor(activity.getResources().getColor(R.color.white));
        }
        vh.lblStatus.setText(statusStrId);
        vh.lblStatus.setTextColor(activity.getResources().getColor(statusColorId));

        vh.layoutAnswersCount.setVisibility(item.answers.size() > 0 ? View.VISIBLE : View.GONE);
        vh.lblCount.setText(String.valueOf(item.answers.size()));
        vh.btnShowAnswer.setVisibility(item.answers.size() > 0 || item.acceptedAnswer != null ? View.VISIBLE : View.GONE);
        vh.btnShowAnswer.setTag(item);
        if(item.hasImage) {
            String url = activity.getImageUrlById(item.id);
            BaseActivity.loadImage(url, vh.imgQuestionImage);
        }
        vh.imgQuestionImage.setVisibility(item.hasImage ? View.VISIBLE : View.GONE);

        vh.btnDelete.setVisibility(item.publishStatus == BaseUserContent.PublishStatusEnum.WaitForApprove ? View.VISIBLE : View.INVISIBLE);
        vh.btnDelete.setTag(item);
    }
}
