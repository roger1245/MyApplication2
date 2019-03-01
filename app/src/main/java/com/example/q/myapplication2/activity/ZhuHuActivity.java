package com.example.q.myapplication2.activity;

import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.util.Log;
import android.view.View;
import android.widget.ProgressBar;
import android.widget.Toast;

import com.example.q.myapplication2.R;
import com.example.q.myapplication2.adapter.ZhiHuArticle;
import com.example.q.myapplication2.adapter.ZhiHuArticleAdapter;
import com.example.q.myapplication2.gson.DataOfZhuHu;
import com.example.q.myapplication2.gson.Story;
import com.example.q.myapplication2.util.Utility;

import java.io.BufferedReader;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.net.HttpURLConnection;
import java.net.URL;
import java.util.ArrayList;
import java.util.List;

public class ZhuHuActivity extends AppCompatActivity {
    private static String TAG = "ZhiHu";
    private DataOfZhuHu data;
    private String latestNews ="https://news-at.zhihu.com/api/4/news/latest";
    private List<ZhiHuArticle> zhiHuArticlesList = new ArrayList<>();
    private ZhiHuArticleAdapter adapter = null;
    private ProgressBar progressBar;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_zhu_hu);
        sendRequestWithHttpURLConnection(latestNews);
        RecyclerView recyclerView = (RecyclerView) findViewById(R.id.recycler_view_zhihu);
        LinearLayoutManager layoutManager = new LinearLayoutManager(this);
        recyclerView.setLayoutManager(layoutManager);
        adapter = new ZhiHuArticleAdapter(zhiHuArticlesList);
        recyclerView.setAdapter(adapter);

    }

    private void sendRequestWithHttpURLConnection(final String address) {
//        progressBar = findViewById(R.id.progress_bar_zhihu);
//        progressBar.setVisibility(View.VISIBLE);
        new Thread(new Runnable() {
            @Override
            public void run() {
                HttpURLConnection connection = null;
                BufferedReader reader = null;
                try {
                    URL url = new URL(address);
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
                    data = Utility.handleResponseOfZhiHu(response.toString());
                    Story[] stories = data.stories;
                    for (Story story : stories) {
                        ZhiHuArticle zhiHuArticle = new ZhiHuArticle(story.title, story.images[0]);
                        zhiHuArticlesList.add(zhiHuArticle);
                        Log.d(TAG, story.images[0]);
                        Log.d(TAG, story.title);
                    }
                    adapter.notifyDataSetChanged();
//                    runOnUiThread(new Runnable() {
//                        @Override
//                        public void run() {
//                                progressBar.setVisibility(View.GONE);
//
//                        }
//                    });
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
}
