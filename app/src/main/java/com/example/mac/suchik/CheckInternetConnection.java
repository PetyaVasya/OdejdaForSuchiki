package com.example.mac.suchik;

import android.content.Context;
import android.net.ConnectivityManager;
import android.net.NetworkInfo;
import android.os.AsyncTask;

public class CheckInternetConnection extends AsyncTask {

    private Context context;
    private String[] position;
    private String city;
    private int dataType;

    public CheckInternetConnection(Context context){
        this.context = context;
        this.dataType = DataType.NULL;
    }

    public CheckInternetConnection(Context context, String city){
        this.context = context;
        this.city = city;
        this.dataType = DataType.CITY;
    }

    public CheckInternetConnection(Context context, String[] position){
        this.context = context;
        this.position = position;
        this.dataType = DataType.POSITION;
    }

    private static boolean hasConnection(final Context context)
    {
        ConnectivityManager cm = (ConnectivityManager)context.getSystemService(Context.CONNECTIVITY_SERVICE);
        NetworkInfo wifiInfo = cm.getNetworkInfo(ConnectivityManager.TYPE_WIFI);
        if (wifiInfo != null && wifiInfo.isConnected())
        {
            return true;
        }
        wifiInfo = cm.getNetworkInfo(ConnectivityManager.TYPE_MOBILE);
        if (wifiInfo != null && wifiInfo.isConnected())
        {
            return true;
        }
        wifiInfo = cm.getActiveNetworkInfo();
        if (wifiInfo != null && wifiInfo.isConnected())
        {
            return true;
        }
        return false;
    }

    @Override
    protected Object doInBackground(Object[] objects) {
        while (!hasConnection(context)){
            if(isCancelled()){
                break;
            }
        }
        return null;
    }

    @Override
    protected void onPostExecute(Object o) {
        super.onPostExecute(o);
        Geoposition geoposition;
        switch (dataType) {
            case DataType.NULL:
                geoposition = new Geoposition(context);
                position = geoposition.start();
                break;
            case DataType.CITY:
                geoposition = new Geoposition(context, city);
                position = geoposition.start();
        }
        (Storage.getOrCreate(null)).setPosition(position[0], position[1]);
    }
}
