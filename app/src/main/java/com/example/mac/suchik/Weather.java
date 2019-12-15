package com.example.mac.suchik;

import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.util.Base64;

import java.io.ByteArrayOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.net.MalformedURLException;
import java.net.URL;
import java.text.MessageFormat;
import java.text.SimpleDateFormat;
import java.util.Date;

import okhttp3.OkHttpClient;
import okhttp3.Request;

import com.caverock.androidsvg.SVG;
import com.caverock.androidsvg.SVGParseException;
import com.example.mac.suchik.WeatherData.Fact;
import com.example.mac.suchik.WeatherData.List;
import com.example.mac.suchik.WeatherData.WeatherData;
import com.google.gson.Gson;

class Weather {
    private Request request;
    private String forecastsResponse;
    private String factResponse;
    private int type;
    private Gson gson;
    private static final MessageFormat urlFormat = new MessageFormat("https://api.openweathermap.org/data/2.5/{0}?lat={1}&lon={2}&lang={3}&appid={4}&units=metric");

    Weather(String lat, String lon, Gson gson) throws IOException {
        Request request = new Request.Builder()
                .url(urlFormat.format(new Object[]{"forecast", lat, lon, "ru", "683fffc67375308d022e9348f4eb18b1"}))
                .build();
        this.forecastsResponse = getResponse(request);
        request = new Request.Builder()
                .url(urlFormat.format(new Object[]{"weather", lat, lon, "ru", "683fffc67375308d022e9348f4eb18b1"}))
                .build();
        this.factResponse = getResponse(request);
        this.gson = gson;
    }

    private String getResponse(Request request) throws IOException {
        OkHttpClient client = new OkHttpClient();
        try (okhttp3.Response response = client.newCall(request).execute()) {
            return response.body().string();
        }
    }


    Response parseWeather(){
        Response response = new Response<>(ResponseType.GETW, gson.fromJson(forecastsResponse, WeatherData.class));
        Fact fact = gson.fromJson(factResponse, Fact.class);
        WeatherData now = (WeatherData) response.response;
        now.setFact(fact);
        URL url = null;
        try {
            url = new URL("https://openweathermap.org/img/wn/"
                    + now.getFact().getWeather().get(0).getIcon() + ".png");
        } catch (MalformedURLException e) {
            e.printStackTrace();
        }
        if (url != null) {
            Bitmap bmp = null;
            try {
                InputStream in = url.openStream();
                bmp = BitmapFactory.decodeStream(in);
            } catch (IOException e) {
                e.printStackTrace();
            }
            now.getFact().getWeather().get(0).setImageIcon(bmp);
        }
        for (List forecast: now.getList()){
            forecast.setDt_txt(new SimpleDateFormat("yyyy-MM-dd HH:mm:ss").format(new Date(forecast.getDt() * 1000)));
            url = null;
            try {
                url = new URL("https://openweathermap.org/img/wn/"
                        + forecast.getWeather().get(0).getIcon() + ".png");
            } catch (MalformedURLException e) {
                e.printStackTrace();
            }
            if (url != null) {
                Bitmap bmp = null;
                try {
                    InputStream in = url.openStream();
                    bmp = BitmapFactory.decodeStream(in);
                } catch (IOException e) {
                    e.printStackTrace();
                }
                forecast.getWeather().get(0).setImageIcon(bmp);
            }
        }

        return response;
    }
}