package com.example.androidsecurityapp;

import androidx.appcompat.app.AppCompatActivity;

import android.content.Context;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.preference.PreferenceManager;
import android.widget.TextView;

public class RootActivity extends AppCompatActivity {

    private static final String APP_PREFERENCES = "analyseData";

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_root2);
        TextView rootText = findViewById(R.id.rootInfo);
        SharedPreferences prefs = getSharedPreferences(APP_PREFERENCES, Context.MODE_PRIVATE);
        boolean rooted = prefs.getBoolean("root", false);
        if (rooted)
            rootText.setText(R.string.root);

    }
}
