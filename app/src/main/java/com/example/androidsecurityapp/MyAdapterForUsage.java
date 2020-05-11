package com.example.androidsecurityapp;

import android.content.pm.PackageManager;
import android.util.ArrayMap;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;

import androidx.constraintlayout.widget.ConstraintLayout;
import androidx.recyclerview.widget.RecyclerView;

import java.util.List;

public class MyAdapterForUsage extends RecyclerView.Adapter<MyAdapterForUsage.MyViewHolder> {

    private List<MyUsageStats> mDataset;
    private PackageManager pm;


    // Provide a reference to the views for each data item
    // Complex data items may need more than one view per item, and
    // you provide access to all the views for a data item in a view holder
    public class MyViewHolder extends RecyclerView.ViewHolder {
        // each data item is just a string in this case
        private ImageView imageView;
        private TextView label;
        private TextView total;
        private TextView last;

        public MyViewHolder(View v) {
            super(v);

            imageView = v.findViewById(R.id.imageUsage);
            label = v.findViewById(R.id.titleUsage);
            total = v.findViewById(R.id.allTimeUsage);
            last = v.findViewById(R.id.lastTimeUsage);

        }
    }

    // Provide a suitable constructor (depends on the kind of dataset)
    public MyAdapterForUsage(List<MyUsageStats>  apus, PackageManager packageManager) {
        this.mDataset = apus;
        this.pm = packageManager;
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

        holder.label.setText(myUsageStats.getPackageName());
        holder.last.setText(String.valueOf(myUsageStats.getLastTimeUsed()));
        holder.total.setText(String.valueOf(myUsageStats.getTotalTimeInForeground()));


    }

    // Return the size of your dataset (invoked by the layout manager)
    @Override
    public int getItemCount() {
        return mDataset.size();
    }
}
