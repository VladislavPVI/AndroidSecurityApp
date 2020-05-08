package com.example.androidsecurityapp;

import android.content.SharedPreferences;
import android.content.pm.ApplicationInfo;
import android.content.pm.PackageInfo;
import android.content.pm.PackageManager;
import android.content.pm.PermissionInfo;
import android.os.Bundle;
import android.preference.PreferenceManager;

import androidx.appcompat.app.AppCompatActivity;
import androidx.recyclerview.widget.RecyclerView;

import com.google.gson.Gson;
import com.google.gson.reflect.TypeToken;

import java.time.Instant;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;

public class ListofAppActivity extends AppCompatActivity {

    private RecyclerView recyclerView;
    private RecyclerView.LayoutManager layoutManager;
    private RecyclerView.Adapter mAdapter;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_listof_app);
        recyclerView = (RecyclerView) findViewById(R.id.my_recycler_view);


        // specify an adapter (see also next example)

        List<AppInfo> appInfos = new ArrayList<>();

        try {
            appInfos = initData();
        } catch (PackageManager.NameNotFoundException e) {
            e.printStackTrace();
        }
        saveAppData("appData",appInfos);
        List<AppInfo> appData = getAppData("appData");
        mAdapter = new MyAdapter(appInfos,getPackageManager());
        recyclerView.setAdapter(mAdapter);

    }

    public void saveAppData(String key , Object obj) {
        SharedPreferences prefs = PreferenceManager.getDefaultSharedPreferences(this);
        SharedPreferences.Editor editor = prefs.edit();
        Gson gson = new Gson();
        String json = gson.toJson(obj);
        editor.putString("saveDataApp", Instant.now().toString());

        editor.putString(key,json);
        editor.apply();     // This line is IMPORTANT !!!
    }

    public List<AppInfo> getAppData(String key) {
        SharedPreferences prefs = PreferenceManager.getDefaultSharedPreferences(this);
        Gson gson = new Gson();
        String json = prefs.getString(key,"");
        java.lang.reflect.Type type = new TypeToken<List<AppInfo>>(){}.getType();
        List<AppInfo> obj = gson.fromJson(json, type);
        return obj;
    }

    private List<AppInfo> initData() throws PackageManager.NameNotFoundException {
        List<AppInfo> appList = new ArrayList<>();
        PackageManager pm = getPackageManager();
        List<ApplicationInfo> packages = pm.getInstalledApplications(PackageManager.GET_META_DATA);

        for (ApplicationInfo ap : packages) {
            List<String> commonPremissons = new ArrayList<>();
            List<String> dangerousPremissons = new ArrayList<>();
            PackageInfo packageInfo = pm.getPackageInfo(ap.packageName, PackageManager.GET_PERMISSIONS);
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
            AppInfo appInfo = new AppInfo(ap.loadLabel(pm).toString(),commonPremissons,dangerousPremissons,ap.packageName);
            appList.add(appInfo);
        }
        return appList;
    }
}
