package com.mybaltazar.baltazar2.fragments;

import android.app.ProgressDialog;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.net.Uri;
import android.os.Build;
import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.util.Log;
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
import com.mybaltazar.baltazar2.models.Question;
import com.mybaltazar.baltazar2.models.StudyField;
import com.mybaltazar.baltazar2.utils.DataListener;
import com.mybaltazar.baltazar2.webservices.CommonData;
import com.mybaltazar.baltazar2.webservices.CommonResponse;
import com.mybaltazar.baltazar2.webservices.DataResponse;
import com.mybaltazar.baltazar2.webservices.RetryableCallback;
import com.mybaltazar.baltazar2.webservices.Services;
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
import retrofit2.Response;

import static android.app.Activity.RESULT_OK;

public class NewQuestionFragment extends BaseFragment
{
    @BindView(R.id.spinnerGrade)        Spinner spinnerGrade;
    @BindView(R.id.spinnerStudyField)   Spinner spinnerStudyField;
    @BindView(R.id.spinnerCourse)       Spinner spinnerCourse;
//    @BindView(R.id.spinnerSection)      Spinner spinnerSection;
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
        final BaseActivity activity = (BaseActivity)getActivity();
        final ProgressDialog progress = activity.showProgress();
        activity.loadCommonData(false, new DataListener<CommonData>() {
            @Override
            public void onCallBack(CommonData data) {
                progress.dismiss();
                loadSpinners(data);
            }

            @Override
            public void onFailure() {
                progress.dismiss();
                activity.onBackPressed();
            }
        });
    }

    private void loadSpinners(final CommonData data)
    {
        spinnerGrade.setAdapter(new ArrayAdapter<>(getContext(), android.R.layout.simple_spinner_dropdown_item,
                getResources().getStringArray(R.array.grades)));

        spinnerGrade.setOnItemSelectedListener(new AdapterView.OnItemSelectedListener() {
            @Override
            public void onItemSelected(AdapterView<?> parent, View view, int position, long id) {
                int grade = position + 1;
                spinnerStudyField.setVisibility(grade >= 10 ? View.VISIBLE : View.GONE);
                setCoursesSpinnerAdapter(data.courses, data.sections);
            }
            @Override
            public void onNothingSelected(AdapterView<?> parent) {}
        });

        setSpinnerAdapter(spinnerStudyField, data.studyFields);
        spinnerStudyField.setOnItemSelectedListener(new AdapterView.OnItemSelectedListener() {
            @Override
            public void onItemSelected(AdapterView<?> parent, View view, int position, long id) {
                setCoursesSpinnerAdapter(data.courses, data.sections);
            }

            @Override
            public void onNothingSelected(AdapterView<?> parent) {}
        });
    }

    private void setCoursesSpinnerAdapter(List<Course> allCourses, final List<CourseSection> allSections)
    {
        int grade = spinnerGrade.getSelectedItemPosition()+1;
        StudyField studyField = (StudyField) spinnerStudyField.getSelectedItem();
        String otherCourses = getString(R.string.other_courses);
        Course other = null;
        List<Course> courses = new ArrayList<>();
        for(Course c : allCourses) {
            if(grade == c.grade && (grade < 10 || studyField.id.equals(c.studyFieldId))) {
                if(c.name.equals(otherCourses))
                    other = c;
                else
                    courses.add(c);
            }
        }
        if(other != null)
            courses.add(other);
        setSpinnerAdapter(spinnerCourse, courses);
//        setSectionsSpinnerAdapter(allSections);
    }

//    private void setSectionsSpinnerAdapter(List<CourseSection> allSections)
//    {
//        Course course = (Course) spinnerCourse.getSelectedItem();
//        List<CourseSection> sections = new ArrayList<>();
//        for(CourseSection s : allSections) {
//            if(s.courseId.equals(course.id))
//                sections.add(s);
//        }
//        spinnerSection.setVisibility(sections.size() > 0 ? View.VISIBLE : View.GONE);
//        setSpinnerAdapter(spinnerSection, sections);
//    }

    private <T> void setSpinnerAdapter(Spinner spinner, List<T> list)
    {
        spinner.setAdapter(new ArrayAdapter<T>(getContext(), android.R.layout.simple_spinner_dropdown_item, list));
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
        final ProgressDialog progress = activity.showProgress();

        final Question question = new Question();
        question.grade = spinnerGrade.getSelectedItemPosition()+1;
        question.courseId = ((Course)spinnerCourse.getSelectedItem()).id;
//        CourseSection section = (CourseSection)spinnerSection.getSelectedItem();
//        if(section != null)
//            question.sectionId = section.id;
        question.text = txtDescription.getText().toString();

        Call<DataResponse<Question>> call = activity.createWebService(Services.class).publishQuestion(BaseActivity.getToken(), question);
        call.enqueue(new RetryableCallback<DataResponse<Question>>(call) {
            @Override
            public void onFinalFailure(Call<DataResponse<Question>> call, Throwable t) {
                progress.dismiss();
                Toast.makeText(getContext(), R.string.no_network, Toast.LENGTH_LONG).show();
                activity.onBackPressed();
            }

            @Override
            public void onResponse(Call<DataResponse<Question>> call, Response<DataResponse<Question>> response) {
                DataResponse<Question> resp = response.body();
                if(resp == null)
                    onFinalFailure(call, new Exception("null body!"));
                else if(!resp.success && resp.message != null)
                    Toast.makeText(getContext(), resp.message, Toast.LENGTH_LONG).show();
                else if(imageFile == null) {
                    progress.dismiss();
                    done();
                }
                else
                    uploadImage(progress, resp.data);
            }
        });
    }

    private void uploadImage(final ProgressDialog progress, final Question data)
    {
        final BaseActivity activity = (BaseActivity)getActivity();
        Call<CommonResponse> call = activity.createWebService(Services.class).uploadQuestionImage(BaseActivity.getToken(), data.id,
                MultipartBody.Part.createFormData("image", imageFile.getName(),
                        RequestBody.create(MediaType.parse("image/*"), imageFile)));
        call.enqueue(new RetryableCallback<CommonResponse>(call) {
            @Override
            public void onFinalFailure(Call<CommonResponse> call, Throwable t)
            {
                progress.dismiss();
                Log.i("Upload error:", t.getMessage());
                Toast.makeText(getContext(), R.string.server_problem, Toast.LENGTH_LONG).show();
                activity.onBackPressed();
            }

            @Override
            public void onResponse(Call<CommonResponse> call, Response<CommonResponse> response)
            {
                progress.dismiss();
                CommonResponse resp = response.body();
                if(resp == null)
                    onFinalFailure(call, new Exception("null body!"));
                else if(!resp.success && resp.message != null)
                    Toast.makeText(getContext(), resp.message, Toast.LENGTH_LONG).show();
                else
                    done();
            }
        });
    }

    private void done() {
        if(imageFile != null) {
            imageFile.delete();
            imageFile = null;
        }
        Toast.makeText(getContext(), R.string.sendSuccess, Toast.LENGTH_SHORT).show();
        Toast.makeText(getContext(), R.string.questionWillShowAfterConfirm, Toast.LENGTH_LONG).show();
        txtDescription.setText("");
        getActivity().onBackPressed();
        ((BaseActivity)getActivity()).loadCommonData(true, null);
    }
}
