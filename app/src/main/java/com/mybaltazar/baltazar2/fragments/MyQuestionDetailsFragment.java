package com.mybaltazar.baltazar2.fragments;

import android.app.ProgressDialog;
import android.content.DialogInterface;
import android.os.Bundle;
import android.support.v7.app.AlertDialog;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;
import android.widget.Toast;

import com.mybaltazar.baltazar2.R;
import com.mybaltazar.baltazar2.activities.BaseActivity;
import com.mybaltazar.baltazar2.activities.MainActivity;
import com.mybaltazar.baltazar2.models.Answer;
import com.mybaltazar.baltazar2.models.Question;
import com.mybaltazar.baltazar2.utils.StringUtils;
import com.mybaltazar.baltazar2.webservices.CommonResponse;
import com.mybaltazar.baltazar2.webservices.RetryableCallback;
import com.mybaltazar.baltazar2.webservices.Services;

import butterknife.BindView;
import butterknife.ButterKnife;
import butterknife.OnClick;
import ozaydin.serkan.com.image_zoom_view.ImageViewZoom;
import retrofit2.Call;

public class MyQuestionDetailsFragment extends BaseFragment
{
    @BindView(R.id.lblUserName)             TextView lblUserName;
    @BindView(R.id.lblDate)                 TextView lblDate;
    @BindView(R.id.imgAnswerImage)          ImageViewZoom imgAnswerImage;
    @BindView(R.id.lblAnswerDescription)    TextView lblAnswerDescription;
    @BindView(R.id.layoutResponseButtons)   View layoutResponseButtons;

    Question question;
    Answer answer;
    int index = 0;

    public MyQuestionDetailsFragment() { }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState)
    {
        View view = inflater.inflate(R.layout.fragment_my_question_details, container, false);
        ButterKnife.bind(this, view);
        final MainActivity activity = (MainActivity)getActivity();
        question = (Question)getArguments().getSerializable("item");
        if((question.answers == null || question.answers.size() == 0) && question.acceptedAnswer == null)
        {
            Toast.makeText(getContext(), "جواب موجود نیست!", Toast.LENGTH_SHORT).show();
            activity.onBackPressed();
            return view;
        }
        if(question.answers.size() > 0) {
            answer = question.answers.get(0);
            layoutResponseButtons.setVisibility(View.VISIBLE);
        }
        else {
            answer = question.acceptedAnswer;
            layoutResponseButtons.setVisibility(View.GONE);
        }
        loadUI(answer);
        return view;
    }

    private void loadUI(Answer answer)
    {
        lblUserName.setText(answer.userName);
        lblDate.setText(StringUtils.getPersianDateString(answer.createDate));
        lblAnswerDescription.setText(answer.text);
        imgAnswerImage.setVisibility(answer.hasImage ? View.VISIBLE : View.GONE);
        if(answer.hasImage)
        {
            BaseActivity activity = (BaseActivity)getActivity();
            String url = activity.getImageUrlById(answer.id);
            BaseActivity.loadImage(url, imgAnswerImage);
        }
    }

    @OnClick({R.id.btnCorrectAnswer, R.id.btnUnclearAnswer, R.id.btnIrrelevantAnswer})
    protected void responseButtons_Click(View button)
    {
        Answer.QuestionerResponseEnum questionerResponse;
        switch (button.getId())
        {
            case R.id.btnCorrectAnswer:
                questionerResponse = Answer.QuestionerResponseEnum.Accepted;
                break;
            case R.id.btnUnclearAnswer:
                questionerResponse = Answer.QuestionerResponseEnum.Rejected;
                break;
            case R.id.btnIrrelevantAnswer:
                questionerResponse = Answer.QuestionerResponseEnum.Reported;
                break;
            default:
                return;
        }
        sendResponse(questionerResponse);
    }

    private void sendResponse(final Answer.QuestionerResponseEnum questionerResponse)
    {
        int msgId;
        switch (questionerResponse) {
            case Accepted:
                msgId = R.string.sure_to_approve_answer;
                break;
            case Rejected:
                msgId = R.string.sure_to_reject_answer;
                break;
            case Reported:
                msgId = R.string.sure_to_report_answer;
                break;
            default:
                return;
        }
        new AlertDialog.Builder(getContext())
                .setMessage(msgId)
                .setNegativeButton(R.string.cancel, null)
                .setPositiveButton(R.string.yes, new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialog, int which) {
                        callResponse(questionerResponse);
                    }
                })
                .create().show();
    }

    private void callResponse(final Answer.QuestionerResponseEnum questionerResponse)
    {
        BaseActivity activity = (BaseActivity)getActivity();
        final ProgressDialog progress = activity.showProgress();
        Call<CommonResponse> call = activity.createWebService(Services.class).setAnswerResponse(
                BaseActivity.getToken(), question.id, answer.id, questionerResponse);
        call.enqueue(new RetryableCallback<CommonResponse>(activity, progress)
        {
            @Override
            public void onFinalSuccess(CommonResponse data)
            {
                BaseActivity activity = (BaseActivity)getActivity();
                if(activity != null) {
                    switch (questionerResponse) {
                        case Accepted:
                            activity.onBackPressed();
                            break;
                        default:
                            index++;
                            if(question.answers.size() > index)
                            {
                                answer = question.answers.get(index);
                                loadUI(answer);
                            }
                            else
                                activity.onBackPressed();
                    }
                }
            }
        });
    }


}
