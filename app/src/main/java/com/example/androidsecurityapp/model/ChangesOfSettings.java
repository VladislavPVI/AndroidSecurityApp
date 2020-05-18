package com.example.androidsecurityapp.model;

public class ChangesOfSettings {
    private String setting;
    private long oldDate;
    private long newDate;
    private String oldValue;
    private String newValue;
    private boolean approved;

    public ChangesOfSettings(String setting, long oldDate, long newDate, String oldValue, String newValue) {
        this.setting = setting;
        this.oldDate = oldDate;
        this.newDate = newDate;
        this.oldValue = oldValue;
        this.newValue = newValue;
        this.approved = false;
    }

    public boolean isApproved() {
        return approved;
    }

    public void setApproved(boolean approved) {
        this.approved = approved;
    }

    public String getSetting() {
        return setting;
    }

    public void setSetting(String setting) {
        this.setting = setting;
    }

    public long getOldDate() {
        return oldDate;
    }

    public void setOldDate(long oldDate) {
        this.oldDate = oldDate;
    }

    public long getNewDate() {
        return newDate;
    }

    public void setNewDate(long newDate) {
        this.newDate = newDate;
    }

    public String getOldValue() {
        return oldValue;
    }

    public void setOldValue(String oldValue) {
        this.oldValue = oldValue;
    }

    public String getNewValue() {
        return newValue;
    }

    public void setNewValue(String newValue) {
        this.newValue = newValue;
    }
}
