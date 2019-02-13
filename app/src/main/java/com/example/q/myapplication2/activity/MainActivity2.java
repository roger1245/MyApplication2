package com.example.q.myapplication2.activity;

import android.content.ContentValues;
import android.content.Intent;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.support.annotation.NonNull;
import android.support.design.widget.NavigationView;
import android.support.v4.view.GravityCompat;
import android.support.v4.widget.DrawerLayout;
import android.support.v7.app.ActionBar;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.util.Log;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.view.Window;
import android.widget.ProgressBar;
import android.widget.TextView;
import android.widget.Toast;
import android.support.v7.widget.Toolbar;

import com.example.q.myapplication2.R;
import com.example.q.myapplication2.db.MyDatabaseHelper;
import com.example.q.myapplication2.gson.Data;
import com.example.q.myapplication2.gson.Date;
import com.example.q.myapplication2.util.Utility;

import java.io.BufferedReader;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.net.HttpURLConnection;
import java.net.URL;
import java.text.SimpleDateFormat;

public class MainActivity2 extends AppCompatActivity {
    private TextView titleArticle;
    private TextView titleAuthor;
    private TextView contentTV;
    private TextView endTV;
    private DrawerLayout mDrawerLayout;
    private MyDatabaseHelper myDatabaseHelper;
    private Data data;
    private String address;
    private MenuItem like;
    private ProgressBar bar;
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
        myDatabaseHelper = new MyDatabaseHelper(this, "Article.db", null, 1);
        NavigationView navigationView = (NavigationView) findViewById(R.id.nav_view);
        titleArticle = findViewById(R.id.title_article);
        titleAuthor = findViewById(R.id.title_author);
        contentTV = findViewById(R.id.content_tv);
        endTV = findViewById(R.id.end_tv);
        address = "https://interface.meiriyiwen.com/article/today?dev=1";
        sendRequestWithHttpURLConnection(address);
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
            case R.id.toolbar_like:
//                item_like = item;
                if (item.getTitle().equals("收藏失败")) {
                    item.setIcon(R.drawable.ic_nav_like_success);
                    item.setTitle("收藏成功");
                    addToDatabase();
                    Toast.makeText(this, "收藏成功", Toast.LENGTH_SHORT).show();
                } else if (item.getTitle().equals("收藏成功")){
                    item.setIcon(R.drawable.ic_nav_like_fail);
                    item.setTitle("收藏失败");
                    deleteToDatabase();
                    Toast.makeText(this, "取消收藏", Toast.LENGTH_SHORT).show();
                }
                break;
            case R.id.toolbar_before:
                address = "https://interface.meiriyiwen.com/article/day?dev=1&date=" + data.date.prev;
                sendRequestWithHttpURLConnection(address);
                break;
            case R.id.toolbar_later:
                address = "https://interface.meiriyiwen.com/article/day?dev=1&date=" + data.date.next;
                sendRequestWithHttpURLConnection(address);
                break;
            case R.id.toolbar_random:
                address = "https://interface.meiriyiwen.com/article/random?dev=1";
                sendRequestWithHttpURLConnection(address);
                break;
            case R.id.toolbar_today:
//                item_today = item
                address = "https://interface.meiriyiwen.com/article/today?dev=1";
                sendRequestWithHttpURLConnection(address);
                break;
            default:
                break;
        }
        return true;
    }
    //添加收藏的文章到数据库
    private void addToDatabase() {
        SQLiteDatabase db = myDatabaseHelper.getWritableDatabase();
        ContentValues values = new ContentValues();
        values.put("curr", data.date.curr);
        values.put("prev", data.date.prev);
        values.put("next", data.date.next);
        values.put("author", data.author);
        values.put("title", data.title);
        values.put("digest", data.digest);
        values.put("content", data.content);
        values.put("wordCount", data.wc);
        db.insert("Article", null, values);
    }
    //删除收藏的数据
    private void deleteToDatabase() {
        SQLiteDatabase db = myDatabaseHelper.getWritableDatabase();

        db.delete("Article", "curr = ?", new String[] { data.date.curr });
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        getMenuInflater().inflate(R.menu.toolbar_menu, menu);
        like = menu.findItem(R.id.toolbar_like);
        return true;
    }


    private void sendRequestWithHttpURLConnection(final String address) {
        bar = (ProgressBar) findViewById(R.id.progress_bar);
        bar.setVisibility(View.VISIBLE);
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
                    data = Utility.handleResponse(response.toString());

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
        content = content.replaceAll("<p>", "  ");
        content = content.replaceAll("</p>", "  ");
        String wordCount = "全文共" + data.wc + "字";
//        Log.d("nowaDate", date.curr);
        SQLiteDatabase db = myDatabaseHelper.getWritableDatabase();
        Cursor c = db.query("Article", null,"curr = ?", new String[] { date.curr }, null, null, null);
        if (c.moveToNext()) {
//            Log.d("nowa", currentDay);
//            Log.d("nowa", "jingguo2");
            like.setIcon(R.drawable.ic_nav_like_success);
            like.setTitle("收藏成功");

        } else {
//            Log.d("nowa","jingguo3");
            like.setIcon(R.drawable.ic_nav_like_fail);
            like.setTitle("收藏失败");
        }
//        Cursor cursor = db.query("Article", null,"curr = ? ", new String[] { date.curr },null, null, null, null);
//        if (cursor != null) {
//            Log.d("nowa", cursor.toString());
//            String curr1 = cursor.getString(cursor.getColumnIndex("curr"));
//            Log.d("nowa", curr1);
//            like.setIcon(R.drawable.ic_nav_like_fail);
//            like.setTitle("收藏失败");
//        }
//        cursor.close();
        bar.setVisibility(View.GONE);
        titleArticle.setText(title);
        titleAuthor.setText(author);
        contentTV.setText(content);
        endTV.setText(wordCount);
    }
//    private boolean checkeColumnExists(String tableName, String columnName) {
//        boolean result = false;
//        Cursor cursor = null;
//        SQLiteDatabase db = myDatabaseHelper.getWritableDatabase();
//        try {
//            cursor = db.rawQuery( "select * from sqlite_master where name = ? and sql like ?",
//                    new String[]{tableName, "%" + columnName + "%"});
//            result = null != cursor && cursor.moveToFirst();
//        } catch (Exception e) {
//            e.printStackTrace();
//        } finally {
//            if (null != cursor && !cursor.isClosed()) {
//                cursor.close();
//            }
//        }
//        return result;
//    }

}
