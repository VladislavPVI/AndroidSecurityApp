package com.example.androidsecurityapp;

import java.util.List;

public class AppInfo {
    private String label;
    private List<String> commonPremissons;
    private List<String> dangerousPremissons;
    private boolean expanded;
    private boolean trust;
    private String packageName;

    public AppInfo(String label, List<String> commonPremissons, List<String> dangerousPremissons,String packageName) {
        this.label = label;
        this.commonPremissons = commonPremissons;
        this.dangerousPremissons = dangerousPremissons;
        this.expanded = false;
        this.trust = false;
        this.packageName = packageName;
    }

    public String getLabel() {
        return label;
    }

    public void setLabel(String label) {
        this.label = label;
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

    public boolean isTrust() {
        return trust;
    }

    public void setTrust(boolean trust) {
        this.trust = trust;
    }

    public String getPackageName() {
        return packageName;
    }

    public void setPackageName(String packageName) {
        this.packageName = packageName;
    }
}
