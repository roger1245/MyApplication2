package com.example.q.myapplication2.activity;

import android.content.SharedPreferences;
import android.preference.PreferenceManager;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.util.Log;
import android.widget.TextView;
import android.widget.Toast;

import com.example.q.myapplication2.R;
import com.example.q.myapplication2.gson.Data;
import com.example.q.myapplication2.gson.Date;
import com.example.q.myapplication2.util.HttpUtil;
import com.example.q.myapplication2.util.Utility;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.net.HttpURLConnection;
import java.net.URL;
import java.security.PrivateKey;

import okhttp3.Call;
import okhttp3.Callback;
import okhttp3.Response;

public class MainActivity2 extends AppCompatActivity {
    private TextView titleArticle;
    private TextView titleAuthor;
    private TextView contentTV;
    private TextView endTV;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main2);
        titleArticle = findViewById(R.id.title_article);
        titleAuthor = findViewById(R.id.title_author);
        contentTV = findViewById(R.id.content_tv);
        endTV = findViewById(R.id.end_tv);
        sendRequestWithHttpURLConnection();
    }
    private void sendRequestWithHttpURLConnection() {
        new Thread(new Runnable() {
            @Override
            public void run() {
                HttpURLConnection connection = null;
                BufferedReader reader = null;
                try {
                    URL url = new URL("https://interface.meiriyiwen.com/article/today?dev=1");
                    connection = (HttpURLConnection) url.openConnection();
                    connection.setRequestMethod("GET");
                    connection.setConnectTimeout(8000);
                    connection.setReadTimeout(8000);
                    InputStream in = connection.getInputStream();
                    reader = new BufferedReader(new InputStreamReader(in));
                    StringBuilder response = new StringBuilder();
                    String line;
                    while ((line = reader.readLine()) != null) {
                        response.append(line);
                    }
                    final Data data = Utility.handleResponse(response.toString());

                runOnUiThread(new Runnable() {
                    @Override
                    public void run() {
                        if (data != null) {

                            showDataInfo(data);
                        } else {

                            Toast.makeText(MainActivity2.this, "刷新失败", Toast.LENGTH_SHORT).show();
                        }
                    }
                });
                } catch (Exception e) {
                    e.printStackTrace();
                } finally {
                    if (reader != null) {
                        try {
                            reader.close();
                        } catch (Exception e) {
                            e.printStackTrace();
                        }
                    }
                    if (connection != null) {
                        connection.disconnect();
                    }
                }
            }
        }).start();
    }

    //更改展示数据
    private void showDataInfo(Data data) {
        Date date = data.date;
        String title = data.title;
        String author = data.author;
        String content = data.content;
        String wordCount = "全文共" + data.wc + "字";
        titleArticle.setText(title);
        titleAuthor.setText(author);
        contentTV.setText(content);
        endTV.setText(wordCount);
    }
}
