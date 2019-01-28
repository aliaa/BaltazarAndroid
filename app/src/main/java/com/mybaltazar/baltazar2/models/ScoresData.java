package com.mybaltazar.baltazar2.models;

import java.util.List;

public class ScoresData
{
    public class TopStudent
    {
        public String userName;
        public String cityId;
        public String school;
        public int points;
    }

    public int myPoints;
    public int myPointsFromLeague;
    public int myPointsFromOtherQuestions;
    public int myTotalScore;
    public int myScoreOnBase;
    public List<TopStudent> totalTop;
    public List<TopStudent> topOnBase;
}
