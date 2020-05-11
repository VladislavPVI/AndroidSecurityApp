package com.example.androidsecurityapp;

import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;

import androidx.appcompat.app.AppCompatActivity;

public class MainActivity extends AppCompatActivity {

    private static final String TAG = "INF";

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        Button fab = findViewById(R.id.button);
        Button scan = findViewById(R.id.fileScanButton);
        Button bat = findViewById(R.id.batteryB);
        Button ram = findViewById(R.id.procB);
        Button sensor = findViewById(R.id.sensorButt);
        Button stat = findViewById(R.id.usageStat);

        stat.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Intent intent = new Intent(MainActivity.this,AppUsage.class);
                startActivity(intent);
            }
        });

        sensor.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Intent intent = new Intent(MainActivity.this,SensorActivity.class);
                startActivity(intent);
            }
        });

        fab.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Intent intent = new Intent(MainActivity.this,ListofAppActivity.class);
                startActivity(intent);
            }
        });
        scan.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent = new Intent(MainActivity.this,FileScan.class);
                startActivity(intent);
            }
        });

        bat.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent sample = new Intent("android.intent.action.POWER_USAGE_SUMMARY");
                startActivity(sample);
            }
        });

        ram.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent = new Intent(MainActivity.this, RamActivity.class);
                startActivity(intent);
            }
        });
    //    ListView listView = findViewById(R.id.listV);


//        final PackageManager pm = getPackageManager();
////get a list of installed apps.
//        List<ApplicationInfo> packages = pm.getInstalledApplications(PackageManager.GET_META_DATA);
//
//
//        List<String> output = new ArrayList<>();
//
//        for (ApplicationInfo packageInfo : packages) {
//            output.add(packageInfo.packageName);
//            Log.d(TAG, "Installed package :" + packageInfo.packageName);
//            Log.d(TAG, "Source dir : " + packageInfo.sourceDir);
//            Log.d(TAG, "Launch Activity :" + pm.getLaunchIntentForPackage(packageInfo.packageName));
//            Log.d(TAG, "Permission :" + packageInfo.permission);
//            //Get Permissions
//
//            PackageInfo packageInfoMy = null;
//            try {
//                packageInfoMy = pm.getPackageInfo(packageInfo.packageName, PackageManager.GET_PERMISSIONS);
//            } catch (PackageManager.NameNotFoundException e) {
//                e.printStackTrace();
//            }
//            String[] requestedPermissions = packageInfoMy.requestedPermissions;
//
//            if (requestedPermissions != null) {
//                for (int i = 0; i < requestedPermissions.length; i++) {
//                    Log.d("test", requestedPermissions[i]);
//                }
//
//            }
//
//        }
//        ArrayAdapter<String> adapter = new ArrayAdapter<>(this,
//                android.R.layout.simple_list_item_1, output);
//      //  listView.setAdapter(adapter);

    }
}
