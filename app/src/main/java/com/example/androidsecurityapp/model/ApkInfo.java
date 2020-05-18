package com.example.androidsecurityapp.model;

import android.graphics.drawable.Drawable;

public class ApkInfo {
    private Drawable imageView;
    private String label;
    private long date;
    private String path;
    private long size;

    public ApkInfo(Drawable imageView, String label, long date, String path, long size) {
        this.imageView = imageView;
        this.label = label;
        this.date = date;
        this.path = path;
        this.size = size;
    }

    public Drawable getImageView() {
        return imageView;
    }

    public void setImageView(Drawable imageView) {
        this.imageView = imageView;
    }

    public String getLabel() {
        return label;
    }

    public void setLabel(String label) {
        this.label = label;
    }

    public long getDate() {
        return date;
    }

    public void setDate(long date) {
        this.date = date;
    }

    public String getPath() {
        return path;
    }

    public void setPath(String path) {
        this.path = path;
    }

    public long getSize() {
        return size;
    }

    public void setSize(long size) {
        this.size = size;
    }
}
