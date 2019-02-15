package com.mybaltazar.baltazar2.adapters;

import android.support.v7.widget.RecyclerView;
import android.view.View;
import android.widget.TextView;

import com.mybaltazar.baltazar2.R;
import com.mybaltazar.baltazar2.activities.BaseActivity;
import com.mybaltazar.baltazar2.models.Student;

import java.util.List;

import butterknife.BindView;
import butterknife.ButterKnife;
import eu.inmite.android.lib.validations.form.validators.BaseValidator;

class LeagueArchiveViewHolder extends RecyclerView.ViewHolder
{
    @BindView(R.id.lblLeagueName)   TextView lblLeagueName;
    @BindView(R.id.lblLeaguePoints) TextView lblLeaguePoints;

    public LeagueArchiveViewHolder(View itemView) {
        super(itemView);
        ButterKnife.bind(this, itemView);
    }
}

public class LeagueArchiveAdapter extends BaseRecyclerViewAdapter<LeagueArchiveViewHolder, Student.FestivalPoint>
{
    public LeagueArchiveAdapter(BaseActivity activity, List<Student.FestivalPoint> list) {
        super(activity, list, R.layout.item_league_archive);
    }

    @Override
    protected LeagueArchiveViewHolder createViewHolder(View view) {
        return new LeagueArchiveViewHolder(view);
    }

    @Override
    protected void onBindViewHolder(LeagueArchiveViewHolder vh, Student.FestivalPoint item)
    {
        vh.lblLeagueName.setText(item.displayName);
        vh.lblLeaguePoints.setText(String.valueOf(item.points));
    }
}
