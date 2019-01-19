package com.mybaltazar.baltazar2.models;

import java.io.Serializable;

public class CourseSection implements Serializable
{
    public String id;
    public String courseId;
    public String name;

    @Override
    public String toString() {
        return name;
    }
}
