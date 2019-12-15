package com.example.mac.suchik.UI;

import androidx.recyclerview.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import com.example.mac.suchik.R;
import com.example.mac.suchik.Storage;
import com.example.mac.suchik.UI.settings_page.VH_weather_adapter;
import com.example.mac.suchik.WeatherData.List;

import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.Date;

public class Weather_Adapter extends RecyclerView.Adapter<VH_weather_adapter> {
    private java.util.List<List> mData;
    private boolean isF;
    private ICallBackOnDayChanged itemClickListener;

    void setClickListener(ICallBackOnDayChanged itemClickListener) {
        this.itemClickListener = itemClickListener;
    }
    
    public interface ICallBackOnDayChanged{
        void onDayChanged(List weather, String date);
    }

    public Weather_Adapter(java.util.List<List> data, boolean isF) {
        super();
        this.isF = isF;
        mData = data;
    }

    @Override
    public VH_weather_adapter onCreateViewHolder(ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(parent.getContext()).inflate(R.layout.for_list_weather, parent, false);
        VH_weather_adapter weather_adapter = new VH_weather_adapter(view);
        return weather_adapter;
    }

    public void onBindViewHolder(final VH_weather_adapter holder, final int position) {
        //String date = "Fri, 22 Apr 2016 15:29:51 +0600";
        //String date = "2019-01-22";

        String strCurrentDate = mData.get(position).getDt_txt();
        SimpleDateFormat format = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss");
        Date newDate = null;
        try {
            newDate = format.parse(strCurrentDate);
        } catch (ParseException e) {
            e.printStackTrace();
        }
        format = new SimpleDateFormat("dd.MM HH:mm");
        final String date = format.format(newDate);
        if (position == 0)
            holder.date.setText("Сегодня");
        else
            holder.date.setText(date);

        holder.itemView.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                final List fact = mData.get(position);
                (Storage.getOrCreate(null)).getClothes(fact);
                itemClickListener.onDayChanged(fact, date);
            }
        });

        double s = mData.get(position).getMain().getTemp();
        if (isF) {
            if (s > 0) holder.temp_avg.setText(String.format("+" + "%.0f" + "°С", s));
            else holder.temp_avg.setText(String.format("%.0f °С", s));
        } else {
            double far = (s * 9 / 5) + 32;
            if (far > 0) holder.temp_avg.setText(String.format("+" + "%.0f" + "F", far));
            else holder.temp_avg.setText(String.format("%.0f" + "F", far));
        }
        if (mData.get(position).getWeather().get(0).getImageIcon() != null) {
            holder.im.setImageBitmap(mData.get(position).getWeather().get(0).getImageIcon());
        }
        else {
            String condition = mData.get(position).getWeather().get(0).getMain();
            switch (condition) {
                case "clear":
                    holder.im.setImageResource(R.drawable.sunny);
                    break;
                case "partly-cloudy":
                    holder.im.setImageResource(R.drawable.cloud);
                    break;
                case "cloudy":
                    holder.im.setImageResource(R.drawable.cloud);
                    break;
                case "overcast":
                    holder.im.setImageResource(R.drawable.cloud);
                    break;
                case "partly-cloudy-and-light-rain":
                    holder.im.setImageResource(R.drawable.rain);
                    break;
                case "partly-cloudy-and-rain":
                    holder.im.setImageResource(R.drawable.rain);
                    break;
                case "overcast-and-rain":
                    holder.im.setImageResource(R.drawable.rain);
                    break;
                case "overcast-thunderstorms-with-rain":
                    holder.im.setImageResource(R.drawable.rain);
                    break;
                case "cloudy-and-light-rain":
                    holder.im.setImageResource(R.drawable.rain);
                    break;
                case "overcast-and-light-rain":
                    holder.im.setImageResource(R.drawable.rain);
                    break;
                case "cloudy-and-rain":
                    holder.im.setImageResource(R.drawable.rain);
                    break;
                case "overcast-and-wet-snow":
                    holder.im.setImageResource(R.drawable.snowing);
                    break;
                case "partly-cloudy-and-light-snow":
                    holder.im.setImageResource(R.drawable.snowing);
                    break;
                case "partly-cloudy-and-snow":
                    holder.im.setImageResource(R.drawable.snowing);
                    break;
                case "overcast-and-snow":
                    holder.im.setImageResource(R.drawable.snowing);
                    break;
                case "cloudy-and-light-snow":
                    holder.im.setImageResource(R.drawable.snowing);
                    break;
                case "overcast-and-light-snow":
                    holder.im.setImageResource(R.drawable.snowing);
                    break;
                case "cloudy-and-snow":
                    holder.im.setImageResource(R.drawable.snowing);
                    break;
            }
        }
    }

    @Override
    public int getItemCount() {
        return mData.size();
    }
    public void setList(java.util.List<List> new_elements){
        mData.clear();
        mData.addAll(new_elements);
        notifyDataSetChanged();
    }
}
