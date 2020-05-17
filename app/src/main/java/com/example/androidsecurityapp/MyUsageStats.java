package com.example.androidsecurityapp;

public class MyUsageStats implements Comparable<MyUsageStats> {
    private Long totalTimeInForeground;
    private Long lastTimeUsed;
    private String packageName;
    private String label;

    private long rxMobile;
    private long rxWIFI;
    private long txMobile;
    private long txWIFI;


    public MyUsageStats(Long totalTimeInForeground, Long lastTimeUsed, String packageName, String label, long rxMobile, long rxWIFI, long txMobile, long txWIFI) {
        this.totalTimeInForeground = totalTimeInForeground;
        this.lastTimeUsed = lastTimeUsed;
        this.packageName = packageName;
        this.label = label;
        this.rxMobile = rxMobile;
        this.rxWIFI = rxWIFI;
        this.txMobile = txMobile;
        this.txWIFI = txWIFI;
    }

    public long getRXMobile() {
        return rxMobile;
    }

    public long getRXWIFI() {
        return rxWIFI;
    }
    public long getTXMobile() {
        return txMobile;
    }

    public long getTXWIFI() {
        return txWIFI;
    }

    public String getLabel() {
        return label;
    }

    public void setLabel(String label) {
        this.label = label;
    }

    public Long getTotalTimeInForeground() {
        return totalTimeInForeground;
    }

    public void setTotalTimeInForeground(Long totalTimeInForeground) {
        this.totalTimeInForeground = totalTimeInForeground;
    }

    public Long getLastTimeUsed() {
        return lastTimeUsed;
    }

    public void setLastTimeUsed(Long lastTimeUsed) {
        this.lastTimeUsed = lastTimeUsed;
    }

    public String getPackageName() {
        return packageName;
    }

    public void setPackageName(String packageName) {
        this.packageName = packageName;
    }

    @Override
    public int compareTo(MyUsageStats o) {
        if (getTotalTimeInForeground() == null || o.getTotalTimeInForeground() == null) {
            return 0;
        }
        return getTotalTimeInForeground().compareTo(o.getTotalTimeInForeground());
    }
}
