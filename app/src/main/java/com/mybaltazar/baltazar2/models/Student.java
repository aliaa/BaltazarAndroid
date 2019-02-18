package com.mybaltazar.baltazar2.models;

import java.util.List;

public class Student extends BaseEntity
{
    public enum GenderEnum
    {
        Unspecified, Male, Female
    }

    public class FestivalPoint
    {
        public String name;
        public String displayName;
        public int pointsFromLeague;
        public int pointsFromOtherQuestions;
        public int points;
    }

    public String firstName, lastName, nickName, phone, password;
    public int grade;
    public int membershipDurationDays;
    public String studyFieldId;
    public String address;
    public GenderEnum gender;
    public String cityId;
    public String schoolName;
    public String schoolPhone;
    public String token;
    public int coins;
    public int totalPoints;
    public int totalPointsFromLeague;
    public int totalPointsFromOtherQuestions;
    public String invitationCode;
    public String invitedFromCode;
    public String birthDate;
    public String pusheId;
    public List<FestivalPoint> festivalPoints;
    public boolean isTeacher;
}
