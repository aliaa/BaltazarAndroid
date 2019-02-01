package com.mybaltazar.baltazar2.models;

import java.io.Serializable;

public class StudyField extends BaseEntity
{
    public String name;

    @Override
    public String toString() {
        return name;
    }
}
