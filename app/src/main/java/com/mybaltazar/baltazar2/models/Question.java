package com.mybaltazar.baltazar2.models;

import java.io.Serializable;
import java.util.ArrayList;

public class Question implements Serializable
{
    public enum Status {
        unpublished,
        published,
        answered
    }

    public Integer id, user_id, prize;
    public String title, context, image, video, voice, created_at;
    public Status status;
    public Course course;
    public Field field;
    public User user;
    public Level level;
    public ArrayList<Answer> answers;
    public int total_answers;
    public int new_answers;

    public String getLevelTitle() {
        if(level != null)
            return level.title;
        return null;
    }

    public String getCourseTitle() {
        if(course != null)
            return course.title;
        return null;
    }
}
