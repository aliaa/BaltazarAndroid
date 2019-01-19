package com.mybaltazar.baltazar2.models;

import java.io.Serializable;

public class Answer extends BaseUserContent
{
    public enum QuestionerResponseEnum
    {
        NotSeen,
        Accepted,
        Rejected,
        Reported,
    }

    public String questionerId;
    public QuestionerResponseEnum response;
}
