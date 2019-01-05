package com.mybaltazar.baltazar2.models;

import java.io.Serializable;

public class Course implements Serializable
{
    public int id;
    public String title, description;

    @Override
    public String toString() {
        return title;
    }
}
