package com.mybaltazar.baltazar2.fragments;


import android.app.ProgressDialog;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.media.AudioManager;
import android.media.MediaPlayer;
import android.net.Uri;
import android.os.Build;
import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.v7.widget.CardView;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.TextView;
import android.widget.Toast;
import android.widget.VideoView;

import com.mybaltazar.baltazar2.R;
import com.mybaltazar.baltazar2.activities.BaseActivity;
import com.mybaltazar.baltazar2.models.Answer;
import com.mybaltazar.baltazar2.models.Question;
import com.mybaltazar.baltazar2.utils.StringUtils;
import com.mybaltazar.baltazar2.webservices.CommonData;
import com.mybaltazar.baltazar2.webservices.CommonResponse;
import com.mybaltazar.baltazar2.webservices.DataResponse;
import com.mybaltazar.baltazar2.webservices.RetryableCallback;
import com.mybaltazar.baltazar2.webservices.Services;
import com.theartofdev.edmodo.cropper.CropImage;
import com.theartofdev.edmodo.cropper.CropImageView;

import java.io.File;
import java.io.IOException;

import butterknife.BindView;
import butterknife.ButterKnife;
import butterknife.OnClick;
import okhttp3.MediaType;
import okhttp3.MultipartBody;
import okhttp3.RequestBody;
import ozaydin.serkan.com.image_zoom_view.ImageViewZoom;
import retrofit2.Call;
import retrofit2.Response;

import static android.app.Activity.RESULT_OK;

public class QuestionDetailFragment extends BaseFragment
{
    @BindView(R.id.cardQuestion)        CardView cardQuestion;
    @BindView(R.id.imgAvatar)           ImageView imgAvatar;
    @BindView(R.id.lblUserName)         TextView lblUserName;
    @BindView(R.id.layoutGradeLesson)   View layoutGradeLesson;
    @BindView(R.id.lblCourseText)       TextView lblCourseText;
    @BindView(R.id.lblCourseName)       TextView lblCourseName;
    @BindView(R.id.lblGrade)            TextView lblGrade;
    @BindView(R.id.lblDate)             TextView lblDate;
    @BindView(R.id.imgQuestionImage)    ImageViewZoom imgQuestionImage;
    @BindView(R.id.lblQuestionText)     TextView lblQuestionText;
    @BindView(R.id.imgAnswerImage)      ImageView imgAnswerImage;
    @BindView(R.id.videoView)           VideoView videoView;
    @BindView(R.id.layoutVoice)         View layoutVoice;
    @BindView(R.id.txtAnswer)           EditText txtAnswer;

    private File answerImageFile = null;
    private static final float IMG_ANSWER_HEIGHT_AFTER_CHOOSE_DP = 250;

    public QuestionDetailFragment() { }

    @Override
    public int getTitleId() {
        return R.string.question_details;
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.fragment_question_detail, container, false);
        ButterKnife.bind(this, view);
        loadUI((Question)getArguments().getSerializable("item"));
        return view;
    }

    private void loadUI(Question item)
    {
        CommonData commonData = ((BaseActivity)getActivity()).loadCommonData(false, null);
        lblUserName.setText(item.userName);
        layoutGradeLesson.setVisibility(item.fromBaltazar ? View.GONE : View.VISIBLE);
        cardQuestion.setCardBackgroundColor(getResources().getColor(item.fromBaltazar ?  R.color.lightGreen : R.color.white));
        lblQuestionText.setBackgroundColor(getResources().getColor(item.fromBaltazar ? R.color.midGreen : R.color.gray));
        imgAvatar.setVisibility(item.fromBaltazar ? View.GONE : View.VISIBLE);
        if (item.courseId == null)
        {
            lblCourseName.setVisibility(View.GONE);
            lblCourseText.setVisibility(View.GONE);
        }
        else
            lblCourseName.setText(commonData.getCourseName(item.courseId));

        String[] grades = getResources().getStringArray(R.array.grades);
        lblGrade.setText(grades[item.grade-1]);

        lblDate.setText(StringUtils.getPersianDateString(item.createDate));
        lblQuestionText.setText(item.text);

        if (item.hasImage)
        {
            BaseActivity activity = (BaseActivity)getActivity();
            if(activity != null) {
                BaseActivity.loadImage(activity.getImageUrlById(item.id), imgQuestionImage);
                imgQuestionImage.setVisibility(View.VISIBLE);
            }
        }
        else
            imgQuestionImage.setVisibility(View.GONE);

        imgAnswerImage.setVisibility(!item.fromBaltazar || item.allowUploadOnAnswer ? View.VISIBLE : View.GONE);

        if (item.hasVideo)
        {
            Uri url = Uri.parse(getString(R.string.media_base_url) + getString(R.string.video_dir) + item.id + ".mp4");
            videoView.setVideoURI(url);
            videoView.setOnPreparedListener(new MediaPlayer.OnPreparedListener() {
                @Override
                public void onPrepared(MediaPlayer mp) {
                    mp.setLooping(true);
                    videoView.start();
                }
            });
            videoView.setVisibility(View.VISIBLE);
        }
        else
            videoView.setVisibility(View.GONE);

        if(item.hasVoice)
        {
            layoutVoice.setVisibility(View.VISIBLE);
            layoutVoice.setTag(item.id);
        }
        else
            layoutVoice.setVisibility(View.GONE);
    }

    @OnClick(R.id.imgAnswerImage)
    protected void imgAnswerImage_Click()
    {
        if (Build.VERSION.SDK_INT >= 23 && getActivity().checkSelfPermission(android.Manifest.permission.CAMERA) != PackageManager.PERMISSION_GRANTED)
            requestPermissions(new String[]{android.Manifest.permission.CAMERA}, 10);
        else
            openCamera();
    }

    @Override
    public void onRequestPermissionsResult(int requestCode, @NonNull String[] permissions, @NonNull int[] grantResults)
    {
        super.onRequestPermissionsResult(requestCode, permissions, grantResults);
        if(requestCode == 10 && grantResults[0] == PackageManager.PERMISSION_GRANTED) {
            openCamera();
        }
    }

    private void openCamera()
    {
        CropImage.activity()
                .setGuidelines(CropImageView.Guidelines.ON)
//                .setMaxCropResultSize(1280, 720)
                .start(getContext(), this);
    }

    @Override
    public void onActivityResult(int requestCode, int resultCode, Intent data)
    {
        if (requestCode == CropImage.CROP_IMAGE_ACTIVITY_REQUEST_CODE) {
            CropImage.ActivityResult result = CropImage.getActivityResult(data);
            if (resultCode == RESULT_OK) {
                Uri resultUri = result.getUri();
                imgAnswerImage.setImageURI(resultUri);
                answerImageFile = new File(resultUri.getPath());
                float density = getContext().getResources().getDisplayMetrics().density;
                int height = Math.round(density * IMG_ANSWER_HEIGHT_AFTER_CHOOSE_DP);
                imgAnswerImage.setLayoutParams(new LinearLayout.LayoutParams(ViewGroup.LayoutParams.MATCH_PARENT, height));
            }
            else if (resultCode == CropImage.CROP_IMAGE_ACTIVITY_RESULT_ERROR_CODE) {
                Exception error = result.getError();
                error.printStackTrace();
                Toast.makeText(getContext(), error.getMessage(), Toast.LENGTH_LONG).show();
            }
        }
    }

    MediaPlayer mediaPlayer;

    @OnClick(R.id.layoutVoice)
    protected void layoutVoice_Click()
    {
        String voiceName = (String)layoutVoice.getTag();
        Uri url = Uri.parse(getString(R.string.media_base_url) + getString(R.string.voice_dir) + voiceName + ".mp3");
        mediaPlayer = new MediaPlayer();
        mediaPlayer.setAudioStreamType(AudioManager.STREAM_MUSIC);
        try {
            mediaPlayer.setDataSource(getContext(), url);
            mediaPlayer.prepare();
            mediaPlayer.start();
        }
        catch (IOException e) {
            e.printStackTrace();
            Toast.makeText(getContext(), R.string.voice_error, Toast.LENGTH_LONG).show();
        }
    }

    @OnClick(R.id.btnSend)
    protected void btnSend_Click()
    {
        final ProgressDialog progress = ((BaseActivity)getActivity()).showProgress(R.string.sendingAnswer);
        final BaseActivity activity = (BaseActivity)getActivity();
        Question question = (Question)getArguments().getSerializable("item");

        Answer answer = new Answer();
        answer.text = txtAnswer.getText().toString();
        answer.questionId = question.id;
        answer.toBaltazarQuestion = question.fromBaltazar;

        Call<DataResponse<Answer>>  call = activity.createWebService(Services.class).publishAnswer(BaseActivity.getToken(), answer);
        call.enqueue(new RetryableCallback<DataResponse<Answer>>(activity, progress)
        {
            @Override
            public void onFinalSuccess(DataResponse<Answer> response) {
                if(answerImageFile == null)
                {
                    Toast.makeText(getContext(), R.string.sendSuccess, Toast.LENGTH_SHORT).show();
                    activity.onBackPressed();
                }
                else {
                    uploadImage(activity.showProgress(), response.data);
                }
            }
        });
    }

    private void uploadImage(final ProgressDialog progress, final Answer data)
    {
        final BaseActivity activity = (BaseActivity)getActivity();
        Call<CommonResponse> call = activity.createWebService(Services.class).uploadAnswerImage(BaseActivity.getToken(), data.id,
                MultipartBody.Part.createFormData("image", answerImageFile.getName(),
                        RequestBody.create(MediaType.parse("image/*"), answerImageFile)));
        call.enqueue(new RetryableCallback<CommonResponse>(activity, progress)
        {
            @Override
            public void onFinalSuccess(CommonResponse response) {
                Toast.makeText(getContext(), R.string.sendSuccess, Toast.LENGTH_SHORT).show();
                activity.onBackPressed();
            }
        });
    }
}
