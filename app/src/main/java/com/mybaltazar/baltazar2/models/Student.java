package com.mybaltazar.baltazar2.models;

import java.util.List;

public class Student
{
    public enum GenderEnum
    {
        male, female
    }

    public String id;
    public String firstName, lastName, phone, password;
    public int grade;
    public String studyFieldId;
    public String address;
    public GenderEnum gender;
    public String cityId;
    public String schoolName;
    public String token;
    public int coins;
    public int points;
    public int pointsFromLeague;
    public int pointsFromOtherQuestions;
}
