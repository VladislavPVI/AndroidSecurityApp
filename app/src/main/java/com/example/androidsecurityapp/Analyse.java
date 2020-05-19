package com.example.androidsecurityapp;

import android.annotation.SuppressLint;
import android.app.ActivityManager;
import android.app.usage.NetworkStats;
import android.app.usage.NetworkStatsManager;
import android.app.usage.UsageStats;
import android.app.usage.UsageStatsManager;
import android.content.Context;
import android.content.SharedPreferences;
import android.content.pm.ApplicationInfo;
import android.content.pm.PackageInfo;
import android.content.pm.PackageManager;
import android.net.ConnectivityManager;
import android.os.Environment;
import android.os.StatFs;
import android.telephony.TelephonyManager;

import com.example.androidsecurityapp.model.AppInfo;
import com.example.androidsecurityapp.model.ChangesOfSettings;
import com.example.androidsecurityapp.model.DangerousPermissions;
import com.example.androidsecurityapp.model.MyUsageStats;
import com.google.gson.Gson;
import com.google.gson.reflect.TypeToken;

import java.io.BufferedReader;
import java.io.File;
import java.io.InputStreamReader;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.Collections;
import java.util.HashMap;
import java.util.List;

import static android.content.Context.ACTIVITY_SERVICE;

public class Analyse {
    private SharedPreferences preferences;
    private PackageManager pm;

    private boolean rooted;
    private List<AppInfo> appInfos;
    private List<String> apkFiles;
    private List<ChangesOfSettings> settingsChanges;
    private long saveDate;
    private long oldSaveDate;
    private List<String> externalDirs;
    private HashMap<String, String> oldSettings;
    private HashMap<String, String> newSettings;
    private Context applicationContext;
    private NetworkStatsManager networkStatsManager;
    private String idMob;
    private List<MyUsageStats> myUsageStats;
    private int score;

    private String permissonText;
    private String rootText;
    private String apkText;
    private String settingsText;
    private String resText;

    public String getPermissonText() {
        return permissonText;
    }

    public String getRootText() {
        return rootText;
    }

    public String getApkText() {
        return apkText;
    }

    public String getSettingsText() {
        return settingsText;
    }

    public String getResText() {
        return resText;
    }

    public Analyse(SharedPreferences prefs, Context applicationContext) {
        this.preferences = prefs;
        this.pm = applicationContext.getPackageManager();
        this.applicationContext = applicationContext;
        this.externalDirs = getStorageDirectories();
    }

    public List<String> getStorageDirectories() {

        File[] externalDirs = applicationContext.getExternalFilesDirs(null);

        List<String> results = new ArrayList<>();
        for (File file : externalDirs) {
            String path = file.getPath().split("/Android")[0];
            results.add(path);
        }
        return results;
    }

    public void loadSavedData() {

        Gson gson = new Gson();

        saveDate = preferences.getLong("analyseDate", 0);
        oldSaveDate = preferences.getLong("oldAnalyseDate", 0);

        String json = preferences.getString("appData", "");
        java.lang.reflect.Type type = new TypeToken<List<AppInfo>>() {
        }.getType();
        appInfos = gson.fromJson(json, type);

        rooted = preferences.getBoolean("root", false);

        json = preferences.getString("oldSettings", "");
        type = new TypeToken<HashMap<String, String>>() {
        }.getType();
        oldSettings = gson.fromJson(json, type);

        json = preferences.getString("newSettings", "");
        newSettings = gson.fromJson(json, type);

        json = preferences.getString("apkPaths", "");
        type = new TypeToken<List<String>>() {
        }.getType();
        apkFiles = gson.fromJson(json, type);

        json = preferences.getString("settingsChanges", "");
        type = new TypeToken<List<ChangesOfSettings>>() {
        }.getType();
        settingsChanges = gson.fromJson(json, type);

        json = preferences.getString("myUsageStats", "");
        type = new TypeToken<List<MyUsageStats>>() {
        }.getType();
        myUsageStats = gson.fromJson(json, type);

        score = setScore();

    }


    public void saveData() {
        Gson gson = new Gson();
        SharedPreferences.Editor editor = preferences.edit();

        editor.putLong("oldAnalyseDate", oldSaveDate);
        editor.putLong("analyseDate", saveDate);


        String json = gson.toJson(appInfos);
        editor.putString("appData", json);

        editor.putBoolean("root", rooted);

        json = gson.toJson(apkFiles);
        editor.putString("apkPaths", json);

        json = gson.toJson(oldSettings);
        editor.putString("oldSettings", json);

        json = gson.toJson(newSettings);
        editor.putString("newSettings", json);

        json = gson.toJson(settingsChanges);
        editor.putString("settingsChanges", json);

        json = gson.toJson(myUsageStats);
        editor.putString("myUsageStats", json);

        editor.apply();

    }

    public void initDataAnalyse(HashMap<String, String> newSettings) {

        saveDate = System.currentTimeMillis();
        oldSaveDate = saveDate;

        appInfos = initData();

        rooted = isRootGiven();

        oldSettings = newSettings;
        this.newSettings = newSettings;

        apkFiles = new ArrayList<>();
        for (String storage : externalDirs)
            findApk(new File(storage));

        myUsageStats = appUsage();

        score = setScore();


    }


    public void updateData(HashMap<String, String> newSettings) {

        oldSaveDate = saveDate;
        saveDate = System.currentTimeMillis();

        updateDataApp();

        rooted = isRootGiven();

        oldSettings = this.newSettings;
        this.newSettings = newSettings;

        apkFiles = new ArrayList<>();
        for (String storage : externalDirs)
            findApk(new File(storage));

        settingsChanges = getCompare(oldSettings, this.newSettings);

        score = setScore();

    }

    public List<ChangesOfSettings> getCompare(HashMap<String, String> oldSettings, HashMap<String, String> newSettings) {

        List<ChangesOfSettings> compare = new ArrayList<>();

        for (String setting : oldSettings.keySet()) {
            String newV = newSettings.get(setting);
            String oldV = oldSettings.get(setting);
            if (!oldV.equals(newV))
                compare.add(new ChangesOfSettings(setting, oldSaveDate, saveDate, oldV, newV));
        }

        if (settingsChanges != null)
            compare.addAll(settingsChanges);

        if (compare.size() == 0)
            return null;
        else return compare;
    }

    public void findApk(File file) {
        if (file.isDirectory()) {
            File[] files = file.listFiles();
            if (files.length > 0) {
                for (File fileInt : files)
                    findApk(fileInt);
            }
        } else {
            String[] split = file.getName().split("[.]");
            if (split[split.length - 1].equals("apk"))
                apkFiles.add(file.getPath());
        }
    }

    public static boolean isRootAvailable() {
        for (String pathDir : System.getenv("PATH").split(":")) {
            if (new File(pathDir, "su").exists()) {
                return true;
            }
        }
        return false;
    }

    public static boolean isRootGiven() {
        if (isRootAvailable()) {
            Process process = null;
            try {
                process = Runtime.getRuntime().exec(new String[]{"su", "-c", "id"});
                BufferedReader in = new BufferedReader(new InputStreamReader(process.getInputStream()));
                String output = in.readLine();
                if (output != null && output.toLowerCase().contains("uid=0"))
                    return true;
            } catch (Exception e) {
                e.printStackTrace();
            } finally {
                if (process != null)
                    process.destroy();
            }
        }

        return false;
    }

    private List<MyUsageStats> appUsage() {
        Calendar calendar = Calendar.getInstance();
        long endTime = calendar.getTimeInMillis();
        calendar.add(Calendar.YEAR, -1);
        long startTime = calendar.getTimeInMillis();

        UsageStatsManager mUsageStatsManager = (UsageStatsManager) applicationContext.getSystemService(Context.USAGE_STATS_SERVICE);
        networkStatsManager = (NetworkStatsManager) applicationContext.getSystemService(Context.NETWORK_STATS_SERVICE);

        idMob = getSubscriberId(ConnectivityManager.TYPE_MOBILE);

        final List<UsageStats> stats =
                mUsageStatsManager.queryUsageStats(UsageStatsManager.INTERVAL_BEST, startTime, endTime);

        HashMap<String, MyUsageStats> map = new HashMap<>();

        for (UsageStats st : stats) {
            String packageName = st.getPackageName();
            MyUsageStats myUsageStats = map.get(packageName);

            if (myUsageStats == null) {
                String label;
                ApplicationInfo applicationInfo;
                long packageRxBytesMobile;
                long packageRxBytesWifi;
                long packageTxBytesMobile;
                long packageTxBytesWifi;

                try {
                    applicationInfo = pm.getApplicationInfo(packageName, 0);
                    label = pm.getApplicationLabel(applicationInfo).toString();
                    packageRxBytesMobile = getPackageRxBytesMobile(applicationInfo.uid);
                    packageRxBytesWifi = getPackageRxBytesWifi(applicationInfo.uid);
                    packageTxBytesMobile = getPackageTxBytesMobile(applicationInfo.uid);
                    packageTxBytesWifi = getPackageTxBytesWifi(applicationInfo.uid);

                } catch (PackageManager.NameNotFoundException e) {
                    continue;
                }
                map.put(packageName, new MyUsageStats(st.getTotalTimeInForeground(), st.getLastTimeUsed(), packageName, label, packageRxBytesMobile, packageRxBytesWifi, packageTxBytesMobile, packageTxBytesWifi));
            } else {
                if (st.getLastTimeUsed() > myUsageStats.getLastTimeUsed())
                    myUsageStats.setLastTimeUsed(st.getLastTimeUsed());
                myUsageStats.setTotalTimeInForeground(myUsageStats.getTotalTimeInForeground() + st.getTotalTimeInForeground());
            }
        }

        List<MyUsageStats> listStat = new ArrayList<>(map.values());
        Collections.sort(listStat);
        Collections.reverse(listStat);

        return listStat;
    }

    public long getPackageRxBytesMobile(int packageUid) {
        NetworkStats networkStats = null;
        try {
            networkStats = networkStatsManager.queryDetailsForUid(
                    ConnectivityManager.TYPE_MOBILE,
                    idMob,
                    0,
                    System.currentTimeMillis(),
                    packageUid);
        } catch (NullPointerException e) {
            return -1;
        }

        long rxBytes = 0L;
        NetworkStats.Bucket bucket = new NetworkStats.Bucket();
        while (networkStats.hasNextBucket()) {
            networkStats.getNextBucket(bucket);
            rxBytes += bucket.getRxBytes();
        }
        networkStats.close();
        return rxBytes;
    }

    public long getPackageTxBytesMobile(int packageUid) {
        NetworkStats networkStats = null;
        try {
            networkStats = networkStatsManager.queryDetailsForUid(
                    ConnectivityManager.TYPE_MOBILE,
                    idMob,
                    0,
                    System.currentTimeMillis(),
                    packageUid);
        } catch (NullPointerException e) {
            return -1;
        }

        long txBytes = 0L;
        NetworkStats.Bucket bucket = new NetworkStats.Bucket();
        while (networkStats.hasNextBucket()) {
            networkStats.getNextBucket(bucket);
            txBytes += bucket.getTxBytes();
        }
        networkStats.close();
        return txBytes;
    }

    public long getPackageRxBytesWifi(int packageUid) {
        NetworkStats networkStats = null;
        try {
            networkStats = networkStatsManager.queryDetailsForUid(
                    ConnectivityManager.TYPE_WIFI,
                    "",
                    0,
                    System.currentTimeMillis(),
                    packageUid);
        } catch (NullPointerException e) {
            return -1;
        }

        long rxBytes = 0L;
        NetworkStats.Bucket bucket = new NetworkStats.Bucket();
        while (networkStats.hasNextBucket()) {
            networkStats.getNextBucket(bucket);
            rxBytes += bucket.getRxBytes();
        }
        networkStats.close();
        return rxBytes;
    }

    public long getPackageTxBytesWifi(int packageUid) {
        NetworkStats networkStats = null;
        try {
            networkStats = networkStatsManager.queryDetailsForUid(
                    ConnectivityManager.TYPE_WIFI,
                    "",
                    0,
                    System.currentTimeMillis(),
                    packageUid);
        } catch (NullPointerException e) {
            return -1;
        }

        long txBytes = 0L;
        NetworkStats.Bucket bucket = new NetworkStats.Bucket();
        while (networkStats.hasNextBucket()) {
            networkStats.getNextBucket(bucket);
            txBytes += bucket.getTxBytes();
        }
        networkStats.close();
        return txBytes;
    }

    @SuppressLint("MissingPermission")
    private String getSubscriberId(int networkType) {
        if (ConnectivityManager.TYPE_MOBILE == networkType) {
            TelephonyManager tm = (TelephonyManager) applicationContext.getSystemService(Context.TELEPHONY_SERVICE);
            return tm.getSubscriberId();
        }
        return "";
    }

    private void updateDataApp() {
        List<String> names = new ArrayList<>();
        for (AppInfo appInfo : appInfos)
            names.add(appInfo.getPackageName());

        List<AppInfo> appList = new ArrayList<>();
        List<ApplicationInfo> packages = pm.getInstalledApplications(PackageManager.GET_META_DATA);

        for (ApplicationInfo ap : packages) {
            if (names.contains(ap.packageName))
                continue;
            List<String> commonPremissons = new ArrayList<>();
            List<String> dangerousPremissons = new ArrayList<>();
            PackageInfo packageInfo = null;
            try {
                packageInfo = pm.getPackageInfo(ap.packageName, PackageManager.GET_PERMISSIONS);
            } catch (PackageManager.NameNotFoundException e) {
                continue;
            }
            String[] stringArray = packageInfo.requestedPermissions;

            if (stringArray != null) {
                for (String perm : stringArray) {
                    if (DangerousPermissions.forCont(perm)) {
                        dangerousPremissons.add(perm);
                    } else {
                        commonPremissons.add(perm);
                    }
                }
            }
            AppInfo appInfo = null;
            if (dangerousPremissons.size() != 0)
                appInfo = new AppInfo(ap.loadLabel(pm).toString(), commonPremissons, dangerousPremissons, ap.packageName, false);
            else
                appInfo = new AppInfo(ap.loadLabel(pm).toString(), commonPremissons, dangerousPremissons, ap.packageName, true);

            appList.add(appInfo);
        }
        if (appList.size() != 0)
            appInfos.addAll(appList);
    }


    private List<AppInfo> initData() {
        List<AppInfo> appList = new ArrayList<>();
        List<ApplicationInfo> packages = pm.getInstalledApplications(PackageManager.GET_META_DATA);


        for (ApplicationInfo ap : packages) {
            List<String> commonPremissons = new ArrayList<>();
            List<String> dangerousPremissons = new ArrayList<>();
            PackageInfo packageInfo = null;
            try {
                packageInfo = pm.getPackageInfo(ap.packageName, PackageManager.GET_PERMISSIONS);
            } catch (PackageManager.NameNotFoundException e) {
                continue;
            }
            String[] stringArray = packageInfo.requestedPermissions;

            if (stringArray != null) {
                for (String perm : stringArray) {
                    if (DangerousPermissions.forCont(perm)) {
                        dangerousPremissons.add(perm);
                    } else {
                        commonPremissons.add(perm);
                    }
                }
            }
            AppInfo appInfo = null;
            if (dangerousPremissons.size() != 0)
                appInfo = new AppInfo(ap.loadLabel(pm).toString(), commonPremissons, dangerousPremissons, ap.packageName, false);
            else
                appInfo = new AppInfo(ap.loadLabel(pm).toString(), commonPremissons, dangerousPremissons, ap.packageName, true);

            appList.add(appInfo);
        }
        return appList;
    }

    public boolean isRooted() {
        return rooted;
    }

    public List<AppInfo> getAppInfos() {
        return appInfos;
    }

    public List<String> getApkFiles() {
        return apkFiles;
    }

    public List<ChangesOfSettings> getSettingsChanges() {
        return settingsChanges;
    }

    public long getSaveDate() {
        return saveDate;
    }

    public double getScorePerm() {
        int permissionsCount = 0;
        for (AppInfo app : appInfos) {
            if (!app.isTrust()) {
                permissionsCount++;
            }
        }
        permissonText = permissionsCount + "/" + appInfos.size();

        return (1 - (permissionsCount / (double) appInfos.size())) * 100.0;
    }

    public boolean nonTrustSettings() {
        if (settingsChanges!=null){
        for (ChangesOfSettings sett : settingsChanges) {
            if (!sett.isApproved())
                return false;
        }}
        return true;
    }

    public double getStorage() {
        StatFs stat = new StatFs(Environment.getExternalStorageDirectory().getPath());
        long bytesAvailable = stat.getBlockSizeLong() * stat.getAvailableBlocksLong();
        long totalM = stat.getTotalBytes();

        return bytesAvailable / (double) totalM * 100.0;

    }

    public double getRam() {
        ActivityManager.MemoryInfo mi = new ActivityManager.MemoryInfo();
        ActivityManager activityManager = (ActivityManager) applicationContext.getSystemService(ACTIVITY_SERVICE);
        activityManager.getMemoryInfo(mi);

        long ramAV = mi.availMem;
        long ramTotal = mi.totalMem;

        return ramAV / (double) ramTotal * 100.0;

    }


    public int getScore() {
        return score;
    }

    public int setScore() {
        double score = 0;

        if (!rooted) {
            score += 35;
        }
        rootText = String.valueOf(rooted);

        score += getScorePerm() * 0.3;

        if (apkFiles.size() == 0) {
            score += 15;
            apkText = "GOOD";
        } else apkText = "Details";

        if (nonTrustSettings()) {
            score += 5;
            settingsText = "GOOD";
        } else settingsText = "Details";

        double scoreRam = getRam();
        if (scoreRam >= 15)
            score += 10;
        else if (scoreRam >= 5)
            score += 5;

        double scoreStorage = getStorage();
        if (scoreStorage >= 10)
            score += 5;
        else if (scoreRam >= 5)
            score += 2.5;

        if (scoreRam > 15 && scoreStorage > 10)
            resText = "GOOD";
        else if (scoreRam < 5 && scoreStorage < 5)
            resText = "BAD";
        else resText = "OK";

        return (int) score;


    }

}
