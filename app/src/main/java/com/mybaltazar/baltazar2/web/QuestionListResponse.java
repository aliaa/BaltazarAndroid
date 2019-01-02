package com.mybaltazar.baltazar2.web;

import com.mybaltazar.baltazar2.models.Question;

import java.util.ArrayList;

public class QuestionListResponse extends ServerResponse
{
    public ArrayList<Question> list;
    public ArrayList<Question> questions;
}
