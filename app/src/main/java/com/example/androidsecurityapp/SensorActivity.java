package com.example.androidsecurityapp;

import androidx.appcompat.app.AppCompatActivity;
import androidx.core.app.ActivityCompat;

import android.Manifest;
import android.app.Activity;
import android.app.ActivityManager;
import android.app.AppOpsManager;
import android.app.usage.NetworkStats;
import android.app.usage.NetworkStatsManager;
import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.content.IntentFilter;
import android.content.pm.PackageManager;
import android.net.ConnectivityManager;
import android.net.NetworkCapabilities;
import android.os.BatteryManager;
import android.os.Bundle;
import android.os.Environment;
import android.os.RemoteException;
import android.os.StatFs;
import android.provider.Settings;
import android.telephony.TelephonyManager;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.ProgressBar;
import android.widget.TextView;

import java.util.Timer;
import java.util.TimerTask;

public class SensorActivity extends AppCompatActivity {
    private ProgressBar thermometer;
    private mBatInfoReceiver myBatInfoReceiver;
    private NetworkStatsManager networkStatsManager;

    private long[] mobileData;
    private long[] wiFiData;


    private Timer mTimer;
    private MyTimerTask mMyTimerTask;
    private TextView netInf;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_sensor);
        thermometer = (ProgressBar) findViewById(R.id.progressBar);
        Button upd = findViewById(R.id.updateTemp);
        final TextView tempT = findViewById(R.id.textTEMP);
        netInf = findViewById(R.id.textViewForNet);


        verifyStoragePermissions(this);
        myBatInfoReceiver = new mBatInfoReceiver();

        this.registerReceiver(this.myBatInfoReceiver,
                new IntentFilter(Intent.ACTION_BATTERY_CHANGED));

        upd.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                float temp = myBatInfoReceiver.get_temp();
                thermometer.setProgress((int) temp);
                tempT.setText(String.valueOf(temp) + "°C");


            }
        });
        ActivityManager.MemoryInfo mi = new ActivityManager.MemoryInfo();
        ActivityManager activityManager = (ActivityManager) getSystemService(ACTIVITY_SERVICE);
        activityManager.getMemoryInfo(mi);

        double availableMegs = mi.availMem / 0x100000L;
        // 1024 bytes      == 1 Kibibyte
        // 1024 Kibibyte   == 1 Mebibyte
        // 1024 * 1024     == 1048576
        // 1048576         == 0x100000 (Hexadecimal)

//Percentage can be calculated for API 16+
        double total = mi.totalMem;
        double percentAvail = mi.availMem / (double) mi.totalMem * 100.0;

        StatFs stat = new StatFs(Environment.getExternalStorageDirectory().getPath());
        long bytesAvailable;
        bytesAvailable = stat.getBlockSizeLong() * stat.getAvailableBlocksLong();
        long totalM = stat.getTotalBytes() / (1024 * 1024);

        long megAvailable = bytesAvailable / (1024 * 1024);
        Log.e("", "Available MB : " + megAvailable);

        if (!packageUsageStatsAllowed()) {
            Intent intent = new Intent(Settings.ACTION_USAGE_ACCESS_SETTINGS);
            startActivity(intent);
        }
        networkStatsManager = (NetworkStatsManager) getApplicationContext().getSystemService(Context.NETWORK_STATS_SERVICE);

        mobileData = getMobileData();
        wiFiData = getWiFiData();

        mTimer = new Timer();
        mMyTimerTask = new MyTimerTask();
        mTimer.schedule(mMyTimerTask, 1000, 1000);


    }

    public long[] getMobileData() {
        NetworkStats.Bucket bucket;
        try {
            bucket = networkStatsManager.querySummaryForDevice(ConnectivityManager.TYPE_MOBILE,
                    getSubscriberId(ConnectivityManager.TYPE_MOBILE), 0,
                    System.currentTimeMillis());
        } catch (RemoteException e) {
            return new long[]{-1, -1};
        }
        return new long[]{bucket.getRxBytes(), bucket.getTxBytes()};
    }

    public long[] getWiFiData() {
        NetworkStats.Bucket bucket;
        try {
            bucket = networkStatsManager.querySummaryForDevice(ConnectivityManager.TYPE_WIFI,
                    "",
                    0,
                    System.currentTimeMillis());
        } catch (RemoteException e) {
            return new long[]{-1, -1};
        }
        return new long[]{bucket.getRxBytes(), bucket.getTxBytes()};
    }

    //Here Manifest.permission.READ_PHONE_STATS is needed
    private String getSubscriberId(int networkType) {
        if (ConnectivityManager.TYPE_MOBILE == networkType) {
            TelephonyManager tm = (TelephonyManager) getApplicationContext().getSystemService(Context.TELEPHONY_SERVICE);
            if (ActivityCompat.checkSelfPermission(this, Manifest.permission.READ_PHONE_STATE) != PackageManager.PERMISSION_GRANTED) {
                ActivityCompat.requestPermissions(this,
                        new String[]{Manifest.permission.READ_PHONE_STATE},
                        1);
            }
            return tm.getSubscriberId();
        }
        return "";
    }

    public boolean packageUsageStatsAllowed() {
        AppOpsManager appOps = (AppOpsManager) getSystemService(Context.APP_OPS_SERVICE);
        int mode = appOps.checkOpNoThrow(AppOpsManager.OPSTR_GET_USAGE_STATS,
                android.os.Process.myUid(), getPackageName());
        return mode == AppOpsManager.MODE_ALLOWED;
    }

    // Storage Permissions
    private static final int REQUEST_EXTERNAL_STORAGE = 1;
    private static String[] PERMISSIONS_STORAGE = {
            Manifest.permission.READ_EXTERNAL_STORAGE,
            Manifest.permission.WRITE_EXTERNAL_STORAGE
    };

    /**
     * Checks if the app has permission to write to device storage
     * <p>
     * If the app does not has permission then the user will be prompted to grant permissions
     *
     * @param activity
     */
    public static void verifyStoragePermissions(Activity activity) {
        // Check if we have write permission
        int permission = ActivityCompat.checkSelfPermission(activity, Manifest.permission.WRITE_EXTERNAL_STORAGE);

        if (permission != PackageManager.PERMISSION_GRANTED) {
            // We don't have permission so prompt the user
            ActivityCompat.requestPermissions(
                    activity,
                    PERMISSIONS_STORAGE,
                    REQUEST_EXTERNAL_STORAGE
            );
        }
    }

    private class mBatInfoReceiver extends BroadcastReceiver {

        int temp = 0;
        int level = 0;

        float get_temp() {
            return (float) (temp / 10);
        }

        @Override
        public void onReceive(Context arg0, Intent intent) {
            temp = intent.getIntExtra(BatteryManager.EXTRA_TEMPERATURE, 0);
            level = intent.getIntExtra(BatteryManager.EXTRA_LEVEL, 0);
        }

    }

    class MyTimerTask extends TimerTask {

        @Override
        public void run() {
            long[] newMobileData = getMobileData();
            long[] newWiFiData = getWiFiData();

            long mobileSpeedRx = (newMobileData[0] - mobileData[0]) / 1000;
            long mobileSpeedTx = (newMobileData[1] - mobileData[1]) / 1000;
            long wifiSpeedRx = (newWiFiData[0] - wiFiData[0]) / 1000;
            long wifiSpeedTx = (newWiFiData[1] - wiFiData[1]) / 1000;

            mobileData = newMobileData;
            wiFiData = newWiFiData;

            final String text = "Mobile " + "DW: " + mobileSpeedRx + " UP: " + mobileSpeedTx + "\nWIFI " + "DW: " + wifiSpeedRx + " UP: " + wifiSpeedTx;

            runOnUiThread(new Runnable() {

                @Override
                public void run() {
                    netInf.setText(text);
                }
            });
        }
    }


}
