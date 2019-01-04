package com.mybaltazar.baltazar2.models;

public class Course
{
    public int id;
    public String title, description;

    @Override
    public String toString() {
        return title;
    }
}
