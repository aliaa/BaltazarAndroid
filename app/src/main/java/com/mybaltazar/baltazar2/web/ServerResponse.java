package com.mybaltazar.baltazar2.web;

import com.google.gson.annotations.SerializedName;
import com.mybaltazar.baltazar2.models.*;
import com.mybaltazar.baltazar2.models.Error;

import java.util.ArrayList;

public class ServerResponse 
{
    public String access_token,token_type,message;
    public int expires_in;
    public ArrayList<City> cities;
    public ArrayList<Level> levels;
    public ArrayList<Field> fields;
    @SerializedName("degree")
    public ArrayList<Degree> degrees;
    public ArrayList<Course> courses;
    public Question question;
    public int code;
    public boolean success;
    public ArrayList<ShopItem> products;
    public ArrayList<Blog> posts;
    public boolean isLiked;
    public ArrayList<ShopItem> favorites;
    public Answer answer;
    public User user;
    public Error error;
    @SerializedName("states")
    public ArrayList<LevelField> levelFields;
}
