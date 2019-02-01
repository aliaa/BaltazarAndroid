package com.mybaltazar.baltazar2.models;

import java.io.Serializable;

public abstract class BaseEntity implements Serializable
{
    public static final String EMPTY_ID = "000000000000000000000000";

    public String id;
}
