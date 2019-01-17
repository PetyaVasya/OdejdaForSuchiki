package com.example.mac.suchik;

import android.content.Context;

import com.example.mac.suchik.WeatherData.WeatherData;
import com.google.gson.Gson;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.LinkedHashMap;
import java.util.List;

public class Storage implements ResponseType, Callbacks{
    private WeatherData response;
    private Geoposition geoposition;
    private String[] position;
    private HashMap<Integer, List<Callbacks>> type_callback_rels = new LinkedHashMap<>();
    private Gson gson;

    private static Storage _instance;


    public static synchronized Storage getOrCreate(Context context){
        if (_instance == null){
            _instance = new Storage(context);
        }
        return _instance;
    }

    Storage(Context context){
        this.gson = new Gson();
        geoposition = new Geoposition(context);
    }

    void updateWeather(String[] position){
        new WrapperApi(position[0], position[1], Storage.this, gson).execute();
    }

    void updatePosition(){
        onLoad(geoposition.start());
    }

    void setPosition(String lat, String lon){
        onLoad(new Response<>(ResponseType.GGEOPOSITION, new String[]{lat, lon}));
    }

    void subscribe(int type, Callbacks callbacks){
        if (type_callback_rels.get(type) == null) type_callback_rels.put(type,
                new ArrayList<Callbacks>());
        type_callback_rels.get(type).add(callbacks);
    }
    void unsubscribe(Callbacks callbacks){
        for (List<Callbacks> callbacks1: type_callback_rels.values()){
            if (callbacks1.contains(callbacks))
                callbacks1.remove(callbacks1.indexOf(callbacks));
        }
    }

    void getWeatherToday(String[] position){
        if (response == null) {
            updateWeather(position);
        }else {
            onLoad(new Response<>(ResponseType.WTODAY, response.getFact()));
        }
    }

    void getWeatherForecasts(String[] position) {
        if (response == null) {
            updateWeather(position);
        } else {
            onLoad(new Response<>(ResponseType.WFORECASTS, response.getForecasts()));
        }
    }

    @Override
    public void onLoad(Response response) {
        List<Callbacks> list = null;
        switch (response.type){
            case ResponseType.GETW:
                this.response = (WeatherData) response.response;
                for (int i = 1; i < 4; i++){
                    if (type_callback_rels.get(i) == null) type_callback_rels.put(i,
                            new ArrayList<Callbacks>());
                    list = type_callback_rels.get(i);
                    for (Callbacks callbacks: list) {
                        switch (i) {
                            case ResponseType.GETW:
                                callbacks.onLoad(new Response<>(ResponseType.WTODAY,
                                        ((WeatherData) response.response).getFact()));
                                callbacks.onLoad(new Response<>(ResponseType.WFORECASTS,
                                        ((WeatherData) response.response).getForecasts()));
                                break;
                            case ResponseType.WTODAY:
                                callbacks.onLoad(new Response<>(i,
                                        ((WeatherData) response.response).getFact()));
                                break;
                            case ResponseType.WFORECASTS:
                                callbacks.onLoad(new Response<>(i,
                                        ((WeatherData) response.response).getForecasts()));
                                break;
                        }
                    }
                }
                break;
            case ResponseType.WTODAY:
                if (type_callback_rels.get(ResponseType.WTODAY) == null)
                    type_callback_rels.put(ResponseType.WTODAY, new ArrayList<Callbacks>());
                list = type_callback_rels.get(ResponseType.WTODAY);
                for (Callbacks callbacks: list) {
                    callbacks.onLoad(new Response<>(ResponseType.WTODAY, response.response));
                }
                break;
            case ResponseType.WFORECASTS:
                if (type_callback_rels.get(ResponseType.WFORECASTS) == null)
                    type_callback_rels.put(ResponseType.WFORECASTS, new ArrayList<Callbacks>());
                list = type_callback_rels.get(ResponseType.WFORECASTS);
                for (Callbacks callbacks: list) {
                    callbacks.onLoad(new Response<>(ResponseType.WFORECASTS, response.response));
                }
                break;
            case ResponseType.GGEOPOSITION:
                this.position = (String[]) response.response;
                if (type_callback_rels.get(ResponseType.GGEOPOSITION) == null)
                    type_callback_rels.put(ResponseType.GGEOPOSITION, new ArrayList<Callbacks>());
                list = type_callback_rels.get(ResponseType.GGEOPOSITION);
                for (Callbacks callbacks: list) {
                    callbacks.onLoad(response);
                }
                break;
            case ResponseType.GEOERROR:
                if (type_callback_rels.get(ResponseType.GGEOPOSITION) == null)
                    type_callback_rels.put(ResponseType.GGEOPOSITION, new ArrayList<Callbacks>());
                list = type_callback_rels.get(ResponseType.GGEOPOSITION);
                for (Callbacks callbacks: list) {
                    if (position != null)
                        callbacks.onLoad(new Response<>(ResponseType.GGEOPOSITION, position));
                    else callbacks.onLoad(response);
                }
        }
    }
}