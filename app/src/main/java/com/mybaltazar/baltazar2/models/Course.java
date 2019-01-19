package com.mybaltazar.baltazar2.models;

import java.io.Serializable;

public class Course implements Serializable
{
    public String id;
    public int grade;
    public String name;
    public String studyFieldId;

    @Override
    public String toString() {
        return name;
    }
}
