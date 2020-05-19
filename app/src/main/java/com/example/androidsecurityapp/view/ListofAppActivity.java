package com.example.androidsecurityapp.view;

import android.content.Context;
import android.content.SharedPreferences;
import android.content.pm.ApplicationInfo;
import android.content.pm.PackageInfo;
import android.content.pm.PackageManager;
import android.os.Bundle;

import androidx.appcompat.app.AppCompatActivity;
import androidx.recyclerview.widget.RecyclerView;

import com.example.androidsecurityapp.model.DangerousPermissions;
import com.example.androidsecurityapp.R;
import com.example.androidsecurityapp.adapter.MyAdapter;
import com.example.androidsecurityapp.model.AppInfo;
import com.google.gson.Gson;
import com.google.gson.reflect.TypeToken;

import java.util.ArrayList;
import java.util.List;

public class ListofAppActivity extends AppCompatActivity {

    private RecyclerView recyclerView;
    private RecyclerView.LayoutManager layoutManager;
    private RecyclerView.Adapter mAdapter;
    private List<AppInfo> appInfos;
    private SharedPreferences prefs;

    public static final String APP_PREFERENCES = "analyseData";

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_listof_app);
        recyclerView = findViewById(R.id.my_recycler_view);
        prefs = getSharedPreferences(APP_PREFERENCES, Context.MODE_PRIVATE);

    }

    @Override
    protected void onResume() {
        super.onResume();
        appInfos = getAppData("appData");
        if (appInfos == null)
            appInfos = initData();

        mAdapter = new MyAdapter(appInfos, getPackageManager());
        recyclerView.setAdapter(mAdapter);

    }

    @Override
    protected void onPause() {
        super.onPause();
        for (AppInfo appInfo : appInfos)
            appInfo.setExpanded(false);
        saveAppData("appData", appInfos);
    }

    public void saveAppData(String key, Object obj) {
        SharedPreferences.Editor editor = prefs.edit();
        Gson gson = new Gson();
        String json = gson.toJson(obj);

        editor.putString(key, json);
        editor.apply();
    }

    public List<AppInfo> getAppData(String key) {
        Gson gson = new Gson();
        String json = prefs.getString(key, "");
        java.lang.reflect.Type type = new TypeToken<List<AppInfo>>() {
        }.getType();
        List<AppInfo> obj = gson.fromJson(json, type);
        return obj;
    }

    private List<AppInfo> initData() {
        List<AppInfo> appList = new ArrayList<>();
        PackageManager pm = getPackageManager();
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
                        continue;
                    }
                    commonPremissons.add(perm);
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
}
