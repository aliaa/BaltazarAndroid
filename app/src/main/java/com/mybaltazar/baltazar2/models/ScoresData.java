package com.mybaltazar.baltazar2.models;

import java.util.List;

public class ScoresData
{
    public class TopStudent
    {
        public String studentId;
        public String userName;
        public String cityId;
        public String school;
        public int points;
    }

    public String festivalName;
    public int myFestivalPoints;
    public int myFestivalPointsFromLeague;
    public int myFestivalPointsFromOtherQuestions;
    public int myFestivalScore;
    public int myFestivalScoreOnGrade;
    public int myAllTimePoints;
    public int myAllTimeTotalScore;
    public List<TopStudent> totalTop;
    public List<TopStudent> festivalTop;
    public List<TopStudent> festivalTopOnGrade;
}
