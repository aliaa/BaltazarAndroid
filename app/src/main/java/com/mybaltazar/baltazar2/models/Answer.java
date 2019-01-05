package com.mybaltazar.baltazar2.models;

import java.io.Serializable;

public class Answer implements Serializable
{
    private String voice;
    private String image;
    private String created_at;
    private String video;
    private int question_id;
    private String deleted_at;
    private String updated_at;
    private int user_id;
    private String rate;
    private String context;
    private int reported;
    private int id;
    private String status;
    private User user;

    @Override
    public String toString() {
        return
                "Answer{" +
                        "voice = '" + voice + '\'' +
                        ",image = '" + image + '\'' +
                        ",created_at = '" + created_at + '\'' +
                        ",video = '" + video + '\'' +
                        ",question_id = '" + question_id + '\'' +
                        ",deleted_at = '" + deleted_at + '\'' +
                        ",updated_at = '" + updated_at + '\'' +
                        ",user_id = '" + user_id + '\'' +
                        ",rate = '" + rate + '\'' +
                        ",context = '" + context + '\'' +
                        ",reported = '" + reported + '\'' +
                        ",id = '" + id + '\'' +
                        ",status = '" + status + '\'' +
                        "}";
    }
}
