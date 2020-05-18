package com.example.androidsecurityapp.view;

import android.content.Context;
import android.content.SharedPreferences;
import android.os.Bundle;

import androidx.appcompat.app.AppCompatActivity;
import androidx.recyclerview.widget.RecyclerView;

import com.example.androidsecurityapp.R;
import com.example.androidsecurityapp.adapter.MyAdapterForUsage;
import com.example.androidsecurityapp.model.MyUsageStats;
import com.google.gson.Gson;
import com.google.gson.reflect.TypeToken;

import java.util.List;

public class AppUsage extends AppCompatActivity {

    private RecyclerView recyclerView;
    private RecyclerView.Adapter mAdapter;
    public static final String APP_PREFERENCES = "analyseData";

    public AppUsage() {
    }

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_app_usage);
        SharedPreferences prefs = getSharedPreferences(APP_PREFERENCES, Context.MODE_PRIVATE);
        Gson gson = new Gson();


        String json = prefs.getString("myUsageStats", "");
        java.lang.reflect.Type type = new TypeToken<List<MyUsageStats>>() {
        }.getType();
        List<MyUsageStats> listStat = gson.fromJson(json, type);


        recyclerView = (RecyclerView) findViewById(R.id.recyclerUsage);
        mAdapter = new MyAdapterForUsage(listStat, getApplicationContext());
        recyclerView.setAdapter(mAdapter);

    }


}
