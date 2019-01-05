package com.mybaltazar.baltazar2.models;

import java.io.Serializable;

public class Field implements Serializable
{
    public int id;
    public String title,description;

    @Override
    public String toString() {
        return title;
    }
}
