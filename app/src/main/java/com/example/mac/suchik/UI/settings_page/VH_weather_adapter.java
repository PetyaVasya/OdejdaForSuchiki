package com.example.mac.suchik.UI.settings_page;

import androidx.recyclerview.widget.RecyclerView;
import android.view.View;
import android.widget.ImageView;
import android.widget.TextView;

import com.example.mac.suchik.R;

public class VH_weather_adapter extends RecyclerView.ViewHolder {
    public TextView date, temp_avg;

    public ImageView im;

    public VH_weather_adapter(View itemView) {
        super(itemView);
        date = itemView.findViewById(R.id.for_main);
        temp_avg = itemView.findViewById(R.id.temp_avg);
        im = itemView.findViewById(R.id.part_of_sun);
    }
}
