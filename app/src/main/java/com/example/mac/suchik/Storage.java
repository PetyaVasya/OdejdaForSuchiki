package com.example.mac.suchik;

import android.content.Context;
import android.content.SharedPreferences;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.os.AsyncTask;
import android.util.Base64;

import com.example.mac.suchik.WeatherData.Fact;
import com.example.mac.suchik.WeatherData.List;
import com.example.mac.suchik.WeatherData.Sys;
import com.example.mac.suchik.WeatherData.WeatherData;
import com.google.gson.Gson;

import java.io.ByteArrayOutputStream;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;
import java.util.HashMap;
import java.util.HashSet;
import java.util.LinkedHashMap;
import java.util.Objects;
import java.util.Set;
import java.util.concurrent.ExecutionException;

public class Storage implements Callbacks{
    private String[] position;
    private HashMap<Integer, java.util.List<Callbacks>> type_callback_rels = new LinkedHashMap<>();
    private Gson gson;
    private Context mCtx;
    private SharedPreferences sp;
    private HashMap<String, Boolean> executed;

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
        if (!Objects.equals(sp.getString("pos_lat",
                null), null) && !Objects.equals(sp.getString("pos_lon",
                null), null)){
            position = new String[]{sp.getString("pos_lat",
                    null), sp.getString("pos_lon",
                    null)};
        }
    }

    public Object[] getSavedData(){
        WeatherData response = null;
        if (!Objects.equals(sp.getString("weather", null), null)){
            response =  gson.fromJson(sp.getString("weather", null),
                    WeatherData.class);
//            response.getFact().getWeather().get(0).setImageIcon(getBitmapFromString(s.get(l)));
        }

        String[] community = null;
        if (position != null)
            community = new String[]{position[0], position[1], sp.getString("current_city", "")};
        ArrayList<String> clothes = null;
        if (!Objects.equals(sp.getStringSet("last_clothes", null), null)){
            clothes = new ArrayList<>(sp.getStringSet("last_clothes", null));
        }
        return new Object[]{response, community, clothes};
    }

    public void updateWeather(boolean is_blocked){
        if (position != null && position[0] != null && position[1] != null) {
            WrapperApi request = new WrapperApi(mCtx, position[0], position[1], Storage.this, gson);
            request.executeOnExecutor(AsyncTask.THREAD_POOL_EXECUTOR);
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
                new Community(mCtx, position, Storage.this).executeOnExecutor(AsyncTask.THREAD_POOL_EXECUTOR);
            }
    }

    public void getClothes(List weather) {
        if (!executed.get("GC")) {
                executed.put("GC", true);
                (new GetClothes(mCtx, Storage.this, weather)).executeOnExecutor(AsyncTask.THREAD_POOL_EXECUTOR);
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
        for (java.util.List<Callbacks> callbacks1: type_callback_rels.values()){
            if (callbacks1.contains(callbacks))
                callbacks1.remove(callbacks1.indexOf(callbacks));
        }
    }

    public void saveData(){
        SharedPreferences.Editor editor = sp.edit();
//        if (response != null){
//            for (List list: response.getList()) {
//                list.getWeather().get(0).setStrImage(getStringFromBitmap(list.getWeather().get(0).getImageIcon()));
//                list.getWeather().get(0).setImageIcon(null);
//            }
//            response.getFact().getWeather().get(0).setStrImage(getStringFromBitmap(response.getFact().getWeather().get(0).getImageIcon()));
//            response.getFact().getWeather().get(0).setImageIcon(null);
//            editor.putString("weather", gson.toJson(response));
//            editor.apply();
//        }
        if (position != null && position[0] != null && position[1] != null)
        {
            editor.putString("pos_lat", position[0]);
            editor.putString("pos_lon", position[1]);
        }
//        if (community != null)
//            editor.putString("current_city", community[2]);
//        if (clothes != null)
//            editor.putStringSet("last_clothes", new HashSet<>(clothes));
        editor.commit();
    }

//    public void getWeatherToday(){
//        if (!executed.get("GT")){
//            if (!executed.get("GF")){
//                updateWeather(false);
////            } else{
////                if (position != null && position[0] != null && position[1] != null) {
////                    executed.put("GT", true);
////                    onLoad(new Response<>(ResponseType.WTODAY, response.getFact()));
////                    executed.put("GT", false);
////                }
//            }
//        }
//    }
//
//    public void getIconBitmap(String url) {
//
//    }
//
//    public void getWeatherForecasts() {
//        if (! executed.get("GF")){
//            if (response == null && !executed.get("GT")){
//                updateWeather(false);
//            } else{
//                if (position != null && position[0] != null && position[1] != null){
//                    executed.put("GF", true);
//                    onLoad(new Response<>(ResponseType.WFORECASTS, response.getList()));
//                    executed.put("GF", false);
//                }
//            }
//        }
//    }

    @Override
    public void onLoad(Response response) {
        if (response.response == null)
            response.type = ResponseType.ERROR;
        java.util.List<Callbacks> list = null;
        switch (response.type){
            case ResponseType.GETW:
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
                                        ((WeatherData) response.response).getList()));
                                break;
                            case ResponseType.WTODAY:
                                callbacks.onLoad(new Response<>(ResponseType.WTODAY,
                                        ((WeatherData) response.response).getFact()));
                                break;
                            case ResponseType.WFORECASTS:
                                callbacks.onLoad(new Response<>(ResponseType.WFORECASTS,
                                        ((WeatherData) response.response).getList()));
                                break;
                        }
                    }
                }
                executed.put("GT", false);
                executed.put("GF", false);
                break;
            case ResponseType.WTODAY:
                final Fact n = ((WeatherData) response.response).getFact();
                final Date current = new Date(System.currentTimeMillis());
                getClothes(new List(){{
                    setDt_txt(new SimpleDateFormat("dd.MM HH:mm").format(current));
                    setMain(n.getMain());
                    setClouds(n.getClouds());
                    setDt(n.getDt());
                    setSys(n.getSys());
                    setWeather(n.getWeather());
                    setWind(n.getWind());
                }});
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
                for (Callbacks callback: list) {
                    callback.onLoad(response);
                }
                executed.put("GCC", false);
                break;
            case ResponseType.ERROR:
                break;
        }
    }

    private Bitmap getBitmapFromString(String stringPicture) {
        byte[] decodedString = Base64.decode(stringPicture, Base64.DEFAULT);
        Bitmap decodedByte = BitmapFactory.decodeByteArray(decodedString, 0, decodedString.length);
        return decodedByte;
    }
}