package com.example.q.myapplication2.adapter;

import android.os.Parcel;
import android.os.Parcelable;

public class Article implements Parcelable {
    private String title;
    private String author;
    private String date;

    public Article(String title, String author, String date) {
        this.title = title;
        this.author = author;
        this.date = date;
    }

    public Article() {
    }

    public void setTitle(String title) {
        this.title = title;
    }

    public void setAuthor(String author) {
        this.author = author;
    }

    public void setDate(String date) {
        this.date = date;
    }

    public String getTitle() {

        return title;
    }

    public String getAuthor() {
        return author;
    }

    public String getDate() {
        return date;
    }

    @Override
    public int describeContents() {
        return 0;
    }

    @Override
    public void writeToParcel(Parcel dest, int flags) {
        dest.writeString(title);
        dest.writeString(author);
        dest.writeString(date);
    }
    public static final Parcelable.Creator<Article> CREATOR = new Parcelable.Creator<Article>() {

        @Override
        public Article createFromParcel(Parcel source) {
            Article article = new Article();
            article.title = source.readString();
            article.author = source.readString();
            article.date = source.readString();
            return article;
        }

        @Override
        public Article[] newArray(int size) {
            return new Article[size];
        }
    };
}
