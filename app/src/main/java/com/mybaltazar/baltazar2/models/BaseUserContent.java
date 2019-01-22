package com.mybaltazar.baltazar2.models;

import java.io.Serializable;

public abstract class BaseUserContent implements Serializable
{
    public enum PublishStatusEnum
    {
        waitForApprove,
        published,
        rejected
    }

    public String id;
    public String userId;
    public String createDate;
    public String text;
    public PublishStatusEnum publishStatus;
    public boolean hasImage;
    public boolean hasVideo;
    public boolean hasVoice;
}