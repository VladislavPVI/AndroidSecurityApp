package com.example.androidsecurityapp;

public class MyUsageStats {
    private long totalTimeInForeground;
    private long lastTimeUsed;
    private String packageName;

    public MyUsageStats(long totalTimeInForeground, long lastTimeUsed, String packageName) {
        this.totalTimeInForeground = totalTimeInForeground;
        this.lastTimeUsed = lastTimeUsed;
        this.packageName = packageName;
    }

    public long getTotalTimeInForeground() {
        return totalTimeInForeground;
    }

    public void setTotalTimeInForeground(long totalTimeInForeground) {
        this.totalTimeInForeground = totalTimeInForeground;
    }

    public long getLastTimeUsed() {
        return lastTimeUsed;
    }

    public void setLastTimeUsed(long lastTimeUsed) {
        this.lastTimeUsed = lastTimeUsed;
    }

    public String getPackageName() {
        return packageName;
    }

    public void setPackageName(String packageName) {
        this.packageName = packageName;
    }
}
