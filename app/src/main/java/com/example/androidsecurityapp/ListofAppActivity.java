package com.example.androidsecurityapp;

import android.content.pm.ApplicationInfo;
import android.content.pm.PackageInfo;
import android.content.pm.PackageManager;
import android.os.Bundle;

import androidx.appcompat.app.AppCompatActivity;
import androidx.recyclerview.widget.RecyclerView;

import java.util.ArrayList;
import java.util.List;

public class ListofAppActivity extends AppCompatActivity {

    private RecyclerView recyclerView;
    private RecyclerView.LayoutManager layoutManager;
    private RecyclerView.Adapter mAdapter;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_listof_app);
        recyclerView = (RecyclerView) findViewById(R.id.my_recycler_view);


        // specify an adapter (see also next example)

        List<AppInfo> appInfos = new ArrayList<>();

        try {
            appInfos = initData();
        } catch (PackageManager.NameNotFoundException e) {
            e.printStackTrace();
        }

        mAdapter = new MyAdapter(appInfos);
        recyclerView.setAdapter(mAdapter);

    }

    private List<AppInfo> initData() throws PackageManager.NameNotFoundException {
        List<AppInfo> appList = new ArrayList<>();
        PackageManager pm = getPackageManager();
        List<ApplicationInfo> packages = pm.getInstalledApplications(PackageManager.GET_META_DATA);

        for (ApplicationInfo ap : packages) {
            List<String> commonPremissons = new ArrayList<>();
            List<String> dangerousPremissons = new ArrayList<>();
            PackageInfo packageInfo = pm.getPackageInfo(ap.packageName, PackageManager.GET_PERMISSIONS);
            String[] stringArray = packageInfo.requestedPermissions;

            if (stringArray != null) {
                for (String perm : stringArray) {
                    if (DangerousPermissions.forCont(perm)) {
                        dangerousPremissons.add(perm);
                        continue;
                    }
                    commonPremissons.add(perm);
                }
            }
            AppInfo appInfo = new AppInfo(ap.loadLabel(pm).toString(),ap.loadIcon(pm),commonPremissons,dangerousPremissons);
            appList.add(appInfo);
        }
        return appList;
    }
}
