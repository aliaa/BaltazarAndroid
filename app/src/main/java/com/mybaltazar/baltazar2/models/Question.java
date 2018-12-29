package com.mybaltazar.baltazar2.models;

import java.util.ArrayList;

public class Question 
{
    public Integer id,user_id,prize;
    public String title,context,image,status,created_at;
    public Course course;
    public Field field;
    public User user;
    public Level level;
    public ArrayList<Answer> answers;
    public int total_answers;
    public int new_answers;
}
