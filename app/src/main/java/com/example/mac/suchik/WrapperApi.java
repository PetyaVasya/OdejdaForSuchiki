package com.example.mac.suchik;

import android.content.Context;
import android.content.SharedPreferences;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.os.AsyncTask;
import android.util.Base64;

import com.example.mac.suchik.WeatherData.List;
import com.example.mac.suchik.WeatherData.WeatherData;
import com.google.gson.Gson;

import java.io.ByteArrayOutputStream;
import java.io.IOException;

public class WrapperApi extends AsyncTask<Void, Void, Response>{
    private String lat, lon;
    private Callbacks callbacks;
    private Gson gson;
    private Context context;

    public WrapperApi(Context context, String lat, String lon, Callbacks callbacks, Gson gson) {
        this.context = context;
        this.lat = lat;
        this.lon = lon;
        this.callbacks = callbacks;
        this.gson = gson;
    }

    @Override
    protected Response doInBackground(Void... voids) {
        Weather weather = null;
        boolean flag = true;
        Response res = null;
        while (flag){
            try {
                weather = new Weather(lat, lon, gson);
                res = weather.parseWeather();
                SharedPreferences sp = context.getSharedPreferences(context.getString(R.string.weather_preferences), Context.MODE_PRIVATE);
                SharedPreferences.Editor editor = sp.edit();
                WeatherData response = (WeatherData) res.response;
                if (response != null){
                    for (List list: response.getList()) {
                        list.getWeather().get(0).setStrImage(getStringFromBitmap(list.getWeather().get(0).getImageIcon()));
                        list.getWeather().get(0).setImageIcon(null);
                    }
                    response.getFact().getWeather().get(0).setStrImage(getStringFromBitmap(response.getFact().getWeather().get(0).getImageIcon()));
                    response.getFact().getWeather().get(0).setImageIcon(null);
                    editor.putString("weather", gson.toJson(response));
                    editor.apply();
                }
                for (List list:response.getList()) {
                    list.getWeather().get(0).setImageIcon(getBitmapFromString(list.getWeather().get(0).getStrImage()));
                    list.getWeather().get(0).setStrImage(null);
                }
                response.getFact().getWeather().get(0).setImageIcon(getBitmapFromString(response.getFact().getWeather().get(0).getStrImage()));
                response.getFact().getWeather().get(0).setStrImage(null);
                flag = false;
            } catch (IOException e) {
                flag = true;
            }
        }
        return res;
    }

    @Override
    protected void onPostExecute(Response response) {
        super.onPostExecute(response);
        callbacks.onLoad(response);
    }
    private String getStringFromBitmap(Bitmap bitmapPicture) {
        final int COMPRESSION_QUALITY = 100;
        String encodedImage;
        ByteArrayOutputStream byteArrayBitmapStream = new ByteArrayOutputStream();
        bitmapPicture.compress(Bitmap.CompressFormat.PNG, COMPRESSION_QUALITY,
                byteArrayBitmapStream);
        byte[] b = byteArrayBitmapStream.toByteArray();
        encodedImage = Base64.encodeToString(b, Base64.DEFAULT);
        return encodedImage;
    }
    private Bitmap getBitmapFromString(String stringPicture) {
        byte[] decodedString = Base64.decode(stringPicture, Base64.DEFAULT);
        Bitmap decodedByte = BitmapFactory.decodeByteArray(decodedString, 0, decodedString.length);
        return decodedByte;
    }
}
