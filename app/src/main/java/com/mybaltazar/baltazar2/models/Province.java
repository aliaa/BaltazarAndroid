package com.mybaltazar.baltazar2.models;

import java.io.Serializable;

public class Province implements Serializable
{
    public String id;
    public String name;

    @Override
    public String toString() {
        return name;
    }
}
