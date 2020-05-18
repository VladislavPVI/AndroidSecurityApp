package com.example.androidsecurityapp.view;

import android.content.Context;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.widget.TextView;

import androidx.appcompat.app.AppCompatActivity;

import com.example.androidsecurityapp.R;

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
