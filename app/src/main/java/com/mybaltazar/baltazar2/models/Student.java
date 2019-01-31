package com.mybaltazar.baltazar2.models;

public class Student
{
    public enum GenderEnum
    {
        male, female
    }

    public String id;
    public String firstName, lastName, nickName, phone, password;
    public int grade;
    public int membershipDurationDays;
    public String studyFieldId;
    public String address;
    public GenderEnum gender;
    public String cityId;
    public String schoolName;
    public String token;
    public int coins;
    public int totalPoints;
    public int totalPointsFromLeague;
    public int totalPointsFromOtherQuestions;
    public String invitationCode;
    public String invitedFromCode;
}
