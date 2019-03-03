package com.mybaltazar.baltazar2.models;

import java.io.Serializable;

public class Blog extends BaseEntity implements Serializable
{
    public String dateAdded;
    public String title;
    public String summary;
    public String htmlContent;
    public boolean hasImage;
}
