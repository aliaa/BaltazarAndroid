package com.mybaltazar.baltazar2.models;

import java.io.Serializable;

public class City extends BaseEntity
{
    public String provinceId;
    public String name;

    @Override
    public String toString() {
        return name;
    }
}
