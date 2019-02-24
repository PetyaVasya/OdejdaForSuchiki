package com.example.mac.suchik;

import android.content.Context;
import android.content.SharedPreferences;
import android.database.Cursor;
import android.os.AsyncTask;
import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.v4.app.LoaderManager;
import android.support.v4.content.CursorLoader;
import android.support.v4.content.Loader;
import android.util.Log;
import com.example.mac.suchik.WeatherData.Fact;
import com.example.mac.suchik.WeatherData.WeatherData;

import java.util.ArrayList;
import java.util.Map;
import java.util.Random;
import java.util.TreeMap;
import java.util.concurrent.TimeUnit;

import javax.security.auth.callback.Callback;

public class GetClothes extends AsyncTask<Void, Void, Response> {

    private static Context mContext;
    DB db;
    String[] columns;
    String selection;
    String[] selectionArgs;
    String groupBy;
    String having;
    String orderBy;
    private Callbacks callbacks;
    Fact weather;

    SharedPreferences settings;

    //String[] names = {"head", "glove", "scarf", "coat", "jeans", "shirt", "boot", "eyeglasses", "jogger_pants", "sweater"};

    TreeMap<Integer, String> names = new TreeMap<>();


    final String LOG_TAG = "myLog";
    public GetClothes(Context mContext, Callbacks callbacks, Fact weather) {
        //Storage.getOrCreate(null).updateWeather(true);
        this.mContext = mContext;
        this.callbacks = callbacks;
        this.weather = weather;
        //this.weather = Storage.getOrCreate(null).getResponse().getFact();
    }


    @Override
    protected Response doInBackground(Void... voids) {

        names.put(0, "head");
        names.put(1, "glove");
        names.put(2, "scarf");
        names.put(3, "coat");
        names.put(4, "jeans");
        names.put(5, "shirt");
        names.put(6, "boot");
        names.put(7, "eyeglasses");
        names.put(8, "jogger_pants");
        names.put(9, "sweater");

        settings = mContext.getSharedPreferences("settings", Context.MODE_PRIVATE);

        //Log.d(LOG_TAG, "temp = " + weather.getTemp() + " rain = " + weather.getPrec_type() + " wind = " +
        //        weather.getWind_speed() + " cloud = " + weather.getCloudness());

        ArrayList<String> recommendations = new ArrayList<>();



        db = new DB(mContext);
        db.open();
        columns = new String[]{db.COLUMN_NAME, db.COLUMN_CATEGORY, db.COLUMN_COLOR};

        if (weather.getTemp() > 15) { // Температура > 15
            selection = (db.COLUMN_TEMP_MAX + " < 31");
            selection += (" and " + db.COLUMN_TEMP_MIN + " > 14");
            if (weather.getPrec_type() != 0) { // Идет дождь
                selection += (" and " + db.COLUMN_RAIN + " > 0");
                if (weather.getWind_speed() > 10) { // Скорость ветра
                    selection += (" and " + db.COLUMN_WIND + " > 0");
                    // Дождевик, футболка, резиновые сапоги, шорты/юбка/легкие брюки
                } else {
                    selection += (" and " + db.COLUMN_WIND + " < 2");
                    // Зонт, резиновые сапоги, футболка, шорты/юбка/легкие брюки
                }
            } else { // Не идет дождь
                selection += (" and " + db.COLUMN_RAIN + " < 2");
                if (weather.getWind_speed() > 10) { // Скорость ветра
                    selection += (" and " + db.COLUMN_WIND + " > 0");
                    if (weather.getCloudness() == 0) { // Облачность
                        selection += (" and " + db.COLUMN_CLOUD + " > 0");
                        recommendations.add("Наденьте светлую одежду");
                    }
                    // Кепка/шляпа, ветровка, легкие брюки/шорты/юбка, футболка, кроссовки/сандалии, солнечные очки
                } else {
                    selection += (" and " + db.COLUMN_WIND + " < 2");
                    if (weather.getCloudness() == 0) { // Облачность
                        selection += (" and " + db.COLUMN_CLOUD + " > 0");
                    }
                    // Кепка/шляпа, легкие брюки/шорты/юбка, футболка, кроссовки/сандалии, солнечные очки
                }
            }
        } else if (weather.getTemp() > 0) {
            selection = (db.COLUMN_TEMP_MAX + " < 16");
            selection += (" and " + db.COLUMN_TEMP_MIN + " > -1");
            if (weather.getPrec_type() != 0) { // Идет дождь
                selection += (" and " + db.COLUMN_RAIN + " > 0");
                //selection += (" and " + db.COLUMN_CLOUD + " < 2");
                if (weather.getWind_speed() > 10) {
                    selection += (" and " + db.COLUMN_WIND + " > 0");
                    // Легкая шапка, перчатки, легкий шарф, непромокаемая куртка, брюки, футболка, резиновые сапоги
                } else {
                    selection += (" and " + db.COLUMN_WIND + " < 2");
                    // Перчатки, легкий шарф, непромокаемая куртка, брюки, футболка, резиновые сапоги, зонт
                }
            } else {
                selection += (" and " + db.COLUMN_RAIN + " < 2");
                // Легкая шапка, перчатки, легкий шарф, легкая куртка/джинсовка, брюки, футболка, ботинки
            }
            //Шапка, шарф, перчатки, брюки

        } else if (weather.getTemp() > -15) {
            selection = (db.COLUMN_TEMP_MAX + " < 1");
            selection += (" and " + db.COLUMN_TEMP_MIN + " > -16");
            //Шапка, теплые перчатки, шарф, куртка/пальто, термобелье, брюки, ботинки, футболка
        } else {
            selection = (db.COLUMN_TEMP_MAX + " < -14");
            selection += (" and " + db.COLUMN_TEMP_MIN + " > -31");
            //Теплая шапка, варежки, теплый шарф, шуба/пуховик/теплая куртка, футболка, валенки/теплые ботинки, кофта, терфомбелье, теплые брюки
        }

        Cursor c = db.getData("clothes", columns, selection, null, null, null, null);

        //Log.d(LOG_TAG, "selection = " + selection);

        //ArrayList<String> clothes = new ArrayList<>();

        ArrayList<Integer> banList = new ArrayList<>();



        for (int i = 0; i < names.size(); i++)
        {
            boolean isBaned = settings.getBoolean(names.get(i), false);
            if (isBaned){
                banList.add(i);
            }
        }

        for (Integer integer : banList) {
            Log.d("Ban", integer.toString());
        }


        TreeMap<String, ArrayList<String>> clothes = new TreeMap<>();

        if (c.moveToFirst()) {
            int nameColIndex = c.getColumnIndex("name");
            int categoryColIndex = c.getColumnIndex("category");
            int colorColIndex = c.getColumnIndex("color");


            do {
                //Log.d(LOG_TAG, "name = " + c.getString(nameColIndex) + " category = " + c.getString(categoryColIndex));
                String category = c.getString(categoryColIndex);

                if (!banList.contains(Integer.valueOf(category))) {

                    String item = c.getString(nameColIndex);
                    if (!c.getString(colorColIndex).isEmpty()) {
                        item += " цвет: " + c.getString(colorColIndex);
                    }

                    item += category;
                    if (clothes.containsKey(category)) {
                        clothes.get(category).add(item);
                    } else {
                        ArrayList<String> list = new ArrayList<>();
                        list.add(item);
                        clothes.put(category, list);
                    }
                }

            } while (c.moveToNext());

        } else
            Log.d(LOG_TAG, "0 rows");

        db.close();

        for (Map.Entry<String, ArrayList<String>> stringArrayListEntry : clothes.entrySet()) {
            //Log.d(LOG_TAG, stringArrayListEntry.getKey() + stringArrayListEntry.getValue());
        }


        for (TreeMap.Entry e : clothes.entrySet()) {
            ArrayList<String> list = (ArrayList) e.getValue();
            int rnd = new Random().nextInt(list.size());
            recommendations.add(list.get(rnd));
        }
        //Log.d(LOG_TAG, "RESULT:");
        for (String recommendation : recommendations) {
            //Log.d(LOG_TAG, recommendation);
        }
        return new Response<>(ResponseType.CLOTHES, recommendations);
    }

    @Override
    protected void onPostExecute(Response response) {
        super.onPostExecute(response);
        if (response.response == null)
            response.type = ResponseType.ERROR;
        callbacks.onLoad(response);
    }
}