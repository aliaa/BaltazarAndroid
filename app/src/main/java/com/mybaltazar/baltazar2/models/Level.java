package com.mybaltazar.baltazar2.models;

import java.io.Serializable;

public class Level implements Serializable
{
    public int id;
    public String title, description, deleted_at, created_at, updated_at;

    @Override
    public String toString() {
        return title;
    }
}
