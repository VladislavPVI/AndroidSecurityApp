package com.example.androidsecurityapp;

import androidx.appcompat.app.AppCompatActivity;
import androidx.recyclerview.widget.RecyclerView;

import android.app.usage.UsageStats;
import android.app.usage.UsageStatsManager;
import android.content.Context;
import android.os.Bundle;
import android.text.format.DateUtils;
import android.util.ArrayMap;
import android.util.Log;

import java.text.DateFormat;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.Collection;
import java.util.HashMap;
import java.util.List;
import java.util.Set;

public class AppUsage extends AppCompatActivity {

    private RecyclerView recyclerView;
    private RecyclerView.LayoutManager layoutManager;
    private RecyclerView.Adapter mAdapter;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_app_usage);
        final SimpleDateFormat dateFormat = new SimpleDateFormat("M-d-yyyy HH:mm:ss");

        Calendar calendar = Calendar.getInstance();
        long endTime = calendar.getTimeInMillis();
        calendar.add(Calendar.YEAR, -1);
        long startTime = calendar.getTimeInMillis();

        Log.d("TIME", "Range start:" + dateFormat.format(startTime));
        Log.d("TIME", "Range end:" + dateFormat.format(endTime));


        UsageStatsManager mUsageStatsManager = (UsageStatsManager) getSystemService(Context.USAGE_STATS_SERVICE);
        final List<UsageStats> stats =
                mUsageStatsManager.queryUsageStats(UsageStatsManager.INTERVAL_BEST, startTime, endTime);

        HashMap<String, MyUsageStats> map = new HashMap<>();

        for (UsageStats st : stats) {
            String packageName = st.getPackageName();
            MyUsageStats myUsageStats = map.get(packageName);
            if (myUsageStats == null)
                map.put(packageName, new MyUsageStats(st.getTotalTimeInForeground(), st.getLastTimeUsed(),packageName));
            else {
                if (st.getLastTimeUsed() > myUsageStats.getLastTimeUsed())
                    myUsageStats.setLastTimeUsed(st.getLastTimeUsed());
                myUsageStats.setTotalTimeInForeground(myUsageStats.getTotalTimeInForeground() + st.getTotalTimeInForeground());
            }
        }


        recyclerView = (RecyclerView) findViewById(R.id.recyclerUsage);

        List<MyUsageStats> listStat = new ArrayList<>();

        listStat.addAll(map.values());

        mAdapter = new MyAdapterForUsage(listStat, getPackageManager());

        recyclerView.setAdapter(mAdapter);

    }
}
