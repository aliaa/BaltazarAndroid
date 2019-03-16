package com.mybaltazar.baltazar2.fragments;


import android.app.AlertDialog;
import android.app.ProgressDialog;
import android.content.DialogInterface;
import android.content.Intent;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.EditText;
import android.widget.GridLayout;
import android.widget.Spinner;
import android.widget.TextView;
import android.widget.Toast;

import com.mybaltazar.baltazar2.R;
import com.mybaltazar.baltazar2.activities.AdditionalProfileActivity;
import com.mybaltazar.baltazar2.activities.BaseActivity;
import com.mybaltazar.baltazar2.activities.InviteFriendsActivity;
import com.mybaltazar.baltazar2.activities.TransactionsActivity;
import com.mybaltazar.baltazar2.models.ScoresData;
import com.mybaltazar.baltazar2.models.Student;
import com.mybaltazar.baltazar2.models.StudyField;
import com.mybaltazar.baltazar2.webservices.CommonData;
import com.mybaltazar.baltazar2.webservices.DataResponse;
import com.mybaltazar.baltazar2.webservices.RetryableCallback;
import com.mybaltazar.baltazar2.webservices.Services;

import butterknife.BindView;
import butterknife.ButterKnife;
import butterknife.OnClick;
import butterknife.OnItemSelected;
import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;

public class ProfileFragment extends BaseFragment
{
    @BindView(R.id.lblUserName)             TextView lblUserName;
    @BindView(R.id.lblNickName)             TextView lblNickName;
    @BindView(R.id.lblMyMembershipDuration) TextView lblMyMembershipDuration;
    @BindView(R.id.lblMyTotalPoints)        TextView lblMyTotalPoints;
    @BindView(R.id.lblMyTotalScore)         TextView lblMyTotalScore;
    @BindView(R.id.spinnerGrade)            Spinner spinnerGrade;
    @BindView(R.id.lblMyCurrentFieldText)   TextView lblMyCurrentFieldText;
    @BindView(R.id.spinnerStudyField)       Spinner spinnerStudyField;

    int spinnersSelectionChanged = 0;

    public ProfileFragment() { }

    @Override
    public int getTitleId() {
        return R.string.profile;
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState)
    {
        View root = inflater.inflate(R.layout.fragment_profile, container, false);
        ButterKnife.bind(this, root);
        BaseActivity activity = (BaseActivity)getActivity();
        Student profile = activity.getProfile();
        CommonData commonData = activity.loadCommonData(false, null);
        loadUI(commonData, profile);
        return  root;
    }

    private void loadUI(final CommonData commonData, final Student profile)
    {
        ScoresData scoresData = LeagueFragment.getScoresData();
        if(scoresData != null)
            loadUI(commonData, profile, scoresData);
        else
        {
            final BaseActivity activity = (BaseActivity) getActivity();
            final ProgressDialog progress = activity.showProgress();
            Call<DataResponse<ScoresData>> call = activity.createWebService(Services.class).getScores(BaseActivity.getToken());
            call.enqueue(new Callback<DataResponse<ScoresData>>() {
                @Override
                public void onResponse(Call<DataResponse<ScoresData>> call, Response<DataResponse<ScoresData>> response) {
                    progress.dismiss();
                    DataResponse<ScoresData> resp = response.body();
                    if (resp == null)
                        onFailure(call, null);
                    else {
                        if (resp.message != null)
                            Toast.makeText(activity, resp.message, Toast.LENGTH_LONG).show();
                        if (resp.data != null) {
                            LeagueFragment.setScoresData(resp.data);
                            loadUI(commonData, profile, resp.data);
                        }
                    }
                }

                @Override
                public void onFailure(Call<DataResponse<ScoresData>> call, Throwable t) {
                    progress.dismiss();
                    Toast.makeText(getContext(), R.string.no_network, Toast.LENGTH_LONG).show();
                }
            });
        }
    }

    private void loadUI(CommonData commonData, Student profile, ScoresData scoresData)
    {
        lblUserName.setText(profile.firstName + " " + profile.lastName);
        lblNickName.setText(profile.nickName);
        lblMyTotalPoints.setText(String.valueOf(scoresData.myAllTimePoints));
        lblMyTotalScore.setText(String.valueOf(scoresData.myAllTimeTotalScore));
        lblMyMembershipDuration.setText(String.valueOf(profile.membershipDurationDays) + " " + getString(R.string.day));

        spinnersSelectionChanged = 0;
        spinnerGrade.setAdapter(new ArrayAdapter<String>(getContext(),
                android.R.layout.simple_spinner_dropdown_item,
                getResources().getStringArray(R.array.grades)));
        spinnerGrade.setSelection(profile.grade - 1);

        spinnerStudyField.setAdapter(new ArrayAdapter<StudyField>(getContext(),
                android.R.layout.simple_spinner_dropdown_item,
                commonData.studyFields));
        int studyFieldIndex = 0;
        for(; studyFieldIndex < commonData.studyFields.size(); studyFieldIndex++)
            if(commonData.studyFields.get(studyFieldIndex).id.equals(profile.studyFieldId))
                break;
        if(studyFieldIndex == commonData.studyFields.size())
            studyFieldIndex = 0;
        spinnerStudyField.setSelection(studyFieldIndex);
        spinnerStudyField.setVisibility(profile.grade >= 10 ? View.VISIBLE : View.GONE);
        lblMyCurrentFieldText.setVisibility(profile.grade >= 10 ? View.VISIBLE : View.GONE);
    }

    @OnItemSelected({R.id.spinnerGrade, R.id.spinnerStudyField})
    protected void spinners_ItemSelected()
    {
        int selectedGrade = spinnerGrade.getSelectedItemPosition() + 1;
        spinnerStudyField.setVisibility(selectedGrade >= 10 ? View.VISIBLE : View.GONE);
        lblMyCurrentFieldText.setVisibility(selectedGrade >= 10 ? View.VISIBLE : View.GONE);
        if(spinnersSelectionChanged >= 2) {
            Student update = new Student();
            update.grade = selectedGrade;
            update.studyFieldId = ((StudyField) spinnerStudyField.getSelectedItem()).id;
            callUpdate(update);
        }
        spinnersSelectionChanged++;
    }

    private void callUpdate(Student update)
    {
        Call<DataResponse<Student>> call = ((BaseActivity)getActivity()).createWebService(Services.class).updateStudent(BaseActivity.getToken(), update);
        call.enqueue(new RetryableCallback<DataResponse<Student>>(getContext())
        {
            @Override
            public void onFinalSuccess(DataResponse<Student> response)
            {
                BaseActivity activity = (BaseActivity)getActivity();
                if(activity != null) {
                    activity.setProfile(response.data);
                    CommonData commonData = activity.loadCommonData(false, null);
                    loadUI(commonData, response.data);
                }
            }
        });
    }

    @OnClick(R.id.btnEditNickName)
    protected void btnEditNickName_Click()
    {
        final EditText editText = (EditText)getLayoutInflater().inflate(R.layout.edit_text, null);
        editText.setText(lblNickName.getText());
        new AlertDialog.Builder(getContext())
                .setTitle(R.string.nickName)
                .setView(editText)
                .setPositiveButton(R.string.change, new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialog, int which) {
                        Student update = new Student();
                        update.nickName = editText.getText().toString();
                        callUpdate(update);
                    }
                })
                .setNegativeButton(R.string.cancel, null)
                .create().show();
    }

    @OnClick(R.id.btnFillProfile)
    protected void btnFillProfile_Click() {
        startActivity(new Intent(getContext(), AdditionalProfileActivity.class));
    }

    @OnClick(R.id.btnShowTransaction)
    protected void btnShowTransaction_Click() {
        startActivity(new Intent(getContext(), TransactionsActivity.class));
    }

    @OnClick(R.id.btnInviteFriends)
    protected void btnInviteFriends_Click() {
        startActivity(new Intent(getContext(), InviteFriendsActivity.class));
    }

}
