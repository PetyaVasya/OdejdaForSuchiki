package com.example.mac.suchik.UI;

import android.graphics.drawable.Drawable;
import android.graphics.drawable.PictureDrawable;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import com.example.mac.suchik.R;
import com.example.mac.suchik.Storage;
import com.example.mac.suchik.UI.settings_page.VH_weather_adapter;
import com.example.mac.suchik.WeatherData.Day_short;
import com.example.mac.suchik.WeatherData.Fact;
import com.example.mac.suchik.WeatherData.Forecasts;

import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.List;

public class Weather_Adapter extends RecyclerView.Adapter<VH_weather_adapter> {
    private List<Forecasts> mData;
    private boolean isF;
    private ICallBackOnDayChanged itemClickListener;

    void setClickListener(ICallBackOnDayChanged itemClickListener) {
        this.itemClickListener = itemClickListener;
    }
    
    public interface ICallBackOnDayChanged{
        void onDayChanged(Fact weather, String date);
    }

    public Weather_Adapter(List<Forecasts> data, boolean isF) {
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

        String strCurrentDate = mData.get(position).getDate();
        SimpleDateFormat format = new SimpleDateFormat("yyyy-MM-dd");
        Date newDate = null;
        try {
            newDate = format.parse(strCurrentDate);
        } catch (ParseException e) {
            e.printStackTrace();
        }
        format = new SimpleDateFormat("dd-MM");
        String date = format.format(newDate);
        if (position == 0)
            holder.date.setText("Сегодня");
        else
            holder.date.setText(date);

        holder.itemView.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                final Day_short dayShort = mData.get(position).getParts().getDay_short();
                Fact fact = new Fact(){{
                    setCloudness(dayShort.getCloudness());
                    setTemp(dayShort.getTemp());
                    setWind_speed(dayShort.getWind_speed());
                    setPrec_type(dayShort.getPrec_type());
                    setIcon(dayShort.getIcon());
                    setImageIcon(dayShort.getImageIcon());
                    setHumidity(dayShort.getHumidity());
                    setCondition(dayShort.getCondition());
                    setPressure_mm(dayShort.getPressure_mm());
                    setFeels_like(dayShort.getFeels_like());
                    setWind_dir(dayShort.getWind_dir());
                }};
                (Storage.getOrCreate(null)).getClothes(fact);
                itemClickListener.onDayChanged(fact, mData.get(position).getDate());
            }
        });

        Float s = mData.get(position).getParts().getDay_short().getTemp();
        if (!isF) {
            if (s > 0) holder.temp_avg.setText(String.format("+" + "%.0f" + "°С", s));
            else holder.temp_avg.setText(String.format("%.0f °С", s));
        } else {
            float far = (s * 9 / 5) + 32;
            if (far > 0) holder.temp_avg.setText(String.format("+" + "%.0f" + "°F", far));
            else holder.temp_avg.setText(String.format("%.0f" + "°F", far));
        }
        if (mData.get(position).getParts().getDay_short().getImageIcon() != null) {

            Drawable drawable = new PictureDrawable(mData.get(position).getParts().getDay_short().getImageIcon().renderToPicture());
            holder.im.setImageDrawable(drawable);
        }
        else {
            String condition = mData.get(position).getParts().getDay().getCondition();
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
    public void setList(List<Forecasts> new_elements){
        mData.clear();
        mData.addAll(new_elements);
        notifyDataSetChanged();
    }
}
