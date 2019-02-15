package com.mybaltazar.baltazar2.activities;

import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;

import com.mybaltazar.baltazar2.R;
import com.mybaltazar.baltazar2.adapters.LeagueArchiveAdapter;
import com.mybaltazar.baltazar2.models.Student;

import butterknife.BindView;

public class LeaguesArchiveActivity extends BaseActivity
{
    @BindView(R.id.recycler)
    RecyclerView recycler;

    public LeaguesArchiveActivity() {
        super(R.layout.activity_leagues_archive, false);
    }

    @Override
    protected void onStart() {
        super.onStart();
        recycler.setLayoutManager(new LinearLayoutManager(this));
        Student profile = getProfile();
        if(profile.festivalPoints != null)
            recycler.setAdapter(new LeagueArchiveAdapter(this, profile.festivalPoints));
    }
}
