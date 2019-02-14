package com.example.q.myapplication2.activity;

import android.content.Intent;
import android.support.v7.app.ActionBar;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.support.v7.widget.Toolbar;
import android.util.Log;
import android.view.MenuItem;
import android.view.View;
import android.widget.AdapterView;
import android.widget.ListView;

import com.example.q.myapplication2.R;
import com.example.q.myapplication2.adapter.Article;
import com.example.q.myapplication2.adapter.ArticleAdapter;

import java.util.ArrayList;
import java.util.List;

public class CollectionActivity extends AppCompatActivity {
    private List<Article> articleList;
    private String TAG = "nnnnn";

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_collection);
        Toolbar toolbar = (Toolbar) findViewById(R.id.toolbar_collectActivity);
        setSupportActionBar(toolbar);
        ActionBar actionBar = getSupportActionBar();
        if (actionBar != null) {
            actionBar.setDisplayHomeAsUpEnabled(true);
            actionBar.setHomeAsUpIndicator(R.drawable.ic_back);
        }
        articleList =  getIntent().getParcelableArrayListExtra("article_list");
        ArticleAdapter adapter = new ArticleAdapter(CollectionActivity.this, R.layout.article_item, articleList);
        ListView listView = (ListView) findViewById(R.id.collect_list_view);
        listView.setAdapter(adapter);
        listView.setOnItemClickListener(new AdapterView.OnItemClickListener() {
            @Override
            public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
                Article article = articleList.get(position);
                String data = article.getDate();
                Intent intent = new Intent();
                intent.putExtra("data_return", data);
                setResult(RESULT_OK, intent);
                finish();
            }
        });
    }
    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        switch (item.getItemId()) {
            case android.R.id.home:
                finish();
                break;
        default:
        }
        return true;
    }
}
