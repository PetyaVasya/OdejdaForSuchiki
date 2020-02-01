package com.example.mac.suchik;

import android.content.Context;
import android.content.SharedPreferences;
import android.location.Address;
import android.location.Geocoder;
import android.os.AsyncTask;
import android.util.Log;

import java.io.IOException;
import java.util.List;
import java.util.Locale;

public class Community extends AsyncTask<Void, Void, Response> {
    private Context mCtx;
    private double lat, lng;
    private Callbacks callbacks;

    public Community(Context context, String[] location, Callbacks callbacks) {
        this.mCtx = context;
        this.lat = Double.valueOf(location[0]);
        this.lng = Double.valueOf(location[1]);
        this.callbacks = callbacks;
    }

    @Override

    protected Response doInBackground(Void... voids) {
        Geocoder geocoder = new Geocoder(mCtx, Locale.getDefault());
        String[] result = new String[]{lat + "", lng + "", ""};
        try {
            List<Address> list = geocoder.getFromLocation(lat, lng, 1);
            if (list != null && list.size() > 0) {
                Address address = list.get(0);
                String city = address.getLocality();
                if (city != null)
                    result[2] = city;
                //Log.d("Community", result);
            }
        } catch (IOException e) {
            Log.e("Community", "Impossible to connect to Geocoder", e);
        }
        SharedPreferences sp = mCtx.getSharedPreferences(mCtx.getString(R.string.weather_preferences), Context.MODE_PRIVATE);
        SharedPreferences.Editor editor = sp.edit();
        if (result != null)
            editor.putString("current_city", result[2]);
        editor.apply();
        return new Response<>(ResponseType.COMMUNITY, result);
    }

    @Override
    protected void onPostExecute(Response response) {
        super.onPostExecute(response);
        callbacks.onLoad(response);
    }
}

