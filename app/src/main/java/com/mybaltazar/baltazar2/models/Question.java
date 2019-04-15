package com.mybaltazar.baltazar2.models;

import java.io.Serializable;
import java.util.ArrayList;
import java.util.List;

public class Question extends BaseUserContent
{
    public int grade;
    public String courseId;
    public String sectionId;
    public Answer acceptedAnswer;
    public List<Answer> answers;
    public int prize;
    public boolean hot;
    public boolean fromBaltazar;
    public boolean allowUploadOnAnswer;
    public boolean iAlreadyAnswered;
}
