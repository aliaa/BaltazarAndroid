package com.mybaltazar.baltazar2.models;

import java.io.Serializable;

public class City implements Serializable
{
    public String id, provinceId;
    public String name;

    @Override
    public String toString() {
        return name;
    }
}
