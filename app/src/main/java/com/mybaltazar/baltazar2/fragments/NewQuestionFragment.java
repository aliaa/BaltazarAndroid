package com.mybaltazar.baltazar2.fragments;

import android.app.ProgressDialog;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.net.Uri;
import android.os.Build;
import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.Spinner;
import android.widget.Toast;

import com.mybaltazar.baltazar2.R;
import com.mybaltazar.baltazar2.activities.BaseActivity;
import com.mybaltazar.baltazar2.models.Course;
import com.mybaltazar.baltazar2.models.CourseSection;
import com.mybaltazar.baltazar2.models.StudyField;
import com.mybaltazar.baltazar2.webservices.CommonData;
import com.theartofdev.edmodo.cropper.CropImage;
import com.theartofdev.edmodo.cropper.CropImageView;

import java.io.File;
import java.util.ArrayList;
import java.util.List;

import butterknife.BindView;
import butterknife.ButterKnife;
import butterknife.OnClick;
import eu.inmite.android.lib.validations.form.annotations.NotEmpty;
import okhttp3.MediaType;
import okhttp3.MultipartBody;
import okhttp3.RequestBody;
import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;

import static android.app.Activity.RESULT_OK;

public class NewQuestionFragment extends BaseFragment
{
    @BindView(R.id.spinnerGrade)        Spinner spinnerGrade;
    @BindView(R.id.spinnerStudyField)   Spinner spinnerStudyField;
    @BindView(R.id.spinnerCourse)       Spinner spinnerCourse;
    @BindView(R.id.spinnerSection)      Spinner spinnerSection;
    @BindView(R.id.img)                 ImageView img;

    @NotEmpty(messageId = R.string.is_empty)
    @BindView(R.id.txtDescription)
    EditText txtDescription;

    private File imageFile;

    @Override
    public int getTitleId() {
        return R.string.new_question;
    }

    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.fragment_new_question, container, false);
        ButterKnife.bind(this, view);
        loadSpinners();
        return view;
    }

    private void loadSpinners()
    {
        CommonData commonData = BaseActivity.loadCache(getContext(), "common", CommonData.class);
        if(commonData != null)
            loadSpinners(commonData);
        loadFromNetwork(commonData == null);
    }

    private void loadSpinners(final CommonData data)
    {
        spinnerGrade.setAdapter(new ArrayAdapter<>(getContext(), android.R.layout.simple_spinner_dropdown_item,
                getResources().getStringArray(R.array.grades)));

        spinnerGrade.setOnItemClickListener(new AdapterView.OnItemClickListener() {
            @Override
            public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
                spinnerStudyField.setVisibility(position >= 10 ? View.VISIBLE : View.GONE);
                setCoursesSpinnerAdapter(data.courses, data.sections);
            }
        });

        setSpinnerAdapter(spinnerStudyField, data.studyFields);
        spinnerStudyField.setOnItemClickListener(new AdapterView.OnItemClickListener() {
            @Override
            public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
                setCoursesSpinnerAdapter(data.courses, data.sections);
            }
        });
    }

    private void setCoursesSpinnerAdapter(List<Course> allCourses, final List<CourseSection> allSections)
    {
        int grade = spinnerGrade.getSelectedItemPosition()+1;
        StudyField studyField = (StudyField) spinnerStudyField.getSelectedItem();
        List<Course> courses = new ArrayList<>();
        for(Course c : allCourses) {
            if(grade == c.grade && (grade < 10 || studyField.id.equals(c.studyFieldId)))
                courses.add(c);
        }
        setSpinnerAdapter(spinnerCourse, courses);
        setSectionsSpinnerAdapter(allSections);
    }

    private void setSectionsSpinnerAdapter(List<CourseSection> allSections)
    {

    }

    private <T> void setSpinnerAdapter(Spinner spinner, List<T> list)
    {
        spinner.setAdapter(new ArrayAdapter<T>(getContext(), android.R.layout.simple_spinner_dropdown_item, list));
    }

    private void loadFromNetwork(final boolean showDialog)
    {
        final BaseActivity activity = (BaseActivity)getActivity();
        final ProgressDialog dialog = showDialog ? activity.showProgress() : null;
        Call<ServerResponse> call = activity.createWebService(Requests.class).registerTools();
        call.enqueue(new RetryableCallback<ServerResponse>(call) {
            @Override
            public void onResponse(Call<ServerResponse> call, Response<ServerResponse> response) {
                if (dialog != null)
                    dialog.dismiss();
                ServerResponse data = response.body();
                if (data != null) {
                    if (showDialog)
                        loadSpinners(data);
                    BaseActivity.cacheItem(getContext(), data, "tools");
                }
                else if(data != null)
                    Toast.makeText(getContext(), data.message, Toast.LENGTH_LONG).show();
                else
                    Toast.makeText(getContext(), R.string.connecting_to_server, Toast.LENGTH_LONG).show();
            }

            @Override
            public void onFinalFailure(Call<ServerResponse> call, Throwable t) {
                if (dialog != null)
                    dialog.dismiss();
                Toast.makeText(getContext(), R.string.connecting_to_server, Toast.LENGTH_LONG).show();
            }
        });
    }

    @OnClick(R.id.img)
    protected void img_Click()
    {
        if (Build.VERSION.SDK_INT >= 23 && getActivity().checkSelfPermission(android.Manifest.permission.CAMERA) != PackageManager.PERMISSION_GRANTED)
            requestPermissions(new String[]{android.Manifest.permission.CAMERA}, 10);
        else
            openCamera();
    }


    @Override
    public void onRequestPermissionsResult(int requestCode, @NonNull String[] permissions, @NonNull int[] grantResults) {
        super.onRequestPermissionsResult(requestCode, permissions, grantResults);
        if(requestCode == 10 && grantResults[0] == PackageManager.PERMISSION_GRANTED) {
            openCamera();
        }
    }

    private void openCamera() {

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
                img.setImageURI(resultUri);
                imageFile = new File(resultUri.getPath());


            } else if (resultCode == CropImage.CROP_IMAGE_ACTIVITY_RESULT_ERROR_CODE) {
                Exception error = result.getError();
            }
        }
    }

    @OnClick(R.id.btnSend)
    protected void btnSend_Click()
    {
        final BaseActivity activity = (BaseActivity)getActivity();
        final ProgressDialog dialog = activity.showProgress();
        Call<ServerResponse> call = activity.createWebService(Requests.class).askQuestion(
                BaseActivity.getToken(),
                RequestBody.create(MultipartBody.FORM, spChapter.getSelectedItem().toString()), //TODO
                RequestBody.create(MultipartBody.FORM, txtDescription.getText().toString()),
                RequestBody.create(MediaType.parse("text/plain"), String.valueOf(((Course)spLesson.getSelectedItem()).id)),
                MultipartBody.Part.createFormData("image", imageFile.getName(),
                    RequestBody.create(MediaType.parse("image/*"), imageFile)),
                RequestBody.create(MultipartBody.FORM, "0"),
                RequestBody.create(MediaType.parse("text/plain"), "2"));

        call.enqueue(new Callback<ServerResponse>() {
            @Override
            public void onResponse(Call<ServerResponse> call, Response<ServerResponse> response) {
                dialog.dismiss();
                ServerResponse resp = response.body();
                if (resp != null && resp.success ) {
                    Toast.makeText(activity, R.string.sendSuccess, Toast.LENGTH_SHORT).show();
                    Toast.makeText(activity, R.string.questionWillShowAfterConfirm, Toast.LENGTH_LONG).show();
                    activity.onBackPressed();
                    txtDescription.setText("");
                }
                else if(resp != null)
                    Toast.makeText(activity, resp.getErrorMessage(), Toast.LENGTH_LONG).show();
                else
                    Toast.makeText(activity, R.string.server_problem, Toast.LENGTH_LONG).show();
            }

            @Override
            public void onFailure(Call<ServerResponse> call, Throwable t) {
                dialog.dismiss();
                Toast.makeText(activity, R.string.server_problem, Toast.LENGTH_LONG).show();
            }
        });
    }
}
