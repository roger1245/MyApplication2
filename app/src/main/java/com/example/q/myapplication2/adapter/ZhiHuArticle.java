package com.example.q.myapplication2.adapter;

public class ZhiHuArticle {
        private String title;
        private String imageId;

    public ZhiHuArticle(String title, String imageId) {
        this.title = title;
        this.imageId = imageId;
    }

    public String getTitle() {
        return title;
    }

    public void setTitle(String title) {
        this.title = title;
    }

    public String getImageId() {
        return imageId;
    }

    public void setImageId(String imageId) {
        this.imageId = imageId;
    }
}
