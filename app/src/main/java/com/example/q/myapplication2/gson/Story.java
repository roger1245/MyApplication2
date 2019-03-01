package com.example.q.myapplication2.gson;

import com.google.gson.annotations.SerializedName;

public class Story {
    @SerializedName("images")
    public String[] images;
    @SerializedName("type")
    public int type;
    @SerializedName("id")
    public int id;
    @SerializedName("ga_prefix")
    public String ga_prefix;
    @SerializedName("title")
    public String title;
    @SerializedName("multipic")
    public Boolean multipic;

}
