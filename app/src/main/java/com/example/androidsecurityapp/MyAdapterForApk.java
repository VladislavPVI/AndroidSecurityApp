package com.example.androidsecurityapp;

import android.app.AlertDialog;
import android.content.DialogInterface;
import android.content.pm.PackageManager;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;

import androidx.constraintlayout.widget.ConstraintLayout;
import androidx.recyclerview.widget.RecyclerView;

import java.io.File;
import java.text.SimpleDateFormat;
import java.util.List;
import java.util.concurrent.TimeUnit;

public class MyAdapterForApk extends RecyclerView.Adapter<MyAdapterForApk.MyViewHolder> {
    private List<ApkInfo> mDataset;
    final SimpleDateFormat dateFormat = new SimpleDateFormat("d MMM yyyy HH:mm:ss");


    // Provide a reference to the views for each data item
    // Complex data items may need more than one view per item, and
    // you provide access to all the views for a data item in a view holder
    public class MyViewHolder extends RecyclerView.ViewHolder {
        // each data item is just a string in this case
        private ImageView imageView;
        private TextView label;
        private TextView date;
        private TextView path;
        private TextView size;
        private ConstraintLayout constraintLayout;


        public MyViewHolder(View v) {
            super(v);

            imageView = v.findViewById(R.id.imageApk);
            label = v.findViewById(R.id.titleApk);
            date = v.findViewById(R.id.apkdate);
            path = v.findViewById(R.id.apkPath);
            size = v.findViewById(R.id.apksize);
            constraintLayout = v.findViewById(R.id.layoutApk);

            constraintLayout.setOnLongClickListener(new View.OnLongClickListener() {
                @Override
                public boolean onLongClick(View v) {
                    final ApkInfo apkInfo = mDataset.get(getAdapterPosition());
                    final File file = new File(apkInfo.getPath());

                    AlertDialog.Builder builder = new AlertDialog.Builder(v.getContext());

// 2. Chain together various setter methods to set the dialog characteristics
                    builder.setMessage("Are you sure you want to delete "+file.getName()+" ?")
                            .setTitle("Delete File") .setPositiveButton("OK", new DialogInterface.OnClickListener() {
                        public void onClick(DialogInterface dialog, int id) {
                            if (file.delete()){
                                mDataset.remove(apkInfo);
                                notifyDataSetChanged();
                            }
                        }
                    })
                            .setNegativeButton("Cancel", new DialogInterface.OnClickListener() {
                                public void onClick(DialogInterface dialog, int id) {
                                    dialog.cancel();
                                }
                            });

// 3. Get the AlertDialog from create()
                    AlertDialog dialog = builder.create();
                    dialog.show();
                    return false;
                }
            });

        }



    }

    // Provide a suitable constructor (depends on the kind of dataset)
    public MyAdapterForApk(List<ApkInfo> apks) {
        this.mDataset = apks;
    }

    // Create new views (invoked by the layout manager)
    @Override
    public MyAdapterForApk.MyViewHolder onCreateViewHolder(ViewGroup parent,
                                                             int viewType) {
        // create a new view
        View v = LayoutInflater.from(parent.getContext())
                .inflate(R.layout.list_apk, parent, false);

        return new MyAdapterForApk.MyViewHolder(v);
    }

    // Replace the contents of a view (invoked by the layout manager)
    @Override
    public void onBindViewHolder(MyAdapterForApk.MyViewHolder holder, int position) {
        // - get element from your dataset at this position
        // - replace the contents of the view with that element
        ApkInfo apkInfo = mDataset.get(position);

        holder.date.setText(dateFormat.format(apkInfo.getDate()));
        holder.imageView.setImageDrawable(apkInfo.getImageView());
        holder.label.setText(apkInfo.getLabel());
        holder.path.setText(apkInfo.getPath());
        holder.size.setText(String.valueOf(apkInfo.getSize() / 0x100000L)+"MB");


    }

    // Return the size of your dataset (invoked by the layout manager)
    @Override
    public int getItemCount() {
        return mDataset.size();
    }
}
