package com.example.mac.suchik;

import android.app.Application;

import androidx.core.app.ActivityCompat;

public class WeatherApplication extends Application {
    @Override
    public void onCreate() {
        super.onCreate();
        Storage mSorage = Storage.getOrCreate(getApplicationContext());
        //mSorage.updateWeather(false);
    }
}
