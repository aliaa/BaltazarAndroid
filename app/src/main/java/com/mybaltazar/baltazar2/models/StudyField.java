package com.mybaltazar.baltazar2.models;

import java.io.Serializable;

public class StudyField implements Serializable
{
    public String id, name;

    @Override
    public String toString() {
        return name;
    }
}
