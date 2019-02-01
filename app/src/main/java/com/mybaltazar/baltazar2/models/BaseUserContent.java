package com.mybaltazar.baltazar2.models;

import java.io.Serializable;

public abstract class BaseUserContent extends BaseEntity
{
    public enum PublishStatusEnum
    {
        WaitForApprove,
        Published,
        Rejected
    }

    public String userId;
    public String userName;
    public String createDate;
    public String text;
    public PublishStatusEnum publishStatus;
    public boolean hasImage;
    public boolean hasVideo;
    public boolean hasVoice;
}
