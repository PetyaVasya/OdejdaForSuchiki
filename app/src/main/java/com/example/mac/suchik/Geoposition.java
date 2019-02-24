package com.example.mac.suchik;

import android.Manifest;
import android.app.Activity;
import android.app.AlertDialog;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.location.Address;
import android.location.Geocoder;
import android.location.Location;
import android.location.LocationListener;
import android.location.LocationManager;
import android.os.Bundle;
import android.provider.Settings;
import android.support.annotation.NonNull;
import android.support.v4.app.ActivityCompat;
import android.util.Log;

import java.io.IOException;
import java.util.List;
import java.util.Locale;


public class Geoposition implements LocationListener {
    private static final int REQUEST_LOCATION = 1;
    private LocationManager locationManager;
    private static Context mContext;
    private String name;

    // flag for GPS status
    boolean isGPSEnabled = false;

    // flag for network status
    boolean isNetworkEnabled = false;

    // flag for GPS status
    boolean canGetLocation = false;

    Location location; // location
    double latitude; // latitude
    double longitude; // longitude

    // The minimum distance to change Updates in meters
    private static final long MIN_DISTANCE_CHANGE_FOR_UPDATES = 0; // 10 meters

    // The minimum time between updates in milliseconds
    private static final long MIN_TIME_BW_UPDATES = 0; // 1 minute


    public Geoposition(Context mContext) {
        this.mContext = mContext;
    }

    public Geoposition(Context mContext, String name) {
        this.mContext = mContext;
        this.name = name;
    }
    public String[] start(){
        locationManager = (LocationManager)mContext.getSystemService(Context.LOCATION_SERVICE);
        if (name == null) {
            if (ActivityCompat.checkSelfPermission(mContext, Manifest.permission.ACCESS_FINE_LOCATION)
                    != PackageManager.PERMISSION_GRANTED && ActivityCompat.checkSelfPermission
                    (mContext, Manifest.permission.ACCESS_COARSE_LOCATION) != PackageManager.PERMISSION_GRANTED) {
                ActivityCompat.requestPermissions((Activity) mContext, new String[]{Manifest.permission.ACCESS_FINE_LOCATION, Manifest.permission.ACCESS_COARSE_LOCATION}, REQUEST_LOCATION);
                if (ActivityCompat.checkSelfPermission(mContext, Manifest.permission.ACCESS_FINE_LOCATION)
                        == PackageManager.PERMISSION_GRANTED && ActivityCompat.checkSelfPermission
                        (mContext, Manifest.permission.ACCESS_COARSE_LOCATION) == PackageManager.PERMISSION_GRANTED)
                    return start();
                else
                    return new String[]{null, null};
            } else if (!locationManager.isProviderEnabled(LocationManager.GPS_PROVIDER)) {
                buildAlertMessageNoGps();
                if (locationManager.isProviderEnabled(LocationManager.GPS_PROVIDER))
                    return start();
                return new String[]{null, null};
            } else if (locationManager.isProviderEnabled(LocationManager.GPS_PROVIDER)) {
                return getLocation();
            } else return new String[]{null, null};
        }
        else {
            return getLocationByName(name);
        }
    }


    private String[] getLocation() {

        String[] position = new String[2];
        try {

            locationManager = (LocationManager) mContext
                    .getSystemService(Context.LOCATION_SERVICE);

            // getting GPS status
            isGPSEnabled = locationManager
                    .isProviderEnabled(LocationManager.GPS_PROVIDER);

            // getting network status
            isNetworkEnabled = locationManager
                    .isProviderEnabled(LocationManager.NETWORK_PROVIDER);



            if (ActivityCompat.checkSelfPermission(mContext, Manifest.permission.ACCESS_FINE_LOCATION) != PackageManager.PERMISSION_GRANTED){}

            if (!isGPSEnabled && !isNetworkEnabled) {
                Log.d("network", "no network provider is enabled");
                // no network provider is enabled
            } else {
                this.canGetLocation = true;
                Log.d("network", "GPS " + isGPSEnabled + " Network " + isNetworkEnabled);
                if (isNetworkEnabled) {
                    locationManager.requestLocationUpdates(
                            LocationManager.NETWORK_PROVIDER,
                            MIN_TIME_BW_UPDATES,
                            MIN_DISTANCE_CHANGE_FOR_UPDATES, this);
                    Log.d("Network", "Network");
                    if (locationManager != null) {
                        location = locationManager.getLastKnownLocation(LocationManager.NETWORK_PROVIDER);
                        if (location != null) {
                            latitude = location.getLatitude();
                            longitude = location.getLongitude();
                            position[0] = String.valueOf(latitude);
                            position[1] = String.valueOf(longitude);
                        } else Log.d("Network", "location == null");
                    } else Log.d("Network", "locationManager == null");
                }
                // if GPS Enabled get lat/long using GPS Services
                if (isGPSEnabled) {

                    if (location == null) {
                        locationManager.requestLocationUpdates(
                                LocationManager.GPS_PROVIDER,
                                MIN_TIME_BW_UPDATES,
                                MIN_DISTANCE_CHANGE_FOR_UPDATES, this);
                        Log.d("GPS Enabled", "GPS Enabled");
                        if (locationManager != null) {
                            location = locationManager
                                    .getLastKnownLocation(LocationManager.GPS_PROVIDER);
                            if (location != null) {
                                latitude = location.getLatitude();
                                longitude = location.getLongitude();
                                position[0] = String.valueOf(latitude);
                                position[1] = String.valueOf(longitude);
                            } else Log.d("Network", "location == null");
                        } else Log.d("Network", "locationManager == null");
                    }
                }
            }

        } catch (Exception e) {
            throw e;
        }
        return position;
    }

    private String[] getLocationByName(String name){
        Geocoder geocoder = new Geocoder(mContext);
        List<Address> addresses = null;
        try {
            addresses = geocoder.getFromLocationName(name, 1);
        } catch (IOException e) {
            e.printStackTrace();
        }
        if (addresses == null)
            return new String[]{null, null};
        else if(addresses.size() > 0) {
            return new String[]{String.valueOf(addresses.get(0).getLatitude()),
                    String.valueOf(addresses.get(0).getLongitude())};
        }
        return new String[]{null, null};
    }

    protected void buildAlertMessageNoGps() {

        final AlertDialog.Builder builder = new AlertDialog.Builder(mContext);
        builder.setMessage("Please Turn ON your GPS Connection")
                .setCancelable(false)
                .setPositiveButton("Yes", new DialogInterface.OnClickListener() {
                    public void onClick(final DialogInterface dialog, final int id) {
                        mContext.startActivity(new Intent(Settings.ACTION_LOCATION_SOURCE_SETTINGS));
                    }
                })
                .setNegativeButton("No", new DialogInterface.OnClickListener() {
                    public void onClick(final DialogInterface dialog, final int id) {
                        dialog.cancel();
                    }
                });
        final AlertDialog alert = builder.create();
        alert.show();
    }

    @Override
    public void onLocationChanged(Location location) {
        Log.d("Network", "Location changed");
        locationManager.removeUpdates(this);
    }

    @Override
    public void onStatusChanged(String provider, int status, Bundle extras) {

    }

    @Override
    public void onProviderEnabled(String provider) {

    }

    @Override
    public void onProviderDisabled(String provider) {

    }


}

