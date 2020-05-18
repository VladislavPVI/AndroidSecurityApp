package com.example.androidsecurityapp.adapter;

import android.content.Context;
import android.content.pm.PackageManager;
import android.text.format.Formatter;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;

import androidx.recyclerview.widget.RecyclerView;

import com.example.androidsecurityapp.R;
import com.example.androidsecurityapp.model.MyUsageStats;

import java.text.SimpleDateFormat;
import java.util.List;
import java.util.concurrent.TimeUnit;

public class MyAdapterForUsage extends RecyclerView.Adapter<MyAdapterForUsage.MyViewHolder> {

    private List<MyUsageStats> mDataset;
    private PackageManager pm;
    final SimpleDateFormat dateFormat = new SimpleDateFormat("d MMM yyyy HH:mm:ss");
    private Context appContext;


    // Provide a reference to the views for each data item
    // Complex data items may need more than one view per item, and
    // you provide access to all the views for a data item in a view holder
    public class MyViewHolder extends RecyclerView.ViewHolder {
        // each data item is just a string in this case
        private ImageView imageView;
        private TextView label;
        private TextView total;
        private TextView last;
        private TextView rxWIFI;
        private TextView txWIFI;
        private TextView rxMobile;
        private TextView txMobile;

        public MyViewHolder(View v) {
            super(v);

            imageView = v.findViewById(R.id.imageApk);
            label = v.findViewById(R.id.titleApk);
            total = v.findViewById(R.id.allTimeUsage);
            last = v.findViewById(R.id.apkPath);
            rxWIFI = v.findViewById(R.id.rxWIFI);
            txWIFI = v.findViewById(R.id.txWIFI);
            rxMobile = v.findViewById(R.id.rxMob);
            txMobile = v.findViewById(R.id.txMob);


            //OnClick потом напишу

            //Intent intent = new Intent(android.provider.Settings.ACTION_APPLICATION_DETAILS_SETTINGS);
            //intent.setData(Uri.parse("package:" + getPackageName()));
            //startActivity(intent);

        }
    }

    // Provide a suitable constructor (depends on the kind of dataset)
    public MyAdapterForUsage(List<MyUsageStats> apus, Context applicationContext) {
        this.mDataset = apus;
        this.pm = applicationContext.getPackageManager();
        this.appContext = applicationContext;
    }

    // Create new views (invoked by the layout manager)
    @Override
    public MyAdapterForUsage.MyViewHolder onCreateViewHolder(ViewGroup parent,
                                                             int viewType) {
        // create a new view
        View v = LayoutInflater.from(parent.getContext())
                .inflate(R.layout.list_app_usage, parent, false);

        return new MyAdapterForUsage.MyViewHolder(v);
    }

    // Replace the contents of a view (invoked by the layout manager)
    @Override
    public void onBindViewHolder(MyAdapterForUsage.MyViewHolder holder, int position) {
        // - get element from your dataset at this position
        // - replace the contents of the view with that element
        MyUsageStats myUsageStats = mDataset.get(position);

        try {
            holder.imageView.setImageDrawable(pm.getApplicationIcon(myUsageStats.getPackageName()));
        } catch (PackageManager.NameNotFoundException e) {
            e.printStackTrace();
        }

        holder.label.setText(myUsageStats.getLabel());
        holder.last.setText(dateFormat.format(myUsageStats.getLastTimeUsed()));

        long total = myUsageStats.getTotalTimeInForeground();

        long d = TimeUnit.MILLISECONDS.toDays(total);
        long h = TimeUnit.MILLISECONDS.toHours(total) - TimeUnit.DAYS.toHours(d);
        long m = TimeUnit.MILLISECONDS.toMinutes(total) - TimeUnit.HOURS.toMinutes(h) - TimeUnit.DAYS.toMinutes(d);
        long s = TimeUnit.MILLISECONDS.toSeconds(total) - TimeUnit.MINUTES.toSeconds(m) - TimeUnit.HOURS.toSeconds(h) - TimeUnit.DAYS.toSeconds(d);

        StringBuilder stringBuilder = new StringBuilder();
        if (d != 0) {
            stringBuilder.append(d);
            stringBuilder.append("d ");
        }
        if (h != 0) {
            stringBuilder.append(h);
            stringBuilder.append("h ");
        }
        if (m != 0) {
            stringBuilder.append(m);
            stringBuilder.append("m ");
        }
        if (s != 0) {
            stringBuilder.append(s);
            stringBuilder.append("s");
        }

        holder.total.setText(stringBuilder.toString());
        holder.rxMobile.setText(Formatter.formatFileSize(appContext, myUsageStats.getRXMobile()));
        holder.rxWIFI.setText(Formatter.formatFileSize(appContext, myUsageStats.getRXWIFI()));
        holder.txMobile.setText(Formatter.formatFileSize(appContext, myUsageStats.getTXMobile()));
        holder.txWIFI.setText(Formatter.formatFileSize(appContext, myUsageStats.getTXWIFI()));


    }

    // Return the size of your dataset (invoked by the layout manager)
    @Override
    public int getItemCount() {
        return mDataset.size();
    }
}
