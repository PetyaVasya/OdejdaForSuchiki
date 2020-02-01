package com.example.mac.suchik.UI;

import android.Manifest;
import android.annotation.SuppressLint;
import android.app.ActivityManager;
import android.app.AlertDialog;
import android.content.Context;
import android.content.DialogInterface;
import android.content.SharedPreferences;
import android.content.pm.PackageManager;
import android.content.res.Resources;
import android.graphics.drawable.BitmapDrawable;
import android.os.AsyncTask;
import android.os.Bundle;
import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.core.app.ActivityCompat;
import androidx.fragment.app.Fragment;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;
import androidx.swiperefreshlayout.widget.SwipeRefreshLayout;

import android.text.InputType;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.ProgressBar;
import android.widget.Spinner;
import android.widget.TextView;

import com.example.mac.suchik.Callbacks;
import com.example.mac.suchik.CheckInternetConnection;
import com.example.mac.suchik.CitySave;
import com.example.mac.suchik.R;
import com.example.mac.suchik.Response;
import com.example.mac.suchik.ResponseType;
import com.example.mac.suchik.Storage;
import com.example.mac.suchik.UI.main_window.RecomendationListAdapter;
import com.example.mac.suchik.WeatherData.Fact;
import com.example.mac.suchik.WeatherData.List;
import com.example.mac.suchik.WeatherData.Rain;
import com.example.mac.suchik.WeatherData.Sys;
import com.example.mac.suchik.WeatherData.WeatherData;
import com.google.gson.Gson;

import java.text.DateFormat;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Calendar;
import java.util.Collections;
import java.util.Date;
import java.util.HashMap;
import java.util.LinkedList;
import java.util.Locale;

import static android.content.Context.ACTIVITY_SERVICE;
import static androidx.core.content.ContextCompat.getSystemService;

public class MainWindowFragment extends Fragment implements Callbacks, AdapterView.OnItemSelectedListener, Weather_Adapter.ICallBackOnDayChanged {
    public static Storage mStorage;
    private TextView date;
    private ImageView weather_cloud;
    private TextView temperature;
    private RecyclerView rv;
    private RecyclerView rv_clothes;
    private Spinner spinnerCity;
    private int spinnerLatest;
    private TextView nowDate;
    private TextView nowDateDes;
    private TextView pressure;
    private TextView windy;
    private TextView humidity;
    private TextView feelsLikeTemp;
    private TextView condition;
    private TextView textFeelsLike;
    private ImageView imageHumdity;
    private ImageView imageWindy;
    private ImageView imagePressure;
    private SwipeRefreshLayout swipeContainer;


    private String[] position;
    private ArrayAdapter arrayAdapter;
    private boolean first;
    private SharedPreferences sp;
    private String today;

    private HashMap<String, String> conditions = new HashMap<>();
    private HashMap<String, String[]> cityPos = new HashMap<>();
    private HashMap<String, String> direction = new HashMap<String, String>(){{
        put("nw", "СЗ");
        put("n", "С");
        put("ne", "СВ");
        put("e", "В");
        put("se", "ЮВ");
        put("s", "Ю");
        put("sw", "ЮЗ");
        put("w", "З");
        put("c", "Ш");
    }};
    private java.util.List<String> cities = new LinkedList<>();
    private ProgressBar progressBar;

    private List f;
    private Gson gson;
    private CheckInternetConnection checkInternetConnection;
    private String dateText;

    private boolean isF;

    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        getPermissions();
        mStorage = Storage.getOrCreate(null); // null потому что я надеюсь, что Storage уже инициализирован
        mStorage.subscribe(ResponseType.GGEOPOSITION, this);
        mStorage.subscribe(ResponseType.WTODAY, this);
        mStorage.subscribe(ResponseType.COMMUNITY, this);
        mStorage.subscribe(ResponseType.CLOTHES, this);
        mStorage.subscribe(ResponseType.WFORECASTS, this);
        gson = new Gson();
        sp = getContext().getSharedPreferences("city", Context.MODE_PRIVATE);
//        SharedPreferences.Editor s = sp.edit();
//        s.putString("city", "");
//        s.commit();
        if (!sp.getString("city", "").equals("")){
            CitySave citySave = gson.fromJson(sp.getString("city", ""), CitySave.class);
            cityPos = citySave.getcityPos();
            cities = citySave.getCities();
        }
        else {
            String[] cities2 = getResources().getStringArray(R.array.cities);
            cities.addAll(Arrays.asList(cities2));
            String[] lats = getResources().getStringArray(R.array.lat);
            String[] lons = getResources().getStringArray(R.array.lon);
            for (int i = 0; i < lats.length; i++) {
                cityPos.put(cities.get(i), new String[]{lats[i], lons[i]});
            }
        }
        return inflater.inflate(R.layout.main_window, container, false);
    }

    @Override
    public void onStart() {
        super.onStart();
//        Geoposition geoposition = new Geoposition(getContext());
//        String[] position = geoposition.start();
//        //mStorage.setPosition("55.45", "37.36");
//        mStorage.setPosition(position[0], position[1]);
        //mStorage.updateWeather(false);
    }

    @Override
    public void onStop() {
        super.onStop();
        mStorage.unsubscribe(this);
        SharedPreferences.Editor editor = sp.edit();
        if (cities.contains("Текущее")){
            cities.remove(cities.indexOf("Текущее"));
            cityPos.remove("Текущее");
        }
        CitySave save = new CitySave();
        save.setCities(cities);
        save.setcityPos(cityPos);
        editor.putString("city", gson.toJson(save));
        editor.apply();
        if (!checkInternetConnection.isCancelled()){
            checkInternetConnection.cancel(false);
        }
    }

    @Override
    public void onViewCreated(@NonNull View view, @Nullable Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);

        progressBar = view.findViewById(R.id.progress_bar);
        progressBar.setVisibility(ProgressBar.VISIBLE);
        spinnerCity = view.findViewById(R.id.city);
        temperature = view.findViewById(R.id.temperature);
        weather_cloud = view.findViewById(R.id.weather_cloud);
        rv = view.findViewById(R.id.recommendation_list);
        rv_clothes = view.findViewById(R.id.for_recommendation_list);
        date = view.findViewById(R.id.date);
        nowDate = view.findViewById(R.id.now_date);
        nowDateDes = view.findViewById(R.id.now_date_des);
        humidity = view.findViewById(R.id.humidity);
        pressure = view.findViewById(R.id.pressure_mm);
        windy = view.findViewById(R.id.windy);
        feelsLikeTemp = view.findViewById(R.id.feels_like);
        condition = view.findViewById(R.id.condition);
        textFeelsLike = view.findViewById(R.id.feels_like_des);
        imageHumdity = view.findViewById(R.id.image_humidity);
        imageWindy = view.findViewById(R.id.image_windy);
        imagePressure = view.findViewById(R.id.image_pressure);
        rv_clothes.setLayoutManager(new LinearLayoutManager(getContext(), LinearLayoutManager.VERTICAL, false));
        rv.setLayoutManager(new LinearLayoutManager(getContext(), LinearLayoutManager.HORIZONTAL, false));
        java.util.List<String> data = new ArrayList<>();
        swipeContainer = view.findViewById(R.id.swipeContainer);
        swipeContainer.setOnRefreshListener(new SwipeRefreshLayout.OnRefreshListener() {
            @Override
            public void onRefresh() {
                checkInternetConnection = new CheckInternetConnection(getContext());
                checkInternetConnection.executeOnExecutor(AsyncTask.THREAD_POOL_EXECUTOR);
            }
        });

        DateFormat dateFormat = new SimpleDateFormat("dd.MM.yyyy", Locale.getDefault());
        Date currentDate = new Date();
        dateText = dateFormat.format(currentDate);

        String[] keys = getResources().getStringArray(R.array.conditions);
        String[] values = getResources().getStringArray(R.array.Ru_conditions);
        for (int i = 0; i < keys.length; i++)
            conditions.put(keys[i], values[i]);

        SharedPreferences settings = getContext().getSharedPreferences("settings", Context.MODE_PRIVATE);
        isF = settings.getBoolean("degrees", false);
        Object[] res = mStorage.getSavedData();
        if (res[0] != null & res[1] != null) {
            onLoad(new Response(ResponseType.COMMUNITY, (String[])res[1]));
            WeatherData response = (WeatherData) res[0];
            onLoad(new Response(ResponseType.WTODAY, response.getFact()));
            onLoad(new Response(ResponseType.WFORECASTS, response.getList()));
        }
    }

    private String parseDate(String date){
        SimpleDateFormat format = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss");
        Date newDate = null;
        try {
            newDate = format.parse(date);
        } catch (ParseException e) {
            e.printStackTrace();
        }
        format = new SimpleDateFormat("dd.MM HH:mm");
        return format.format(newDate);
    }

    @SuppressLint("DefaultLocale")
    @Override
    public void onDayChanged(List weather, String date){
        if ((today != null && date.equals(today)) || (date == null)){
            nowDate.setText("Сейчас");
            weather = f;
        }
        else{
            nowDate.setText(date);
        }

        if (isF) {
            if (weather.getMain().getTemp() > 0) temperature.setText(String.format("+" + "%.0f" + "°С", weather.getMain().getTemp()));
            else temperature.setText(String.format("%.0f °С", weather.getMain().getTemp()));
        } else {
            float far = (weather.getMain().getTemp() * 9 / 5) + 32;
            if (far > 0) temperature.setText(String.format("+" + "%.0f" + "F", far));
            else temperature.setText(String.format("%.0f" + "F", far));
        }

        String icon = weather.getWeather().get(0).getIcon();
        if (icon != null) {
            weather_cloud.setImageResource(getResources().getIdentifier(icon, "drawable",
                    getContext().getPackageName()));
        }
        else {
            String condition = weather.getWeather().get(0).getMain();
            Log.d("weather", condition);
            switch (condition) {
                case "clear":
                    weather_cloud.setImageResource(R.drawable.sunny);
                    break;
                case "partly-cloudy":
                    weather_cloud.setImageResource(R.drawable.cloud);
                    break;
                case "cloudy":
                    weather_cloud.setImageResource(R.drawable.cloud);
                    break;
                case "overcast":
                    weather_cloud.setImageResource(R.drawable.cloud);
                    break;
                case "partly-cloudy-and-light-rain":
                    weather_cloud.setImageResource(R.drawable.rain);
                    break;
                case "partly-cloudy-and-rain":
                    weather_cloud.setImageResource(R.drawable.rain);
                    break;
                case "overcast-and-rain":
                    weather_cloud.setImageResource(R.drawable.rain);
                    break;
                case "overcast-thunderstorms-with-rain":
                    weather_cloud.setImageResource(R.drawable.rain);
                    break;
                case "cloudy-and-light-rain":
                    weather_cloud.setImageResource(R.drawable.rain);
                    break;
                case "overcast-and-light-rain":
                    weather_cloud.setImageResource(R.drawable.rain);
                    break;
                case "cloudy-and-rain":
                    weather_cloud.setImageResource(R.drawable.rain);
                    break;
                case "overcast-and-wet-snow":
                    weather_cloud.setImageResource(R.drawable.snowing);
                    break;
                case "partly-cloudy-and-light-snow":
                    weather_cloud.setImageResource(R.drawable.snowing);
                    break;
                case "partly-cloudy-and-snow":
                    weather_cloud.setImageResource(R.drawable.snowing);
                    break;
                case "overcast-and-snow":
                    weather_cloud.setImageResource(R.drawable.snowing);
                    break;
                case "cloudy-and-light-snow":
                    weather_cloud.setImageResource(R.drawable.snowing);
                    break;
                case "overcast-and-light-snow":
                    weather_cloud.setImageResource(R.drawable.snowing);
                    break;
                case "cloudy-and-snow":
                    weather_cloud.setImageResource(R.drawable.snowing);
                    break;
            }
        }
        if (isF) {
            if (weather.getMain().getTemp() > 0) feelsLikeTemp.setText(String.format("+" + "%.0f" + "°С",
                    weather.getMain().getFeels_like()));
            else feelsLikeTemp.setText(String.format("%.0f °С", weather.getMain().getFeels_like()));
        } else {
            float far = (weather.getMain().getFeels_like() * 9 / 5) + 32;
            if (far > 0) feelsLikeTemp.setText(String.format("+" + "%.0f" + "F", far));
            else feelsLikeTemp.setText(String.format("%.0f F", far));
        }

        condition.setText(weather.getWeather().get(0).getDescription());

        pressure.setText(weather.getMain().getPressure() + " мм рт. ст.");

        windy.setText(weather.getWind().getSpeed() + " м/c"); // + direction.get(weather.getWind().getDeg()));

        humidity.setText(Math.round(weather.getMain().getHumidity()) + "%");
    }

    @Override
    public void onLoad(Response response) {
        switch (response.type) {
            case ResponseType.GGEOPOSITION:
                position = (String[]) response.response;
                Log.d("position", position[0] + " " + position[1]);
                mStorage.setPosition(position[0], position[1]);
                break;
            case ResponseType.WTODAY:
                swipeContainer.setRefreshing(false);
                final Fact n = (Fact) response.response;
                final Date current = new Date(System.currentTimeMillis());
                f = new List(){{
                    setDt_txt(new SimpleDateFormat("dd.MM HH:mm").format(current));
                    setMain(n.getMain());
                    setClouds(n.getClouds());
                    setDt(n.getDt());
                    setSys(n.getSys());
                    setWeather(n.getWeather());
                    setWind(n.getWind());
                }};
                onDayChanged(f, today);
                mStorage.getClothes(f);
                break;
            case ResponseType.COMMUNITY:
                final String[] res = (String[]) response.response;
                String community = res[2].trim();
                if (res[2].equals("") && res[0].equals("null") && res[1].equals("null"))
                    res[2] = "Текущее";
                Log.d("community", "community = " + community);
                if (!first) {
                    if (!cities.contains(res[2])) {
                        cities.add(0, res[2]);
                        cityPos.put(res[2], new String[]{res[0], res[1]});
                        Collections.sort(cities);
                        arrayAdapter = new ArrayAdapter<String>(getContext(), R.layout.spinner_item, new LinkedList<String>(){{
                            add("+Добавить новый");
                            addAll(cities);}});
                        }
                    else {
                        arrayAdapter = new ArrayAdapter<String>(getContext(), R.layout.spinner_item, new LinkedList<String>(){{
                            add("+Добавить новый");
                            addAll(cities);}});
                    }
                    spinnerCity.setAdapter(arrayAdapter);
                    spinnerLatest = cities.indexOf(res[2]) + 1;
                    spinnerCity.setSelection(spinnerLatest);
                    spinnerCity.setOnItemSelectedListener(this);
                    first = true;
                    spinnerCity.setEnabled(false);
                    spinnerCity.setClickable(false);
                }
                else if (!cities.contains(res[2])) {
                    cities.add(0, res[2]);
                    Collections.sort(cities);
                    arrayAdapter = new ArrayAdapter<String>(getContext(), R.layout.spinner_item, new LinkedList<String>(){{
                        add("+Добавить новый");
                        addAll(cities);}});
                    cityPos.put(res[2], new String[]{res[0], res[1]});
                    spinnerCity.setAdapter(arrayAdapter);
                    spinnerLatest = cities.indexOf(res[2]) + 1;
                    spinnerCity.setSelection(spinnerLatest);
                }
                else {
                    spinnerLatest = cities.indexOf(res[2]) + 1;
                    spinnerCity.setSelection(spinnerLatest);
                }
                break;
            case ResponseType.CLOTHES:
                ArrayList<String> recommendations = (ArrayList<String>) response.response;
                rv_clothes.setAdapter(new RecomendationListAdapter(recommendations));
                break;
            case ResponseType.WFORECASTS:
                java.util.List<List> forecasts = (java.util.List<List>) response.response;
//                if (today == null){
//                    today = forecasts.get(0).getDate();
//                }
//                forecasts.get(0).getParts().getDay_short().setTemp(f.getTemp());
//                forecasts.get(0).getParts().getDay_short().setImageIcon(f.getImageIcon());
//                forecasts.get(0).getParts().getDay_short().setCondition(f.getCondition());
                final List fact = f;
                final Date d1 = new Date(System.currentTimeMillis());
                Calendar c1 = Calendar.getInstance();
                c1.setTime(d1);
                Calendar c2 = Calendar.getInstance();
                c2.setTime(new Date(forecasts.get(0).getDt() * 1000));
                int start = Math.max(0, (c1.get(Calendar.HOUR_OF_DAY) / 3 - c2.get(Calendar.HOUR_OF_DAY) / 3));
                List changed = forecasts.get(start);
                changed.setDt_txt(new SimpleDateFormat("yyyy-MM-dd HH:mm:ss").format(d1));
                changed.setMain(fact.getMain());
                changed.setClouds(fact.getClouds());
                changed.setDt(fact.getDt());
                changed.setSys(fact.getSys());
                changed.setWeather(fact.getWeather());
                changed.setWind(fact.getWind());
//                    setRain(new Rain());
//                System.out.println(gson.fromJson(gson.toJson(mStorage.getResponse(), WeatherData.class), WeatherData.class));
                today = parseDate(forecasts.get(start).getDt_txt());
                Weather_Adapter adapter = new Weather_Adapter(getContext(), forecasts.subList(0 , 40), isF);
                adapter.setClickListener(this);
                rv.setAdapter(adapter);
                progressBar.setVisibility(ProgressBar.INVISIBLE);
                spinnerCity.setVisibility(Spinner.VISIBLE);
                nowDate.setVisibility(TextView.VISIBLE);
                nowDateDes.setVisibility(TextView.VISIBLE);
                textFeelsLike.setVisibility(TextView.VISIBLE);
                imagePressure.setVisibility(ImageView.VISIBLE);
                imageWindy.setVisibility(ImageView.VISIBLE);
                imageHumdity.setVisibility(ImageView.VISIBLE);
                date.setText(dateText);
                spinnerCity.setEnabled(true);
                spinnerCity.setClickable(true);
                break;
            case ResponseType.GEOERROR:
                checkInternetConnection = new CheckInternetConnection(getContext());
                checkInternetConnection.executeOnExecutor(AsyncTask.THREAD_POOL_EXECUTOR);
                break;
        }
    }

    private void getPermissions(){
        while (ActivityCompat.checkSelfPermission(getContext(), Manifest.permission.ACCESS_FINE_LOCATION)
                != PackageManager.PERMISSION_GRANTED || ActivityCompat.checkSelfPermission
                (getContext(), Manifest.permission.ACCESS_COARSE_LOCATION) != PackageManager.PERMISSION_GRANTED) {
            ActivityCompat.requestPermissions(getActivity(), new String[]{Manifest.permission.ACCESS_FINE_LOCATION, Manifest.permission.ACCESS_COARSE_LOCATION}, 1);
        }
    }

    @Override
    public void onItemSelected(AdapterView<?> parent, View view, int position, long id) {
        if (position == 0){
            AlertDialog.Builder builder = new AlertDialog.Builder(getContext());
            builder.setTitle("Введите название города");
// Set up the input
            final EditText input = new EditText(getContext());
// Specify the type of input expected; this, for example, sets the input as a password, and will mask the text
            input.setInputType(InputType.TYPE_CLASS_TEXT);
            builder.setView(input);
            spinnerCity.setSelection(spinnerLatest);
// Set up the buttons
            builder.setPositiveButton("Найти", new DialogInterface.OnClickListener() {
                @Override
                public void onClick(DialogInterface dialog, int which) {
                    spinnerCity.setEnabled(false);
                    spinnerCity.setClickable(false);
                    checkInternetConnection = new CheckInternetConnection(getContext(),
                            input.getText().toString());
                    checkInternetConnection.executeOnExecutor(AsyncTask.THREAD_POOL_EXECUTOR);
                }
            });
            builder.setNegativeButton("Отмена", new DialogInterface.OnClickListener() {
                @Override
                public void onClick(DialogInterface dialog, int which) {
                    dialog.cancel();
                }
            });

            builder.show();
        }
        else {
            checkInternetConnection = new CheckInternetConnection(getContext(),
                    cityPos.get(cities.get(position - 1)));
            checkInternetConnection.executeOnExecutor(AsyncTask.THREAD_POOL_EXECUTOR);
            spinnerLatest = position;
        }
    }

    @Override
    public void onNothingSelected(AdapterView<?> parent) {

    }

    private ActivityManager.MemoryInfo getAvailableMemory() {
        ActivityManager activityManager = (ActivityManager) getActivity().getSystemService(ACTIVITY_SERVICE);
        ActivityManager.MemoryInfo memoryInfo = new ActivityManager.MemoryInfo();
        activityManager.getMemoryInfo(memoryInfo);
        return memoryInfo;
    }
}
