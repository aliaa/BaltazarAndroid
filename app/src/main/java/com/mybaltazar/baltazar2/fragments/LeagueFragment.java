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
    @BindView(R.id.lblFestivalName) TextView lblFestivalName;
    @BindView(R.id.lblPoints)       TextView lblPoints;
    @BindView(R.id.lblLeaguePoints) TextView lblLeaguePoints;
    @BindView(R.id.lblOtherPoints)  TextView lblOtherPoints;
    @BindView(R.id.lblScore)        TextView lblScore;
    @BindView(R.id.lblScoreOnGrade) TextView lblScoreOnGrade;
    @BindView(R.id.lstTop10OnGrade)     LinearLayout lstTop10OnGrade;
    @BindView(R.id.lstTop10OnFestival)  LinearLayout lstTop10OnFestival;
    @BindView(R.id.lstTop10Total)       LinearLayout lstTop10Total;

    private static ScoresData scoresData;
    private static long cacheTime;
    private static final int CACHE_DURATION_SEC = 1000;

    public static ScoresData getScoresData()
    {
        if(scoresData != null && System.currentTimeMillis() < cacheTime + CACHE_DURATION_SEC)
            return scoresData;
        return null;
    }

    public static void setScoresData(ScoresData data)
    {
        scoresData = data;
        cacheTime = System.currentTimeMillis();
    }

    public LeagueFragment() { }

    @Override
    public int getTitleId() {
        return R.string.league;
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        View root = inflater.inflate(R.layout.fragment_league, container, false);
        ButterKnife.bind(this, root);
        loadUI();
        return root;
    }

    private void loadUI()
    {
        ScoresData data = getScoresData();
        if(data != null)
            loadUI(data);
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
                            setScoresData(resp.data);
                            loadUI(resp.data);
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

    private void loadUI(ScoresData data)
    {
        lblFestivalName.setText(getString(R.string.festival) + " " + data.festivalName);
        lblPoints.setText(String.valueOf(data.myFestivalPoints));
        lblLeaguePoints.setText(String.valueOf(data.myFestivalPointsFromLeague));
        lblOtherPoints.setText(String.valueOf(data.myFestivalPointsFromOtherQuestions));
        lblScore.setText(String.valueOf(data.myFestivalScore));
        lblScoreOnGrade.setText(String.valueOf(data.myFestivalScoreOnGrade));

        CommonData commonData = ((BaseActivity)getActivity()).loadCommonData(false, null);

        lstTop10OnGrade.removeAllViews();
        for (ScoresData.TopStudent st : data.festivalTopOnGrade) {
            lstTop10OnGrade.addView(createTopStudentView(st, commonData));
        }

        lstTop10OnFestival.removeAllViews();
        for (ScoresData.TopStudent st : data.festivalTop) {
            lstTop10OnFestival.addView(createTopStudentView(st, commonData));
        }

        lstTop10Total.removeAllViews();
        for (ScoresData.TopStudent st : data.totalTop) {
            lstTop10Total.addView(createTopStudentView(st, commonData));
        }
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
