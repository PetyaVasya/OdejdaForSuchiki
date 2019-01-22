package com.example.mac.suchik;

import android.support.annotation.NonNull;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import com.example.mac.suchik.AlarmHolder;
import com.example.mac.suchik.R;
import com.example.mac.suchik.UI.main_window.RecomendationListAdapter;
import com.example.mac.suchik.UI.settings_page.VH;

import java.util.ArrayList;
import java.util.List;

public class AlarmAdapter extends RecyclerView.Adapter<AlarmHolder> {
    TextView second;
    private ArrayList<AlarmClock> mData;
    private View view;
    private Alarms alarms;
    private RecyclerView rv;
    public AlarmAdapter(ArrayList<AlarmClock> data, Alarms alarms, RecyclerView rv) {
        mData = data;
        this.alarms = alarms;
        this.rv = rv;
    }

    @Override
    public AlarmHolder onCreateViewHolder(ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(parent.getContext()).inflate(R.layout.alarm_layout, parent, false);
        this.view = view;
        return new AlarmHolder(view);
    }

    class ClickListener implements View.OnClickListener {
        private int id;

        ClickListener(int id){
            this.id = id;
        }

        @Override
        public void onClick(View v) {
            alarms.removeNotification(Integer.parseInt(mData.get(id).getId()), id);
            rv.setAdapter(new AlarmAdapter(alarms.getAlarmsClock(), alarms, rv));
        }
    }

    @Override
    public void onBindViewHolder(@NonNull final AlarmHolder alarmHolder, int i) {
        alarmHolder.getNum().setText((i + 1)+ "");
        alarmHolder.getTime().setText(mData.get(i).getTime());
        alarmHolder.getRemove().setOnClickListener(new ClickListener(i));
    }

    @Override
    public int getItemCount() {
        return mData.size();
    }


    public void setList(ArrayList<AlarmClock> new_elements){
        mData.clear();
        mData.addAll(new_elements);
    }
}
