package com.example.androidsecurityapp;

import androidx.appcompat.app.AppCompatActivity;
import androidx.recyclerview.widget.RecyclerView;

import android.accounts.Account;
import android.accounts.AccountManager;
import android.app.admin.DevicePolicyManager;
import android.content.Context;
import android.content.SharedPreferences;
import android.content.res.Resources;
import android.os.Bundle;
import android.preference.PreferenceManager;
import android.provider.Settings;
import android.util.DisplayMetrics;
import android.view.View;
import android.widget.Button;

import com.example.androidsecurityapp.availableSettings.Global;
import com.example.androidsecurityapp.availableSettings.Secure;
import com.example.androidsecurityapp.availableSettings.SystemS;
import com.google.gson.Gson;
import com.google.gson.reflect.TypeToken;

import java.time.Instant;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Set;

public class SettingsActivity extends AppCompatActivity {

    private static final String APP_PREFERENCES = "analyseData";
    private SharedPreferences prefs;
    private List<ChangesOfSettings> settingsChanges;
    private RecyclerView recyclerView;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_settings);
        recyclerView = findViewById(R.id.recyclerSettings);
        prefs = getSharedPreferences(APP_PREFERENCES, Context.MODE_PRIVATE);


    }
    @Override
    protected void onPause() {
        Gson gson = new Gson();
        SharedPreferences.Editor editor = prefs.edit();
        String json = gson.toJson(settingsChanges);
        editor.putString("settingsChanges", json);
        editor.apply();
        super.onPause();
    }

    @Override
    protected void onResume() {
        super.onResume();
        Gson gson = new Gson();

        String json = prefs.getString("settingsChanges", "");
        java.lang.reflect.Type type = new TypeToken<List<ChangesOfSettings>>() {
        }.getType();
        settingsChanges = gson.fromJson(json, type);

        if (settingsChanges != null && settingsChanges.size()!=0){
            MyAdapterForSettings mAdapter = new MyAdapterForSettings(settingsChanges);
            recyclerView.setAdapter(mAdapter);
        }
    }
}




