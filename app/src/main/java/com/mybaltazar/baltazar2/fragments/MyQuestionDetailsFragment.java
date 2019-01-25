package com.mybaltazar.baltazar2.fragments;

import android.app.ProgressDialog;
import android.os.Bundle;
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
import retrofit2.Response;

public class MyQuestionDetailsFragment extends BaseFragment
{
    @BindView(R.id.lblUserName)             TextView lblUserName;
    @BindView(R.id.lblDate)                 TextView lblDate;
    @BindView(R.id.imgAnswerImage)          ImageViewZoom imgAnswerImage;
    @BindView(R.id.lblAnswerDescription)    TextView lblAnswerDescription;

    Question question;
    Answer answer;
    int index = 0;

    public MyQuestionDetailsFragment() { }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.fragment_my_question_details, container, false);
        ButterKnife.bind(this, view);
        final MainActivity activity = (MainActivity)getActivity();
        question = (Question)getArguments().getSerializable("item");
        if(question.answers == null || question.answers.size() == 0)
        {
            Toast.makeText(getContext(), "جواب موجود نیست!", Toast.LENGTH_SHORT).show();
            activity.onBackPressed();
            return view;
        }
        answer = question.answers.get(0);
        loadUI(answer);
        return view;
    }

    private void loadUI(Answer answer)
    {
        lblUserName.setText(answer.userName);
        lblDate.setText(StringUtils.getPersianDate(answer.createDate));
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
        BaseActivity activity = (BaseActivity)getActivity();
        final ProgressDialog progress = activity.showProgress();
        Call<CommonResponse> call = activity.createWebService(Services.class).setAnswerResponse(
                BaseActivity.getToken(), question.id, answer.id, questionerResponse);
        call.enqueue(new RetryableCallback<CommonResponse>(call) {
            @Override
            public void onFinalFailure(Call<CommonResponse> call, Throwable t) {
                Toast.makeText(getContext(), R.string.no_network, Toast.LENGTH_SHORT).show();
                progress.dismiss();
            }

            @Override
            public void onResponse(Call<CommonResponse> call, Response<CommonResponse> response) {
                progress.dismiss();
                CommonResponse resp = response.body();
                if(resp == null)
                    onFinalFailure(call, null);
                else if(!resp.success)
                    Toast.makeText(getContext(), resp.message, Toast.LENGTH_LONG).show();
                else
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
            }
        });
    }
}
