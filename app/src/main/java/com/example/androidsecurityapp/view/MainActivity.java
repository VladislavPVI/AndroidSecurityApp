package com.example.androidsecurityapp.view;

import android.Manifest;
import android.accounts.Account;
import android.accounts.AccountManager;
import android.annotation.SuppressLint;
import android.app.AlertDialog;
import android.app.AppOpsManager;
import android.app.admin.DevicePolicyManager;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.SharedPreferences;
import android.content.pm.PackageManager;
import android.content.res.Resources;
import android.os.Build;
import android.os.Bundle;
import android.os.Handler;
import android.provider.Settings;
import android.util.DisplayMetrics;
import android.view.View;
import android.widget.Button;
import android.widget.ImageButton;
import android.widget.LinearLayout;
import android.widget.ProgressBar;
import android.widget.TextView;

import androidx.appcompat.app.AppCompatActivity;
import androidx.core.app.ActivityCompat;
import androidx.core.content.ContextCompat;

import com.example.androidsecurityapp.Analyse;
import com.example.androidsecurityapp.R;
import com.example.androidsecurityapp.model.availableSettings.Global;
import com.example.androidsecurityapp.model.availableSettings.Secure;
import com.example.androidsecurityapp.model.availableSettings.SystemS;
import com.google.gson.Gson;

import java.io.File;
import java.text.SimpleDateFormat;
import java.time.Instant;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;

public class MainActivity extends AppCompatActivity {
    private static final int MY_PERMISSIONS_REQUEST = 1;

    final SimpleDateFormat dateFormat = new SimpleDateFormat("d MMM yyyy HH:mm:ss");
    SharedPreferences prefs;
    public static final String APP_PREFERENCES = "analyseData";
    private TextView date;
    private Analyse analyseData;
    private long analyseDate;
    private ProgressBar progressBar;
    private ProgressBar loading;
    private TextView scoreText;
    private LinearLayout buttonArea;
    private TextView hint;
    private Handler handler = new Handler();

    private TextView permText;
    private TextView rootText;
    private TextView apkText;
    private TextView resText;
    private TextView setText;
    private Button analyse;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        setContentView(R.layout.activity_main);
        progressBar = findViewById(R.id.progressBarMain);
        TextView modelName = findViewById(R.id.textViewModel);
        date = findViewById(R.id.dateCheck);
        scoreText = findViewById(R.id.scoreText);
        buttonArea = findViewById(R.id.buttonsArea);
        loading = findViewById(R.id.loading);
        hint = findViewById(R.id.hint);
        analyse = findViewById(R.id.buttonAnalyse);


        List<String> needPerm = new ArrayList();


        if (ContextCompat.checkSelfPermission(MainActivity.this,
                Manifest.permission.READ_PHONE_STATE)
                != PackageManager.PERMISSION_GRANTED)
            needPerm.add(Manifest.permission.READ_PHONE_STATE);
        if (ContextCompat.checkSelfPermission(MainActivity.this,
                Manifest.permission.READ_EXTERNAL_STORAGE)
                != PackageManager.PERMISSION_GRANTED)
            needPerm.add(Manifest.permission.READ_EXTERNAL_STORAGE);


     if (needPerm.size()!=0) {
           ActivityCompat.requestPermissions(MainActivity.this,needPerm.toArray(new String[0]),
                  MY_PERMISSIONS_REQUEST);

        }


        permText = findViewById(R.id.permText);
        rootText = findViewById(R.id.rootText);
        apkText = findViewById(R.id.apkText);
        resText = findViewById(R.id.resText);
        setText = findViewById(R.id.settingsText);


        date.setText(dateFormat.format(analyseDate));
        modelName.setText(Build.MODEL);
        //progressBar.setProgress(94);

        ImageButton perm = findViewById(R.id.imageButtonPerm);

        perm.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent = new Intent(MainActivity.this, ListofAppActivity.class);
                startActivity(intent);
            }
        });

        ImageButton rootB = findViewById(R.id.imageButtonRoot);

        rootB.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent = new Intent(MainActivity.this, RootActivity.class);
                startActivity(intent);
            }
        });

        ImageButton scan = findViewById(R.id.imageButtonApk);

        scan.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent = new Intent(MainActivity.this, FileScan.class);
                startActivity(intent);
            }
        });

        ImageButton settings = findViewById(R.id.imageButtonSettings);

        settings.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent = new Intent(MainActivity.this, SettingsActivity.class);
                startActivity(intent);
            }
        });

        ImageButton monitor = findViewById(R.id.imageButtonRes);
        monitor.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {

                Intent intent = new Intent(MainActivity.this, ResourcesActivity.class);
                startActivity(intent);

            }
        });


        analyse.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {


                    progressBar.setVisibility(View.GONE);
                    date.setVisibility(View.GONE);
                    scoreText.setVisibility(View.GONE);
                    loading.setVisibility(View.VISIBLE);
                    buttonArea.setVisibility(View.GONE);
                    analyse.setEnabled(false);
                    hint.setText(R.string.analyseHint);

                    new Thread(new Runnable() {
                        public void run() {
                            backgroundThreadProcessing();
                            handler.post(new Runnable() {
                                public void run() {
                                    analyse.setEnabled(true);
                                    loading.setVisibility(View.GONE);
                                    progressBar.setVisibility(View.VISIBLE);
                                    date.setVisibility(View.VISIBLE);
                                    scoreText.setVisibility(View.VISIBLE);
                                    buttonArea.setVisibility(View.VISIBLE);
                                    updateViewData();
                                }
                            });
                        }
                    }).start();
                }
             });


    }


    public void updateViewData() {
        date.setText(dateFormat.format(analyseDate));
        int score = analyseData.getScore();
        if (score > 90) {
            scoreText.setText(score + "/100\n EXCELLENT!");
            hint.setText(R.string.ex);
        } else if (score > 80) {
            scoreText.setText(score + "/100\n GOOD!");
            hint.setText(R.string.good);
        } else if (score > 65) {
            scoreText.setText(score + "/100\n SATISFACTORY!");
            hint.setText(R.string.satis);
        } else {
            scoreText.setText(score + "/100\n IN DANGER!");
            hint.setText(R.string.dang);
        }

        permText.setText(analyseData.getPermissonText());
        rootText.setText(analyseData.getRootText());
        apkText.setText(analyseData.getApkText());
        resText.setText(analyseData.getResText());
        setText.setText(analyseData.getSettingsText());

    }

    @Override
    protected void onResume() {
        super.onResume();
        if (!checkUsagePerm()) {
            Intent intent = new Intent(Settings.ACTION_USAGE_ACCESS_SETTINGS);
            startActivity(intent);
        }
            prefs = getSharedPreferences(APP_PREFERENCES, Context.MODE_PRIVATE);
        analyseData = new Analyse(prefs, getApplicationContext());
        analyseDate = prefs.getLong("analyseDate", 0);
        if (analyseDate != 0) {
            analyseData.loadSavedData();
            updateViewData();
        } else {
            progressBar.setVisibility(View.GONE);
            scoreText.setVisibility(View.GONE);
            buttonArea.setVisibility(View.GONE);
            date.setVisibility(View.GONE);

        }

    }

    @Override
    protected void onPause() {
        super.onPause();
        if (analyseDate != 0) {
            analyseData.saveData();
        }
    }

    public List<String> getStorageDirectories() {

        File[] externalDirs = getExternalFilesDirs(null);

        List<String> results = new ArrayList<>();
        for (File file : externalDirs) {
            String path = file.getPath().split("/Android")[0];
            results.add(path);
        }
        return results;
    }


    public void saveHashMap(String key, Object obj) {
        Gson gson = new Gson();
        String json = gson.toJson(obj);
        saveJson(json, key);
    }

    public void saveJson(String json, String key) {
        SharedPreferences.Editor editor = prefs.edit();
        editor.putLong(key + "Date", Instant.now().toEpochMilli());
        editor.putString(key, json);
        editor.apply();

    }


    private HashMap<String, String> getSettings() {
        HashMap<String, String> fieldValue = new HashMap<>();
        for (Secure s : Secure.values()) {
            String setting = s.toString().toLowerCase();
            String value = Settings.Secure.getString(getContentResolver(), setting);
            if (value == null)
                fieldValue.put(setting, "0");

            else fieldValue.put(setting, value);
        }
        for (Global s : Global.values()) {
            String setting = s.toString().toLowerCase();
            String value = Settings.Global.getString(getContentResolver(), setting);
            if (value == null)
                fieldValue.put(setting, "0");

            else fieldValue.put(setting, value);
        }

        for (SystemS s : SystemS.values()) {
            String setting = s.toString().toLowerCase();
            String value = Settings.System.getString(getContentResolver(), setting);
            if (value == null) fieldValue.put(setting, "0");

            else fieldValue.put(setting, value);
        }
        String PASSWORD_TYPE_KEY = "lockscreen.password_type";

        long mode = android.provider.Settings.Secure.getLong(getContentResolver(), "lockscreen.password_type",
                DevicePolicyManager.PASSWORD_QUALITY_SOMETHING);
        fieldValue.put(PASSWORD_TYPE_KEY, String.valueOf(mode));

        DisplayMetrics metrics = new DisplayMetrics();
        getWindowManager().getDefaultDisplay().getMetrics(metrics);
        StringBuilder builder = new StringBuilder();
        builder.append(metrics.widthPixels);
        builder.append("x");
        builder.append(metrics.heightPixels);

        fieldValue.put("DisplayMetric", builder.toString());
        fieldValue.put("language", Resources.getSystem().getConfiguration().locale.getLanguage());
        Account[] accounts = AccountManager.get(this).getAccounts();

        builder = new StringBuilder();

        for (Account account : accounts)
            builder.append(account.name).append(" ");
        fieldValue.put("accounts", builder.toString());

//        final String service = Context.CONNECTIVITY_SERVICE;
//        final ConnectivityManager manager = (ConnectivityManager) this.getSystemService(service);
//        manager.getActiveNetworkInfo();
        return fieldValue;


    }
private boolean checkUsagePerm(){
        AppOpsManager appOps = (AppOpsManager) getSystemService(Context.APP_OPS_SERVICE);
        int mode = appOps.checkOpNoThrow(AppOpsManager.OPSTR_GET_USAGE_STATS,
                android.os.Process.myUid(), getPackageName());
        return mode == AppOpsManager.MODE_ALLOWED;
    }
    @Override
    public void onRequestPermissionsResult(int requestCode,
                                           String[] permissions, int[] grantResults) {
        if (requestCode == MY_PERMISSIONS_REQUEST) {// If request is cancelled, the result arrays are empty.
            if (grantResults.length > 0
                    && grantResults[0] == PackageManager.PERMISSION_GRANTED) {
                // permission was granted, yay! Do the
                // contacts-related task you need to do.
            } else {
                analyse.setEnabled(false);

            }
            return;

            // other 'case' lines to check for other
            // permissions this app might request.
        }
    }
    private void backgroundThreadProcessing() {
        if (analyseDate == 0) {
            analyseData.initDataAnalyse(getSettings());
        } else analyseData.updateData(getSettings());
        analyseDate = analyseData.getSaveDate();
    }


}
