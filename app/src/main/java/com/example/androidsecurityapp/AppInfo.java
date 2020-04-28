package com.example.androidsecurityapp;

import android.graphics.drawable.Drawable;

import java.util.List;

public class AppInfo {
    private String label;
    private Drawable icon;
    private List<String> commonPremissons;
    private List<String> dangerousPremissons;
    private boolean expanded;

    public AppInfo(String label, Drawable icon, List<String> commonPremissons, List<String> dangerousPremissons) {
        this.label = label;
        this.icon = icon;
        this.commonPremissons = commonPremissons;
        this.dangerousPremissons = dangerousPremissons;
        this.expanded = false;
    }

    public String getLabel() {
        return label;
    }

    public void setLabel(String label) {
        this.label = label;
    }

    public Drawable getIcon() {
        return icon;
    }

    public void setIcon(Drawable icon) {
        this.icon = icon;
    }

    public List<String> getCommonPremissons() {
        return commonPremissons;
    }

    public void setCommonPremissons(List<String> commonPremissons) {
        this.commonPremissons = commonPremissons;
    }

    public List<String> getDangerousPremissons() {
        return dangerousPremissons;
    }

    public void setDangerousPremissons(List<String> dangerousPremissons) {
        this.dangerousPremissons = dangerousPremissons;
    }

    public boolean isExpanded() {
        return expanded;
    }

    public void setExpanded(boolean expanded) {
        this.expanded = expanded;
    }
}
