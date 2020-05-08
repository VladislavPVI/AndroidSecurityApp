package com.example.androidsecurityapp;

import android.content.pm.PackageManager;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;

import androidx.constraintlayout.widget.ConstraintLayout;
import androidx.recyclerview.widget.RecyclerView;

import java.util.List;

public class MyAdapter extends RecyclerView.Adapter<MyAdapter.MyViewHolder> {
    private List<AppInfo> mDataset;
    private PackageManager pm;


    // Provide a reference to the views for each data item
    // Complex data items may need more than one view per item, and
    // you provide access to all the views for a data item in a view holder
    public class MyViewHolder extends RecyclerView.ViewHolder {
        // each data item is just a string in this case
        public TextView textView;
        public ImageView imageView;
        public ImageView alert;
        public TextView premissions;
        public TextView premissions2;
        public ConstraintLayout conLayout;
        public ConstraintLayout permiss;


        public MyViewHolder(View v) {
            super(v);

            textView = v.findViewById(R.id.textViewApp);
            imageView = v.findViewById(R.id.imageView3);
            premissions = v.findViewById(R.id.textView5);
            premissions2 = v.findViewById(R.id.textView2);
            alert = v.findViewById(R.id.imageView7);
            conLayout = v.findViewById(R.id.linearLayout2);
            permiss = v.findViewById(R.id.permiss);

            conLayout.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    AppInfo appInfo = mDataset.get(getAdapterPosition());
                    appInfo.setExpanded(!appInfo.isExpanded());
                    notifyItemChanged(getAdapterPosition());
                }
            });

        }
    }

    // Provide a suitable constructor (depends on the kind of dataset)
    public MyAdapter(List<AppInfo> appList, PackageManager packageManager) {
        this.mDataset = appList;
        this.pm = packageManager;
    }

    // Create new views (invoked by the layout manager)
    @Override
    public MyAdapter.MyViewHolder onCreateViewHolder(ViewGroup parent,
                                                     int viewType) {
        // create a new view
        View v = LayoutInflater.from(parent.getContext())
                .inflate(R.layout.list_app, parent, false);

        return new MyViewHolder(v);
    }

    // Replace the contents of a view (invoked by the layout manager)
    @Override
    public void onBindViewHolder(MyViewHolder holder, int position) {
        // - get element from your dataset at this position
        // - replace the contents of the view with that element
        AppInfo appInfo = mDataset.get(position);
        try {
            holder.imageView.setImageDrawable(pm.getApplicationIcon(appInfo.getPackageName()));
        } catch (PackageManager.NameNotFoundException e) {
            e.printStackTrace();
        }

        List<String> dangerousPremissons = appInfo.getDangerousPremissons();
        List<String> commonPremissons = appInfo.getCommonPremissons();

        if (dangerousPremissons.size() != 0){
            holder.alert.setVisibility(View.VISIBLE);
            holder.premissions.setVisibility(View.VISIBLE);
            holder.premissions.setText(dangerousPremissons.toString());
        }
        else {
            holder.alert.setVisibility(View.GONE);
            holder.premissions.setVisibility(View.GONE);
        }

        if (commonPremissons.size() != 0) {
            holder.premissions2.setVisibility(View.VISIBLE);
            holder.premissions2.setText(commonPremissons.toString());
        }
        else holder.premissions2.setVisibility(View.GONE);

        holder.textView.setText(appInfo.getLabel());

        boolean isExpanded = appInfo.isExpanded();
        holder.permiss.setVisibility(isExpanded ? View.VISIBLE : View.GONE);

    }

    // Return the size of your dataset (invoked by the layout manager)
    @Override
    public int getItemCount() {
        return mDataset.size();
    }
}