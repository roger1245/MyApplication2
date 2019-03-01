package com.example.q.myapplication2.gson;

import com.google.gson.annotations.SerializedName;

public class DataOfZhuHu {
    @SerializedName("date")
    public String date;
    @SerializedName("stories")
    public Story[] stories;
}
