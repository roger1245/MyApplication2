package com.example.q.myapplication2.util;

import com.example.q.myapplication2.gson.Data;
import com.google.gson.Gson;

import org.json.JSONObject;

public class Utility {
    //处理返回服务器返回的数据
    public static Data handleResponse(String response) {
            try {
                JSONObject jsonObject = new JSONObject(response);
                JSONObject jsonObjectData = jsonObject.getJSONObject("data");
                String data = jsonObjectData.toString();
                return new Gson().fromJson(data, Data.class);
            } catch (Exception e) {
                e.printStackTrace();
            }
        return null;
    }
}
