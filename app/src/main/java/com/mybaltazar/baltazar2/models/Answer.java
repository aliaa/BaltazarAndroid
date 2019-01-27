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

    public String questionId;
    public QuestionerResponseEnum response;
    public boolean toBaltazarQuestion;
}
