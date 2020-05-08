package com.example.androidsecurityapp;

import androidx.annotation.RequiresApi;
import androidx.appcompat.app.AppCompatActivity;

import android.accounts.Account;
import android.accounts.AccountManager;
import android.app.ActivityManager;
import android.app.Application;
import android.app.admin.DevicePolicyManager;
import android.content.Context;
import android.content.SharedPreferences;
import android.content.res.Configuration;
import android.content.res.Resources;
import android.net.ConnectivityManager;
import android.net.NetworkInfo;
import android.os.Build;
import android.os.Bundle;
import android.preference.PreferenceManager;
import android.provider.Settings;
import android.util.DisplayMetrics;
import android.util.Log;
import android.view.Gravity;
import android.widget.Toast;

import com.example.androidsecurityapp.availableSettings.Global;
import com.example.androidsecurityapp.availableSettings.Secure;
import com.example.androidsecurityapp.availableSettings.SystemS;
import com.google.gson.Gson;
import com.google.gson.reflect.TypeToken;

import java.io.BufferedReader;
import java.io.File;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.lang.reflect.Field;
import java.lang.reflect.InvocationTargetException;
import java.lang.reflect.Method;
import java.time.Instant;
import java.time.LocalDateTime;
import java.util.HashMap;
import java.util.List;

public class RamActivity extends AppCompatActivity {

    private static final String TAG = "STTINGS INFO";

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_ram);
        boolean root = isRootGiven();

        int br = getScreenBrightnessInt255();

        Toast toast = Toast.makeText(getApplicationContext(),
                "ROOT " + root + "\n" + br,
                Toast.LENGTH_SHORT);
        toast.setGravity(Gravity.CENTER, 0, 0);
        toast.show();
        HashMap<String, String> fieldValue = getSettings();
        saveHashMap("mySettings",fieldValue);
        HashMap<String, String> mySetings = getHashMap("mySettings");

        SharedPreferences prefs = PreferenceManager.getDefaultSharedPreferences(this);
        String time = prefs.getString("saveData","");

        System.out.println("wfwrf");


    }

    public void saveHashMap(String key , Object obj) {
        SharedPreferences prefs = PreferenceManager.getDefaultSharedPreferences(this);
        SharedPreferences.Editor editor = prefs.edit();
        Gson gson = new Gson();
        String json = gson.toJson(obj);
        editor.putString("saveData", Instant.now().toString());


        editor.putString(key,json);
        editor.apply();     // This line is IMPORTANT !!!
    }

    public HashMap<String,String> getHashMap(String key) {
        SharedPreferences prefs = PreferenceManager.getDefaultSharedPreferences(this);
        Gson gson = new Gson();
        String json = prefs.getString(key,"");
        java.lang.reflect.Type type = new TypeToken<HashMap<String,String>>(){}.getType();
        HashMap<String, String> obj = gson.fromJson(json, type);
        return obj;
    }

    public HashMap<String, String> getSettings(){
        HashMap<String, String> fieldValue = new HashMap<>();
        for (Secure s : Secure.values()) {
            String setting = s.toString().toLowerCase();
            String value = Settings.Secure.getString(getContentResolver(), setting);
            if (value == null)
                fieldValue.put(setting,"0");

            else fieldValue.put(setting, value);
        }
        for (Global s : Global.values()) {
            String setting = s.toString().toLowerCase();
            String value = Settings.Global.getString(getContentResolver(), setting);
            if (value == null)
                fieldValue.put(setting,"0");

            else fieldValue.put(setting, value);
        }

        for (SystemS s : SystemS.values()) {
            String setting = s.toString().toLowerCase();
            String value = Settings.System.getString(getContentResolver(), setting);
            if (value == null) fieldValue.put(setting,"0");

            else fieldValue.put(setting, value);
        }
        String PASSWORD_TYPE_KEY = "lockscreen.password_type";

        long mode = android.provider.Settings.Secure.getLong(getContentResolver(), "lockscreen.password_type",
                DevicePolicyManager.PASSWORD_QUALITY_SOMETHING);
        fieldValue.put(PASSWORD_TYPE_KEY,String.valueOf(mode));

        DisplayMetrics metrics = new DisplayMetrics();
        getWindowManager().getDefaultDisplay().getMetrics(metrics);
        StringBuilder builder = new StringBuilder();
        builder.append(metrics.widthPixels);
        builder.append("x");
        builder.append(metrics.heightPixels);

        fieldValue.put("DisplayMetric",builder.toString());
        fieldValue.put("language", Resources.getSystem().getConfiguration().locale.getLanguage());
        Account[] accounts = AccountManager.get(this).getAccounts();

        builder = new StringBuilder();

        for (Account account : accounts)
            builder.append(account.name).append(" ");
        fieldValue.put("accounts",builder.toString());

//        final String service = Context.CONNECTIVITY_SERVICE;
//        final ConnectivityManager manager = (ConnectivityManager) this.getSystemService(service);
//        manager.getActiveNetworkInfo();
        return fieldValue;


    }



    public int getScreenBrightnessInt255() {
        int screenBrightness = 255;
        try {
            int string = Settings.System.getInt(getContentResolver(), Settings.System.ACCELEROMETER_ROTATION);
            screenBrightness = Settings.System.getInt(getContentResolver(), Settings.System.SCREEN_BRIGHTNESS);
        } catch (Exception e) {
            e.printStackTrace();
        }
        return screenBrightness;
    }


    public static boolean isRootAvailable() {
        for (String pathDir : System.getenv("PATH").split(":")) {
            if (new File(pathDir, "su").exists()) {
                return true;
            }
        }
        return false;
    }

    public static boolean isRootGiven() {
        if (isRootAvailable()) {
            Process process = null;
            try {
                process = Runtime.getRuntime().exec(new String[]{"su", "-c", "id"});
                BufferedReader in = new BufferedReader(new InputStreamReader(process.getInputStream()));
                String output = in.readLine();
                if (output != null && output.toLowerCase().contains("uid=0"))
                    return true;
            } catch (Exception e) {
                e.printStackTrace();
            } finally {
                if (process != null)
                    process.destroy();
            }
        }

        return false;
    }

}
