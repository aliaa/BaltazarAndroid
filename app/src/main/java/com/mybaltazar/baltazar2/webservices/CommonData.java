package com.mybaltazar.baltazar2.webservices;

import com.mybaltazar.baltazar2.models.*;

import java.io.Serializable;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

public class CommonData implements Serializable
{
    public class UpgradeData
    {
        public String message;
        public boolean forceUpgrade;
    }

    public UpgradeData upgrade;
    public Student me;
    public List<Province> provinces;
    public List<City> cities;
    public List<Course> courses;
    public List<CourseSection> sections;
    public List<StudyField> studyFields;

    public String getProvinceName(String id)
    {
        if(provinces == null)
            return null;
        for (Province i : provinces)
            if(i.id.equals(id))
                return i.name;
        return null;
    }

    public String getCityName(String id)
    {
        if(cities == null)
            return null;
        for (City i : cities)
            if(i.id.equals(id))
                return i.name;
        return null;
    }

    public String getCourseName(String id)
    {
        if(courses == null)
            return null;
        for (Course i : courses)
            if(i.id.equals(id))
                return i.name;
        return null;
    }

    public Map<String, String> getCoursesMap()
    {
        if(courses == null)
            return null;
        HashMap<String, String> map = new HashMap<>(courses.size());
        for(Course c : courses)
            map.put(c.id, c.name);
        return map;
    }

    public String getSectionName(String id)
    {
        if(sections == null)
            return null;
        for (CourseSection i : sections)
            if(i.id.equals(id))
                return i.name;
        return null;
    }

    public String getStudyFieldName(String id)
    {
        if(studyFields == null)
            return null;
        for (StudyField i : studyFields)
            if(i.id.equals(id))
                return i.name;
        return null;
    }
}
