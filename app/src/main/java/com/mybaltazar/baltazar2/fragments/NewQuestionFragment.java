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
import com.mybaltazar.baltazar2.web.Requests;
import com.mybaltazar.baltazar2.web.RetryableCallback;
import com.mybaltazar.baltazar2.web.ServerResponse;
import com.theartofdev.edmodo.cropper.CropImage;
import com.theartofdev.edmodo.cropper.CropImageView;

import java.io.File;
import java.util.ArrayList;

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
    @BindView(R.id.spLevel)     Spinner spLevel;
    @BindView(R.id.spField)     Spinner spField;
    @BindView(R.id.spLesson)    Spinner spLesson;
    @BindView(R.id.img)         ImageView img;
    @BindView(R.id.spChapter)   Spinner spChapter;

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
        ServerResponse cachedResponse = BaseActivity.loadCache(getContext(), "tools", ServerResponse.class);
        if(cachedResponse != null)
            loadSpinners(cachedResponse);
        loadFromNetwork(cachedResponse == null);
    }

    private void loadSpinners(ServerResponse data)
    {
        setSpinnerAdapter(spField, data.fields);
        spField.setOnItemSelectedListener(new AdapterView.OnItemSelectedListener() {
            @Override
            public void onItemSelected(AdapterView<?> adapterView, View view, int i, long l) {
                spLevel.setVisibility(i > 11 ? View.VISIBLE : View.GONE);
            }

            @Override
            public void onNothingSelected(AdapterView<?> adapterView) { }
        });

        setSpinnerAdapter(spLevel, data.levels);
        setSpinnerAdapter(spLesson, data.courses);
    }

    private <T> void setSpinnerAdapter(Spinner spinner, ArrayList<T> list)
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
                BaseActivity.getSessionId(),
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
