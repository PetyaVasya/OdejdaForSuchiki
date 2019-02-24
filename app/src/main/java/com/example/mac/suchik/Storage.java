package com.example.mac.suchik;

import android.content.Context;
import android.content.SharedPreferences;

import com.example.mac.suchik.WeatherData.Fact;
import com.example.mac.suchik.WeatherData.WeatherData;
import com.google.gson.Gson;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Objects;
import java.util.concurrent.ExecutionException;

public class Storage implements Callbacks{
    private WeatherData response;
    private String[] position;
    private HashMap<Integer, List<Callbacks>> type_callback_rels = new LinkedHashMap<>();
    private Gson gson;
    private Context mCtx;
    private SharedPreferences sp;
    private HashMap<String, Boolean> executed;


    private GetClothes getClothes;

    private static Storage _instance;


    public static synchronized Storage getOrCreate(Context context){
        if (_instance == null){
            _instance = new Storage(context);
        }
        return _instance;
    }

    Storage(Context context){
        this.mCtx = context;
        this.gson = new Gson();

        sp = context.getSharedPreferences(context.getString(R.string.weather_preferences), Context.MODE_PRIVATE);
        executed = new HashMap<String, Boolean> (){{
            put("GG", false);
            put("GT", false);
            put("GF", false);
            put("GC", false);
        }};
        if (!Objects.equals(sp.getString("weather", null), null)){
            response =  gson.fromJson(sp.getString("weather", null),
                            WeatherData.class);
        }
        if (!Objects.equals(sp.getString("pos_lat",
                null), null) && !Objects.equals(sp.getString("pos_lon",
                null), null)){
            position = new String[]{sp.getString("pos_lat",
                    null), sp.getString("pos_lon",
                    null)};
        }
    }

    public WeatherData getResponse() {
        return response;
    }

    public void updateWeather(boolean is_blocked){
        if (position != null && position[0] != null && position[1] != null) {
            WrapperApi request = new WrapperApi(position[0], position[1], Storage.this, gson);
            request.execute();
            if (is_blocked) {
                try {
                    request.get();
                } catch (ExecutionException | InterruptedException e) {
                    onLoad(new Response<>(ResponseType.ERROR, null));
                }
            }
            if (executed.get("GT"))
                executed.put("GF", true);
            else if (executed.get("GF"))
                executed.put("GT", true);
            else {
                executed.putAll(new HashMap() {{
                    put("GT", true);
                    put("GF", true);
                }});
            }
        }
    }

    public void getCurrentCommunity() {
            if (position != null) {
                executed.put("GCC", true);
                new Community(mCtx, position, Storage.this).execute();
            }
    }

    public void getClothes(Fact weather) {
        if (!executed.get("GC")) {
                executed.put("GC", true);
                new GetClothes(mCtx, Storage.this, weather).execute();
        }
    }
    public void setPosition(String lat, String lon){
        if (lat == null || lon == null){
            onLoad(new Response<>(ResponseType.GEOERROR, null));
        }
        else if (!executed.get("GG")) {
            executed.put("GG", true);
            onLoad(new Response<>(ResponseType.GGEOPOSITION, new String[]{lat, lon}));
            executed.put("GG", false);
        }
    }

    public void subscribe(int type, Callbacks callbacks){
        if (type_callback_rels.get(type) == null) type_callback_rels.put(type,
                new ArrayList<Callbacks>());
        type_callback_rels.get(type).add(callbacks);
    }
    public void unsubscribe(Callbacks callbacks){
        for (List<Callbacks> callbacks1: type_callback_rels.values()){
            if (callbacks1.contains(callbacks))
                callbacks1.remove(callbacks1.indexOf(callbacks));
        }
    }

    public void saveData(){
        SharedPreferences.Editor editor = sp.edit();
        if (response != null){
            editor.putString("weather", gson.toJson(response));
            editor.apply();
        }
        if (position != null && position[0] != null && position[1] != null)
        {
            editor.putString("pos_lat", position[0]);
            editor.putString("pos_lon", position[1]);
        }
    }

    public void getWeatherToday(){
        if (!executed.get("GT")){
            if (response == null && !executed.get("GF")){
                updateWeather(false);
            } else{
                if (position != null && position[0] != null && position[1] != null) {
                    executed.put("GT", true);
                    onLoad(new Response<>(ResponseType.WTODAY, response.getFact()));
                    executed.put("GT", false);
                }
            }
        }
    }

    public void getIconBitmap(String url) {

    }

    public void getWeatherForecasts() {
        if (! executed.get("GF")){
            if (response == null && !executed.get("GT")){
                updateWeather(false);
            } else{
                if (position != null && position[0] != null && position[1] != null){
                    executed.put("GF", true);
                    onLoad(new Response<>(ResponseType.WFORECASTS, response.getForecasts()));
                    executed.put("GF", false);
                }
            }
        }
    }

    @Override
    public void onLoad(Response response) {
        if (response.response != null && response.response.getClass() == WeatherData.class)
            this.response = (WeatherData) response.response;
        if (response.response == null)
            response.type = ResponseType.ERROR;
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
                                callbacks.onLoad(new Response<>(ResponseType.WFORECASTS,
                                        ((WeatherData) response.response).getForecasts()));
                                callbacks.onLoad(new Response<>(ResponseType.WTODAY,
                                        ((WeatherData) response.response).getFact()));
                                break;
                            case ResponseType.WTODAY:
                                callbacks.onLoad(new Response<>(ResponseType.WTODAY,
                                        ((WeatherData) response.response).getFact()));
                                break;
                            case ResponseType.WFORECASTS:
                                callbacks.onLoad(new Response<>(ResponseType.WFORECASTS,
                                        ((WeatherData) response.response).getForecasts()));
                                break;
                        }
                    }
                }
                executed.put("GT", false);
                executed.put("GF", false);
                break;
            case ResponseType.WTODAY:
                this.response = (WeatherData) response.response;
                getClothes(this.response.getFact());
                if (type_callback_rels.get(ResponseType.WTODAY) == null)
                    type_callback_rels.put(ResponseType.WTODAY, new ArrayList<Callbacks>());
                list = type_callback_rels.get(ResponseType.WTODAY);
                for (Callbacks callbacks: list) {
                    callbacks.onLoad(new Response<>(ResponseType.WTODAY, response.response));
                }
                executed.put("GT", false);
                break;
            case ResponseType.WFORECASTS:
                if (type_callback_rels.get(ResponseType.WFORECASTS) == null)
                    type_callback_rels.put(ResponseType.WFORECASTS, new ArrayList<Callbacks>());
                list = type_callback_rels.get(ResponseType.WFORECASTS);
                for (Callbacks callbacks: list) {
                    callbacks.onLoad(new Response<>(ResponseType.WFORECASTS, response.response));
                }
                executed.put("GF", false);
                break;
            case ResponseType.GGEOPOSITION:
                this.position = (String[]) response.response;
                updateWeather(false);
                getCurrentCommunity();
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
                break;
            case ResponseType.CLOTHES:
                if (type_callback_rels.get(ResponseType.CLOTHES) == null)
                    type_callback_rels.put(ResponseType.CLOTHES, new ArrayList<Callbacks>());
                list = type_callback_rels.get(ResponseType.CLOTHES);
                for (Callbacks callbacks: list) {
                    callbacks.onLoad(response);
                }
                executed.put("GC", false);
                break;
            case ResponseType.COMMUNITY:
                if (type_callback_rels.get(ResponseType.COMMUNITY) == null)
                    type_callback_rels.put(ResponseType.COMMUNITY, new ArrayList<Callbacks>());
                list = type_callback_rels.get(ResponseType.COMMUNITY);
                for (Callbacks callbacks: list) {
                    callbacks.onLoad(response);
                }
                executed.put("GCC", false);
                break;
        }
    }
}