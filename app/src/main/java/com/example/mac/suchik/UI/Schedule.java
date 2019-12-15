package com.example.mac.suchik.UI;

import android.os.Bundle;
import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.TextView;
import android.widget.TimePicker;

import com.example.mac.suchik.AlarmAdapter;
import com.example.mac.suchik.AlarmClock;
import com.example.mac.suchik.Alarms;
import com.example.mac.suchik.R;

import java.util.ArrayList;

public class Schedule extends Fragment {
    TextView tv;
    Button alarm_on, alarm_off;
    TimePicker timePicker;
    RecyclerView rv;
    Alarms alarms;
    private AlarmAdapter alarmAdapter;

    @Override
    public void onStop() {
        super.onStop();
        alarms.saveData();
    }

    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        return inflater.inflate(R.layout.schedule, container, false);

    }

    @Override
    public void onCreateOptionsMenu(Menu menu, MenuInflater inflater) {
        super.onCreateOptionsMenu(menu, inflater);
        inflater.inflate(R.menu.main_menu, menu);
    }

    @Override
    public void onViewCreated(@NonNull final View view, @Nullable Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);
        rv = view.findViewById(R.id.listOfAlarms);
        tv = view.findViewById(R.id.updateTimeText);
        alarm_on = view.findViewById(R.id.alarm_on);
        alarm_off = view.findViewById(R.id.alarm_off);
        alarms = new Alarms(view.getContext());
        timePicker = view.findViewById(R.id.timePicker);
        alarmAdapter = new AlarmAdapter(alarms);
        alarm_on.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if (!alarms.createNotification(timePicker.getCurrentHour(), timePicker.getCurrentMinute())) {
                    ArrayList<AlarmClock> al = alarms.getAlarmsClock();
                    alarmAdapter.addItem(al.get(al.size() - 1));
                }
            }
        });
        alarm_off.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                alarms.removeAllNotification();
                alarmAdapter.setList(new ArrayList<AlarmClock>());

            }
        });
        rv.setLayoutManager(new LinearLayoutManager(getContext(), LinearLayoutManager.VERTICAL, false));
        rv.setAdapter(alarmAdapter);
    }
}
