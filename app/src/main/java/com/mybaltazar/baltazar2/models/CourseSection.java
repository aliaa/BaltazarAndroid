package com.mybaltazar.baltazar2.models;

import java.io.Serializable;

public class CourseSection extends BaseEntity
{
    public String courseId;
    public String name;

    @Override
    public String toString() {
        return name;
    }
}
