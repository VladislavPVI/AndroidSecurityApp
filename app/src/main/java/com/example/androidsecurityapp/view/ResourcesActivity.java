package com.example.androidsecurityapp.view;

import android.Manifest;
import android.app.ActivityManager;
import android.app.usage.NetworkStats;
import android.app.usage.NetworkStatsManager;
import android.bluetooth.BluetoothAdapter;
import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.content.IntentFilter;
import android.content.pm.PackageManager;
import android.location.LocationManager;
import android.net.ConnectivityManager;
import android.os.BatteryManager;
import android.os.Bundle;
import android.os.Environment;
import android.os.RemoteException;
import android.os.StatFs;
import android.telephony.TelephonyManager;
import android.text.format.Formatter;
import android.view.View;
import android.widget.Button;
import android.widget.ProgressBar;
import android.widget.TextView;

import androidx.appcompat.app.AppCompatActivity;
import androidx.core.app.ActivityCompat;

import com.example.androidsecurityapp.R;

import java.util.Calendar;
import java.util.Timer;
import java.util.TimerTask;

public class ResourcesActivity extends AppCompatActivity {
    private NetworkStatsManager networkStatsManager;
    private ResourcesActivity.mBatInfoReceiver myBatInfoReceiver;

    private long[] mobileData;
    private long[] wiFiData;

    private long startTime;

    private Timer mTimer;
    private MyTimerTask mMyTimerTask;

    private TextView wifiUP;
    private TextView wifiDW;
    private TextView mobileUP;
    private TextView mobileDW;
    private TextView wifiTotal;
    private TextView mobTotal;
    private TextView wifiTotalU;
    private TextView mobTotalU;
    private ProgressBar ramBar;
    private TextView ramText;
    private TextView storageText;
    private ProgressBar storageBar;
    private TextView batteryText;
    private TextView batteryTemp;
    private ProgressBar batteryBar;
    private TextView blue;
    private TextView myGps;
    private Button batteryU;
    private Button appU;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_resources);

        wifiUP = findViewById(R.id.wifiUP);
        wifiDW = findViewById(R.id.wifiDown);
        mobileDW = findViewById(R.id.rxMob);
        mobileUP = findViewById(R.id.txMob);
        wifiTotal = findViewById(R.id.wifiTotal);
        mobTotal = findViewById(R.id.mobTotal);
        wifiTotalU = findViewById(R.id.wifiTotalUP);
        mobTotalU = findViewById(R.id.mobTotalUP);
        ramBar = findViewById(R.id.progressBarRam);
        ramText = findViewById(R.id.ramText);
        blue = findViewById(R.id.bluetStat);
        myGps = findViewById(R.id.gpsStat);

        batteryU = findViewById(R.id.batUsage);
        appU = findViewById(R.id.appUsage);

        batteryU.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent powerUsageIntent = new Intent(Intent.ACTION_POWER_USAGE_SUMMARY);
                startActivity(powerUsageIntent);
            }
        });


        appU.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent = new Intent(ResourcesActivity.this, AppUsage.class);
                startActivity(intent);
            }
        });

        storageText = findViewById(R.id.storageText);
        storageBar = findViewById(R.id.progressBarStorage);
        batteryText = findViewById(R.id.batteryPr);
        batteryTemp = findViewById(R.id.batteryTemp);
        batteryBar = findViewById(R.id.progressBarBattery);


        Calendar calendar = Calendar.getInstance();
        calendar.add(Calendar.MONTH, -1);
        startTime = calendar.getTimeInMillis();

        networkStatsManager = (NetworkStatsManager) getApplicationContext().getSystemService(Context.NETWORK_STATS_SERVICE);

        mobileData = getMobileData();
        wiFiData = getWiFiData();

        setRam();
        setStorage();




        mTimer = new Timer();
        mMyTimerTask = new MyTimerTask();
        mTimer.schedule(mMyTimerTask, 1000, 2000);
    }

    public void isGpsOn() {
        LocationManager manager = (LocationManager) getSystemService(Context.LOCATION_SERVICE);

        if (manager.isProviderEnabled(LocationManager.GPS_PROVIDER))
            myGps.setText("ON");
        else myGps.setText("OFF");


    }

    public void isBluetoothConnected() {
        BluetoothAdapter mBluetoothAdapter = BluetoothAdapter.getDefaultAdapter();
        if (mBluetoothAdapter != null && mBluetoothAdapter.isEnabled())
            blue.setText("ON");
        else blue.setText("OFF");
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
            batteryBar.setProgress(level);
            batteryText.setText(level + "%");
            batteryTemp.setText(String.valueOf(get_temp()) + "°C");
        }

    }

    public void setStorage() {
        StatFs stat = new StatFs(Environment.getExternalStorageDirectory().getPath());
        long bytesAvailable = stat.getBlockSizeLong() * stat.getAvailableBlocksLong();
        long totalM = stat.getTotalBytes();

        double percentAvail = (totalM - bytesAvailable) / (double) totalM * 100.0;

        storageBar.setProgress((int) percentAvail);

        String myStorageText = Formatter.formatFileSize(getApplicationContext(), bytesAvailable) + " / " + Formatter.formatFileSize(getApplicationContext(), totalM);
        storageText.setText(myStorageText);

    }


    public void setRam() {
        ActivityManager.MemoryInfo mi = new ActivityManager.MemoryInfo();
        ActivityManager activityManager = (ActivityManager) getSystemService(ACTIVITY_SERVICE);
        activityManager.getMemoryInfo(mi);

        long ramAV = mi.availMem;
        long ramTotal = mi.totalMem;

        double percentAvail = (ramTotal - ramAV) / (double) ramTotal * 100.0;

        ramBar.setProgress((int) percentAvail);

        String myRamText = Formatter.formatFileSize(getApplicationContext(), ramAV) + " / " + Formatter.formatFileSize(getApplicationContext(), ramTotal);
        ramText.setText(myRamText);
    }

    public long[] getMobileData() {
        NetworkStats.Bucket bucket;
        try {
            bucket = networkStatsManager.querySummaryForDevice(ConnectivityManager.TYPE_MOBILE,
                    getSubscriberId(ConnectivityManager.TYPE_MOBILE), startTime,
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
                    startTime,
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

    class MyTimerTask extends TimerTask {

        @Override
        public void run() {
            long[] newMobileData = getMobileData();
            long[] newWiFiData = getWiFiData();

            long mobileSpeedRx = (newMobileData[0] - mobileData[0]);
            long mobileSpeedTx = (newMobileData[1] - mobileData[1]);
            long wifiSpeedRx = (newWiFiData[0] - wiFiData[0]);
            long wifiSpeedTx = (newWiFiData[1] - wiFiData[1]);


            mobileData = newMobileData;
            wiFiData = newWiFiData;

            final String wifiU = Formatter.formatFileSize(getApplicationContext(), wifiSpeedTx);
            final String wifiD = Formatter.formatFileSize(getApplicationContext(), wifiSpeedRx);
            final String mobU = Formatter.formatFileSize(getApplicationContext(), mobileSpeedTx);
            final String mobD = Formatter.formatFileSize(getApplicationContext(), mobileSpeedRx);

            final String totalM = Formatter.formatFileSize(getApplicationContext(), newMobileData[0]);
            final String totalW = Formatter.formatFileSize(getApplicationContext(), newWiFiData[0]);

            final String totalMU = Formatter.formatFileSize(getApplicationContext(), newMobileData[1]);
            final String totalWU = Formatter.formatFileSize(getApplicationContext(), newWiFiData[1]);


            runOnUiThread(new Runnable() {

                @Override
                public void run() {
                    wifiUP.setText(wifiU);
                    wifiDW.setText(wifiD);
                    mobileDW.setText(mobU);
                    mobileUP.setText(mobD);
                    wifiTotal.setText(totalW);
                    mobTotal.setText(totalM);
                    wifiTotalU.setText(totalWU);
                    mobTotalU.setText(totalMU);
                    isBluetoothConnected();
                    isGpsOn();

                }
            });
        }
    }

    @Override
    protected void onResume() {
        super.onResume();
        myBatInfoReceiver = new ResourcesActivity.mBatInfoReceiver();

        this.registerReceiver(this.myBatInfoReceiver,
                new IntentFilter(Intent.ACTION_BATTERY_CHANGED));
    }

    @Override
    protected void onPause() {
        super.onPause();
        unregisterReceiver(myBatInfoReceiver);
    }
}
