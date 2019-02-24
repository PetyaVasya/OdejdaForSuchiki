package com.example.mac.suchik;

import java.io.IOException;
import java.net.MalformedURLException;
import java.net.URL;

import okhttp3.OkHttpClient;
import okhttp3.Request;

import com.caverock.androidsvg.SVG;
import com.caverock.androidsvg.SVGParseException;
import com.example.mac.suchik.WeatherData.Forecasts;
import com.example.mac.suchik.WeatherData.WeatherData;
import com.google.gson.Gson;

class Weather {
    private Request request;
    private String strResponse;
    private int type;
    private Gson gson;

    Weather(String lat, String lon, Gson gson) throws IOException {
        Request request = new Request.Builder()
                .url("https://api.weather.yandex.ru/v1/forecast?" + "lat=" + lat + "&lon=" + lon +
                        "&hours=false&extra=true")
                .addHeader("X-Yandex-API-Key", "07630454-fe07-4c10-acec-32cb620dc877")
                .build();
        this.request = request;
        this.strResponse = getResponse();
        this.gson = gson;
    }

    private String getResponse() throws IOException {
        OkHttpClient client = new OkHttpClient();
        try (okhttp3.Response response = client.newCall(request).execute()) {
            return response.body().string();
        }
    }


    Response parseWeather(){
        Response response = new Response<>(ResponseType.GETW, gson.fromJson(strResponse, WeatherData.class));
        WeatherData now = (WeatherData) response.response;
        URL url = null;
        try {
            url = new URL("https://yastatic.net/weather/i/icons/blueye/color/svg/"
                    + now.getFact().getIcon() + ".svg");
        } catch (MalformedURLException e) {
            e.printStackTrace();
        }
        if (url != null) {
            try {
                SVG svg = null;
                try {
                    svg = SVG.getFromInputStream(url.openConnection() .getInputStream());
                } catch (SVGParseException e) {
                    e.printStackTrace();
                }
                now.getFact().setImageIcon(svg);
            } catch (IOException e) {
                e.printStackTrace();
            }
        }
        for (Forecasts forecast: now.getForecasts()){
            url = null;
            try {
                url = new URL("https://yastatic.net/weather/i/icons/blueye/color/svg/"
                        + forecast.getParts().getDay_short().getIcon() + ".svg");
            } catch (MalformedURLException e) {
                e.printStackTrace();
            }
            try {
                if (url != null) {
                    SVG svg = null;
                    try {
                        svg = SVG.getFromInputStream(url.openConnection() .getInputStream());
                    } catch (SVGParseException e) {
                        e.printStackTrace();
                    }
                    forecast.getParts().getDay_short().setImageIcon(svg);
                }
            } catch (IOException e) {
                e.printStackTrace();
            }
        }

        return response;
    }
}