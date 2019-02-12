package com.example.q.myapplication2.activity;

import android.content.Intent;
import android.content.SharedPreferences;
import android.preference.PreferenceManager;
import android.support.annotation.NonNull;
import android.support.design.widget.NavigationView;
import android.support.v4.view.GravityCompat;
import android.support.v4.widget.DrawerLayout;
import android.support.v7.app.ActionBar;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.util.Log;
import android.view.Gravity;
import android.view.Menu;
import android.view.MenuItem;
import android.widget.TextView;
import android.widget.Toast;
import android.support.v7.widget.Toolbar;

import com.example.q.myapplication2.R;
import com.example.q.myapplication2.gson.Data;
import com.example.q.myapplication2.gson.Date;
import com.example.q.myapplication2.util.Utility;

import java.io.BufferedReader;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.net.HttpURLConnection;
import java.net.URL;

public class MainActivity2 extends AppCompatActivity {
    private TextView titleArticle;
    private TextView titleAuthor;
    private TextView contentTV;
    private TextView endTV;
    private DrawerLayout mDrawerLayout;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main2);
        Toolbar toolbar = (Toolbar) findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);
        mDrawerLayout = (DrawerLayout) findViewById(R.id.drawer_layout);
        ActionBar actionBar = getSupportActionBar();
        if (actionBar != null) {
            actionBar.setDisplayHomeAsUpEnabled(true);
            actionBar.setHomeAsUpIndicator(R.drawable.ic_actionbar_menu);
        }
        NavigationView navigationView = (NavigationView) findViewById(R.id.nav_view);
        titleArticle = findViewById(R.id.title_article);
        titleAuthor = findViewById(R.id.title_author);
        contentTV = findViewById(R.id.content_tv);
        endTV = findViewById(R.id.end_tv);
        sendRequestWithHttpURLConnection();
        navigationView.setNavigationItemSelectedListener(new NavigationView.OnNavigationItemSelectedListener() {
            @Override
            public boolean onNavigationItemSelected(@NonNull MenuItem menuItem) {
                switch (menuItem.getItemId()) {
                    case R.id.nav_like:
                        startIntent();
                        break;
                    default:
                }
                return true;
            }
        });
    }
    private void startIntent() {
        Intent intent = new Intent("com.example.activity.ACTION_START");
        startActivity(intent);
    }
//    //解决因折叠menu菜单无法显示icon的问题
//    @Override
//    public boolean onMenuOpened(int featureId, Menu menu) {
//        if (menu != null) {
//            if (menu.getClass().getSimpleName().equals("MenuBuilder")) {
//                try {
//                    Method method = menu.getClass().getDeclaredMethod("setOptionalIconVisible", Boolean.TYPE);
//                    method.setAccessible(true);
//                    method.invoke(menu, true);
//                } catch (Exception e) {
//                    e.printStackTrace();
//                }
//            }
//        }
//        return super.onMenuOpened(featureId, menu);
//    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        switch (item.getItemId()) {
            case android.R.id.home:
                mDrawerLayout.openDrawer(GravityCompat.START);
                break;
            case R.id.toolbar_like_fail:
                break;
            case R.id.toolbar_like_success:
                break;
            default:
        }
        return true;
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        getMenuInflater().inflate(R.menu.toolbar_menu, menu);
        return true;
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
