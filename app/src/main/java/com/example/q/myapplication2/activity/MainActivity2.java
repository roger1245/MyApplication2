package com.example.q.myapplication2.activity;

import android.Manifest;
import android.annotation.TargetApi;
import android.app.Dialog;
import android.content.ContentUris;
import android.content.ContentValues;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.net.Uri;
import android.os.Build;
import android.provider.DocumentsContract;
import android.provider.MediaStore;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.support.annotation.RequiresApi;
import android.support.design.widget.NavigationView;
import android.support.v4.app.ActivityCompat;
import android.support.v4.content.ContextCompat;
import android.support.v4.content.FileProvider;
import android.support.v4.view.GravityCompat;
import android.support.v4.widget.DrawerLayout;
import android.support.v7.app.ActionBar;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.util.Log;
import android.view.Gravity;
import android.view.LayoutInflater;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.view.Window;
import android.view.WindowManager;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.ProgressBar;
import android.widget.TextView;
import android.widget.Toast;
import android.support.v7.widget.Toolbar;

import com.example.q.myapplication2.R;
import com.example.q.myapplication2.adapter.Article;
import com.example.q.myapplication2.db.MyDatabaseHelper;
import com.example.q.myapplication2.gson.Data;
import com.example.q.myapplication2.gson.Date;
import com.example.q.myapplication2.util.Utility;

import java.io.BufferedReader;
import java.io.File;
import java.io.FileNotFoundException;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.net.HttpURLConnection;
import java.net.URL;
import java.util.ArrayList;
import de.hdodenhof.circleimageview.CircleImageView;

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
    private CircleImageView circleImageView;
    public static final int TAKE_PHOTO = 2;
    public static final int CHOOSE_PHOTO = 3;
    private Uri imageUri;
    private Dialog dialog;
    private Button cancel;
    private Button takePhoto;
    private Button chooseFromAlbum;
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
        View headerView = navigationView.getHeaderView(0);
        circleImageView = headerView.findViewById(R.id.nav_icon);
        circleImageView.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                show(v);

            }
        });

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
    //调用系统相机拍照，并返回图片
    private void takePhoto() {
        File outputImage = new File(getExternalCacheDir(), "out_image.jpg");
        try {
            if (outputImage.exists()) {
                outputImage.delete();
            }
            outputImage.createNewFile();
        }catch (IOException e) {
            e.printStackTrace();
        }
        if (Build.VERSION.SDK_INT >= 24) {
            imageUri = FileProvider.getUriForFile(this, "com.example.q.myapplication2.fileprovider", outputImage);
        } else {
            imageUri = Uri.fromFile(outputImage);
        }
        Intent intent = new Intent("android.media.action.IMAGE_CAPTURE");
        intent.putExtra(MediaStore.EXTRA_OUTPUT, imageUri);
        startActivityForResult(intent, TAKE_PHOTO);
    }


    @Override
    public void onRequestPermissionsResult(int requestCode, @NonNull String[] permissions, @NonNull int[] grantResults) {
        switch (requestCode) {
            case 1:
                if (grantResults.length > 0 && grantResults[0] == PackageManager.PERMISSION_GRANTED) {
                    takePhoto();
                } else {
                    Toast.makeText(this, "You denied the permission", Toast.LENGTH_SHORT).show();
                }
                break;
            case 2:
                if (grantResults.length > 0 && grantResults[0] == PackageManager.PERMISSION_GRANTED) {
                    openAlbum();
                } else {
                    Toast.makeText(this, "You denied the permission", Toast.LENGTH_SHORT).show();
                }
                break;
            default:
        }
    }
    //展示对话框
    private void show(View view) {
        dialog = new Dialog(this, R.style.Theme_AppCompat_Dialog);
        View inflate = LayoutInflater.from(this).inflate(R.layout.circler_view, null);
        takePhoto = (Button) inflate.findViewById(R.id.take_photo);
        chooseFromAlbum = (Button) inflate.findViewById(R.id.choose_album);
        cancel = (Button) inflate.findViewById(R.id.cancel);
        takePhoto.setOnClickListener(new View.OnClickListener(){
            @Override
            public void onClick(View v) {
                dialog.dismiss();
                if (ContextCompat.checkSelfPermission(MainActivity2.this, Manifest.permission.READ_EXTERNAL_STORAGE) != PackageManager.PERMISSION_GRANTED) {
                    ActivityCompat.requestPermissions(MainActivity2.this, new String[]{ Manifest.permission.READ_EXTERNAL_STORAGE }, 1);
                } else {
                    takePhoto();
                }
            }
        });
        chooseFromAlbum.setOnClickListener(new View.OnClickListener(){
            @Override
            public void onClick(View v) {
                dialog.dismiss();
                if (ContextCompat.checkSelfPermission(MainActivity2.this, Manifest.permission.WRITE_EXTERNAL_STORAGE) != PackageManager.PERMISSION_GRANTED) {
                    ActivityCompat.requestPermissions(MainActivity2.this, new String[]{ Manifest.permission.WRITE_EXTERNAL_STORAGE}, 2);
                } else {
                    openAlbum();
                }
            }
        });
        cancel.setOnClickListener(new View.OnClickListener(){
            @Override
            public void onClick(View v) {
                dialog.dismiss();
            }
        });
        dialog.setContentView(inflate);
        Window dialogWindow = dialog.getWindow();
        dialogWindow.setGravity(Gravity.BOTTOM);
        WindowManager.LayoutParams lp = dialogWindow.getAttributes();
        lp.y = 20;
        dialogWindow.setAttributes(lp);
        dialog.show();
    }
    //打开系统相册
    private void openAlbum() {
        Intent intent = new Intent("android.intent.action.GET_CONTENT");
        intent.setType("image/*");
        startActivityForResult(intent, CHOOSE_PHOTO);
    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, @Nullable Intent data) {
        switch (requestCode) {
            case 1:
                if (resultCode == RESULT_OK) {
                    String returnData = data.getStringExtra("data_return");
                    sendRequestWithHttpURLConnection("https://interface.meiriyiwen.com/article/day?dev=1&date=" + returnData);
                }
                break;
            case TAKE_PHOTO:
                if (resultCode == RESULT_OK) {
                    try {
                        Bitmap bitmap = BitmapFactory.decodeStream(getContentResolver().openInputStream(imageUri));
                        circleImageView.setImageBitmap(bitmap);
                    } catch (FileNotFoundException e ) {
                        e.printStackTrace();
                    }
                }
                break;
            case CHOOSE_PHOTO:
                if (resultCode == RESULT_OK) {
                    if (Build.VERSION.SDK_INT >= 19) {
                        handleImageOnKitKat(data);
                    } else {
                        handleImageBeforeKitKat(data);
                    }
                }
            default:
        }
    }
    @TargetApi(Build.VERSION_CODES.KITKAT)
    private void handleImageOnKitKat(Intent data) {
        String imagePath = null;
        Uri uri = data.getData();
        if (DocumentsContract.isDocumentUri(this, uri)) {
            String docId = DocumentsContract.getDocumentId(uri);
            if ("com.android.providers.media.documents".equals(uri.getAuthority())) {
                String id = docId.split(":")[1];
                String selection = MediaStore.Images.Media._ID + "=" + id;
                imagePath = getImagePath(MediaStore.Images.Media.EXTERNAL_CONTENT_URI, selection);
            } else if ("com.android.providers.downloads.documents".equals(uri.getAuthority())) {
                Uri contentUri = ContentUris.withAppendedId(Uri.parse("content://downloads/public_downloads"), Long.valueOf(docId));
                imagePath = getImagePath(contentUri, null);
            }
        } else if ("content".equalsIgnoreCase(uri.getScheme())) {
            imagePath = uri.getPath();
        } else if ("file".equalsIgnoreCase(uri.getScheme())) {
            imagePath = uri.getPath();
        }
        displayImage(imagePath);

    }
    private void handleImageBeforeKitKat(Intent data) {
        Uri uri = data.getData();
        String imagePath = getImagePath(uri, null);
        displayImage(imagePath);
    }
    private void displayImage(String imagePath) {
        if (imagePath != null) {
            Bitmap bitmap = BitmapFactory.decodeFile(imagePath);
            circleImageView.setImageBitmap(bitmap);
        } else {
            Toast.makeText(this, "fail to get image", Toast.LENGTH_SHORT).show();
        }
    }
    private String getImagePath(Uri uri, String selection) {
        String path = null;
        Cursor cursor = getContentResolver().query(uri, null, selection, null, null);
        if (cursor != null) {
            if (cursor.moveToFirst()) {
                path = cursor.getString(cursor.getColumnIndex(MediaStore.Images.Media.DATA));
            }
            cursor.close();
        }
        return path;
    }


    private void startIntent() {
        Intent intent = new Intent("com.example.activity.ACTION_START");
        intent.putParcelableArrayListExtra("article_list", getDatabaseInfo());
        mDrawerLayout.closeDrawer(GravityCompat.START);
        startActivityForResult(intent, 1);
    }
    //活动间传递数据
    private ArrayList<Article> getDatabaseInfo() {
        ArrayList<Article> articleList = new ArrayList<>();
        SQLiteDatabase db = myDatabaseHelper.getWritableDatabase();
        Cursor cursor = db.query("Article", null, null, null, null, null, null);
        if (cursor.moveToFirst()) {
            do {
                String title = cursor.getString(cursor.getColumnIndex("title"));
                String author = cursor.getString(cursor.getColumnIndex("author"));
                String date = cursor.getString(cursor.getColumnIndex("curr"));
                Article article = new Article(title, author, date);
                articleList.add(article);
            } while (cursor.moveToNext());
        }
        cursor.close();

        return articleList;
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
