package com.example.androidsecurityapp.adapter;

import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.CheckBox;
import android.widget.CompoundButton;
import android.widget.TextView;

import androidx.recyclerview.widget.RecyclerView;

import com.example.androidsecurityapp.R;
import com.example.androidsecurityapp.model.ChangesOfSettings;

import java.text.SimpleDateFormat;
import java.util.List;

public class MyAdapterForSettings extends RecyclerView.Adapter<MyAdapterForSettings.MyViewHolder> {
    private List<ChangesOfSettings> mDataset;
    final SimpleDateFormat dateFormat = new SimpleDateFormat("d MMM yyyy HH:mm:ss");


    // Provide a reference to the views for each data item
    // Complex data items may need more than one view per item, and
    // you provide access to all the views for a data item in a view holder
    public class MyViewHolder extends RecyclerView.ViewHolder {
        // each data item is just a string in this case
        private CheckBox checkBox;

        private TextView nameOfSetting;
        private TextView settingDateNew;
        private TextView settingDateOld;
        private TextView settingValueNew;
        private TextView settingValueOld;


        public MyViewHolder(View v) {
            super(v);

            nameOfSetting = v.findViewById(R.id.nameOfSetting);
            settingDateNew = v.findViewById(R.id.settingDateNew);
            settingDateOld = v.findViewById(R.id.settingDateOld);
            settingValueNew = v.findViewById(R.id.settingValueNew);
            settingValueOld = v.findViewById(R.id.settingValueOld);
            checkBox = v.findViewById(R.id.checkBoxSettings);

            checkBox.setOnCheckedChangeListener(new CompoundButton.OnCheckedChangeListener() {
                @Override
                public void onCheckedChanged(CompoundButton buttonView, boolean isChecked) {
                    ChangesOfSettings changesOfSettings = mDataset.get(getAdapterPosition());
                    changesOfSettings.setApproved(isChecked);
                }
            });

        }
    }

    // Provide a suitable constructor (depends on the kind of dataset)
    public MyAdapterForSettings(List<ChangesOfSettings> settings) {
        this.mDataset = settings;
    }

    // Create new views (invoked by the layout manager)
    @Override
    public MyAdapterForSettings.MyViewHolder onCreateViewHolder(ViewGroup parent,
                                                                int viewType) {
        // create a new view
        View v = LayoutInflater.from(parent.getContext())
                .inflate(R.layout.list_settings, parent, false);

        return new MyAdapterForSettings.MyViewHolder(v);
    }

    // Replace the contents of a view (invoked by the layout manager)
    @Override
    public void onBindViewHolder(MyAdapterForSettings.MyViewHolder holder, int position) {
        // - get element from your dataset at this position
        // - replace the contents of the view with that element
        ChangesOfSettings changesOfSettings = mDataset.get(position);

        holder.nameOfSetting.setText(changesOfSettings.getSetting());
        holder.checkBox.setChecked(changesOfSettings.isApproved());
        holder.settingDateNew.setText(dateFormat.format(changesOfSettings.getNewDate()));
        holder.settingDateOld.setText(dateFormat.format(changesOfSettings.getOldDate()));
        holder.settingValueNew.setText(changesOfSettings.getNewValue());
        holder.settingValueOld.setText(changesOfSettings.getOldValue());


    }

    // Return the size of your dataset (invoked by the layout manager)
    @Override
    public int getItemCount() {
        return mDataset.size();
    }
}
