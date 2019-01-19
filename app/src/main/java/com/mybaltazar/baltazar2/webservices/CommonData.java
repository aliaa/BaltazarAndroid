package com.mybaltazar.baltazar2.webservices;

import com.mybaltazar.baltazar2.models.*;

import java.io.Serializable;
import java.util.List;

public class CommonData implements Serializable
{
    public List<Province> provinces;
    public List<City> cities;
    public List<Course> courses;
    public List<CourseSection> sections;
    public List<StudyField> studyFields;
}
