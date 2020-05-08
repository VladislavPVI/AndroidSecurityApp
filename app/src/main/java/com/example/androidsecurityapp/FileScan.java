package com.example.androidsecurityapp;

import androidx.appcompat.app.AppCompatActivity;

import android.os.Bundle;
import android.provider.Settings;
import android.util.Log;
import android.widget.TextView;

import java.io.File;
import java.util.ArrayList;
import java.util.List;

public class FileScan extends AppCompatActivity {

    private List<File> apkFiles = new ArrayList<>();
    private TextView apkView;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_file_scan);
        apkView = findViewById(R.id.apkView);

        for (String storage : getStorageDirectories())
            findApk(new File(storage));
        apkView.setText(apkFiles.toString());


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
                apkFiles.add(file);

        }



    }

    public List<String> getStorageDirectories() {

        List<String> results = new ArrayList<>();
        File[] externalDirs = getExternalFilesDirs(null);
        for (File file : externalDirs) {
            String path = file.getPath().split("/Android")[0];
            results.add(path);
        }
        return results;
    }
}
