package com.mybaltazar.baltazar2.fragments;

import android.app.ProgressDialog;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.LinearLayout;
import android.widget.TextView;
import android.widget.Toast;

import com.mybaltazar.baltazar2.R;
import com.mybaltazar.baltazar2.activities.BaseActivity;
import com.mybaltazar.baltazar2.models.ScoresData;
import com.mybaltazar.baltazar2.models.Student;
import com.mybaltazar.baltazar2.webservices.CommonData;
import com.mybaltazar.baltazar2.webservices.DataResponse;
import com.mybaltazar.baltazar2.webservices.Services;

import butterknife.BindView;
import butterknife.ButterKnife;
import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;

public class LeagueFragment extends BaseFragment
{
    @BindView(R.id.lblUserName)     TextView lblUserName;
    @BindView(R.id.lblTotalPoints)  TextView lblTotalPoints;
    @BindView(R.id.lblLeaguePoints) TextView lblLeaguePoints;
    @BindView(R.id.lblOtherPoints)  TextView lblOtherPoints;
    @BindView(R.id.lblTotalScore)   TextView lblTotalScore;
    @BindView(R.id.lblScoreOnBase)  TextView lblScoreOnBase;
    @BindView(R.id.lstTop10OnBase)  LinearLayout lstTop10OnBase;
    @BindView(R.id.lstTop10Total)   LinearLayout lstTop10Total;


    public LeagueFragment() { }

    @Override
    public int getTitleId() {
        return R.string.league;
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        View root = inflater.inflate(R.layout.fragment_league, container, false);
        ButterKnife.bind(this, root);
        Student profile = BaseActivity.loadCache(getContext(), BaseActivity.PREF_PROFILE, Student.class);
        if(profile != null)
            loadUI(profile);
        return root;
    }

    private void loadUI(final Student profile)
    {
        lblUserName.setText(profile.firstName + " " + profile.lastName);
        lblTotalPoints.setText(String.valueOf(profile.points));
        lblLeaguePoints.setText(String.valueOf(profile.pointsFromLeague));
        lblOtherPoints.setText(String.valueOf(profile.pointsFromOtherQuestions));

        final BaseActivity activity = (BaseActivity)getActivity();
        final ProgressDialog progress = activity.showProgress();
        Call<DataResponse<ScoresData>> call = activity.createWebService(Services.class).getScores(BaseActivity.getToken());
        call.enqueue(new Callback<DataResponse<ScoresData>>() {
            @Override
            public void onResponse(Call<DataResponse<ScoresData>> call, Response<DataResponse<ScoresData>> response) {
                progress.dismiss();
                DataResponse<ScoresData> resp = response.body();
                if(resp == null)
                    onFailure(call, null);
                else
                {
                    if (resp.message != null)
                        Toast.makeText(activity, resp.message, Toast.LENGTH_LONG).show();
                    if(resp.data != null)
                    {
                        lblTotalPoints.setText(String.valueOf(resp.data.myPoints));
                        lblLeaguePoints.setText(String.valueOf(resp.data.myPointsFromLeague));
                        lblOtherPoints.setText(String.valueOf(resp.data.myPointsFromOtherQuestions));
                        lblTotalScore.setText(String.valueOf(resp.data.myTotalScore));
                        lblScoreOnBase.setText(String.valueOf(resp.data.myScoreOnBase));

                        CommonData commonData = BaseActivity.loadCache(activity, BaseActivity.PREF_COMMON, CommonData.class);
                        lstTop10Total.removeAllViews();
                        for (ScoresData.TopStudent st : resp.data.totalTop) {
                            lstTop10Total.addView(createTopStudentView(st, commonData));
                        }
                        lstTop10OnBase.removeAllViews();
                        for (ScoresData.TopStudent st : resp.data.topOnBase) {
                            lstTop10OnBase.addView(createTopStudentView(st, commonData));
                        }
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

    private View createTopStudentView(ScoresData.TopStudent st, CommonData commonData)
    {
        View v = getLayoutInflater().inflate(R.layout.item_top_student, lstTop10Total, false);
        ((TextView)v.findViewById(R.id.lblUserName)).setText(st.userName);
        ((TextView)v.findViewById(R.id.lblPoint)).setText(String.valueOf(st.points));
        ((TextView)v.findViewById(R.id.lblUserName)).setText(st.userName);

        TextView lblCityName = v.findViewById(R.id.lblCityName);
        String cityName = commonData.getCityName(st.cityId);
        if(cityName != null) {
            if (st.school == null || st.school.equals(""))
                lblCityName.setText(cityName);
            else
                lblCityName.setText(cityName + " / " + st.school);
        }
        return v;
    }
}
