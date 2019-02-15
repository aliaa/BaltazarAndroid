package com.mybaltazar.baltazar2.events;

import com.mybaltazar.baltazar2.models.Student;

public class ProfileRefreshEvent implements Event
{
    public final Student profile;

    public ProfileRefreshEvent(Student profile) {
        this.profile = profile;
    }
}
