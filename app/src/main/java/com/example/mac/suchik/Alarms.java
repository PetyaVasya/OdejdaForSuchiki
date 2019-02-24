package com.example.mac.suchik;

import android.app.AlarmManager;
import android.app.NotificationChannel;
import android.app.NotificationManager;
import android.app.PendingIntent;
import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.graphics.Color;
import android.os.SystemClock;
import android.widget.Toast;

import com.google.gson.Gson;

import java.text.DateFormat;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.Date;
import java.util.HashSet;
import java.util.LinkedList;
import java.util.Locale;
import java.util.Objects;
import java.util.Set;

public class Alarms {

    private NotificationManager mNotificationManager;
    private AlarmManager alarmManager;
    private PendingIntent notifyPendingIntent;
    private Intent notifyIntent;
    private Context context;
    private SharedPreferences sp;
    private ArrayList<AlarmClock> arcl;
    private Gson gson = new Gson();

    public Alarms(Context context) {
        this.context = context;
        notifyIntent = new Intent(context, AlarmReceiver.class);
        mNotificationManager = (NotificationManager) context.getSystemService(context.NOTIFICATION_SERVICE);
        alarmManager = (AlarmManager) context.getSystemService(context.ALARM_SERVICE);
        sp = context.getSharedPreferences("alarms", Context.MODE_PRIVATE);

//        SharedPreferences.Editor s = sp.edit();
//
//        s.putString("alarms", "");
//        s.commit();
        if (Objects.equals(sp.getString("alarms", ""), ""))
            arcl = new ArrayList<>();
        else
            arcl = gson.fromJson(sp.getString("alarms", ""), AlarmsClock.class).getAlarmClock();
    }

    public ArrayList<AlarmClock> getAlarmsClock() {
        return arcl;
    }

    public boolean createNotification(int hours, int mins) {
        int nowID = (int) SystemClock.elapsedRealtime();
        Calendar calendar = Calendar.getInstance();
        calendar.set(Calendar.HOUR_OF_DAY, hours);
        calendar.set(Calendar.MINUTE, mins);
        calendar.set(Calendar.SECOND, 0);
        Boolean exist = false;
        DateFormat dateFormat = new SimpleDateFormat("HH.mm", Locale.getDefault());
        for (AlarmClock alarmClock: arcl){
            if (alarmClock.getTime().equals(dateFormat.format(calendar.getTime()))){
                exist = true;
                break;
            }
        }
        if (!exist) {
            notifyPendingIntent = PendingIntent.getBroadcast
                    (context, nowID, notifyIntent, PendingIntent.FLAG_UPDATE_CURRENT);
            alarmManager.setRepeating(AlarmManager.RTC_WAKEUP,
            calendar.getTimeInMillis(), AlarmManager.INTERVAL_DAY, notifyPendingIntent);
            arcl.add(new AlarmClock(dateFormat.format(calendar.getTime()), nowID + ""));
        }
        return exist;
    }

    public void removeAllNotification() {
        for (AlarmClock s : arcl) {
            PendingIntent notS = PendingIntent.getBroadcast
                    (context, Integer.parseInt(s.getId()), notifyIntent, PendingIntent.FLAG_UPDATE_CURRENT);
            alarmManager.cancel(notS);
        }
        arcl = new ArrayList<>();
        mNotificationManager.cancelAll();
    }

    public void removeNotification(int id, int elementID) {
        PendingIntent notS = PendingIntent.getBroadcast
                (context, id, notifyIntent, PendingIntent.FLAG_UPDATE_CURRENT);
        alarmManager.cancel(notS);
        arcl.remove(elementID);
    }

    public void saveData() {
        SharedPreferences.Editor edit = sp.edit();
        AlarmsClock s = new AlarmsClock();
        s.setAlarmClock(arcl);
        edit.putString("alarms", gson.toJson(s));
        edit.apply();
    }
}
