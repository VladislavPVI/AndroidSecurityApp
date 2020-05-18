package com.example.androidsecurityapp.view;

import android.content.Context;
import android.content.SharedPreferences;
import android.content.pm.PackageInfo;
import android.content.pm.PackageManager;
import android.graphics.drawable.Drawable;
import android.os.Bundle;

import androidx.appcompat.app.AppCompatActivity;
import androidx.recyclerview.widget.RecyclerView;

import com.example.androidsecurityapp.R;
import com.example.androidsecurityapp.adapter.MyAdapterForApk;
import com.example.androidsecurityapp.model.ApkInfo;
import com.google.gson.Gson;
import com.google.gson.reflect.TypeToken;

import java.io.File;
import java.util.ArrayList;
import java.util.List;

public class FileScan extends AppCompatActivity {

    private static final String APP_PREFERENCES = "analyseData";


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_file_scan);
        PackageManager pm = getPackageManager();
        // apkView = findViewById(R.id.apkView);

        SharedPreferences prefs = getSharedPreferences(APP_PREFERENCES, Context.MODE_PRIVATE);
        Gson gson = new Gson();

        String json = prefs.getString("apkPaths", "");
        java.lang.reflect.Type type = new TypeToken<List<String>>() {
        }.getType();
        List<String> apkFiles = gson.fromJson(json, type);

        List<ApkInfo> apkInfoList = new ArrayList<>();

        for (String path : apkFiles) {
            File file = new File(path);
            if (file.exists()) {
                PackageInfo pi = pm.getPackageArchiveInfo(path, 0);
                pi.applicationInfo.sourceDir = path;
                pi.applicationInfo.publicSourceDir = path;
                Drawable apkIcon = pi.applicationInfo.loadIcon(pm);
                String apkTitle = (String) pi.applicationInfo.loadLabel(pm);
                apkInfoList.add(new ApkInfo(apkIcon, apkTitle, file.lastModified(), path, file.length()));
            }
        }
        RecyclerView recyclerView = findViewById(R.id.recyclerApk);

        if (apkInfoList.size() != 0) {
            MyAdapterForApk mAdapter = new MyAdapterForApk(apkInfoList);
            recyclerView.setAdapter(mAdapter);
        }


    }

}
