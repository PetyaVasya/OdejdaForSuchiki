package com.example.mac.suchik;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import java.util.ArrayList;

public class AlarmAdapter extends RecyclerView.Adapter<AlarmHolder> {
    TextView second;
    private ArrayList<AlarmClock> mData;
    private Alarms alarms;
    public AlarmAdapter(final Alarms alarms) {
        mData = new ArrayList<AlarmClock>(){{addAll(alarms.getAlarmsClock());}};
        this.alarms = alarms;
    }

    @Override
    public AlarmHolder onCreateViewHolder(ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(parent.getContext()).inflate(R.layout.alarm_layout, parent, false);
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
            removeItem(id);
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
        notifyDataSetChanged();
    }

    public void removeItem(int pos){
        mData.remove(pos);
        notifyDataSetChanged();
    }

    public void addItem(AlarmClock alarmClock){
        mData.add(alarmClock);
        notifyDataSetChanged();
    }
}
