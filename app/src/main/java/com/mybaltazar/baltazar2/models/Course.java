package com.mybaltazar.baltazar2.models;

import java.io.Serializable;

public class Course extends BaseEntity
{
    public int grade;
    public String name;
    public String studyFieldId;

    @Override
    public String toString() {
        return name;
    }

    public static Course ALL;

    static
    {
        ALL = new Course();
        ALL.name = "همه";
    }
}
