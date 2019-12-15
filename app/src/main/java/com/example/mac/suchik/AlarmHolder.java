package com.example.mac.suchik;

import androidx.recyclerview.widget.RecyclerView;
import android.view.View;
import android.widget.Button;
import android.widget.TextView;

class AlarmHolder extends RecyclerView.ViewHolder {
    private TextView num;
    private TextView time;
    private Button remove;

    public TextView getNum() {
        return num;
    }

    public TextView getTime() {
        return time;
    }

    public Button getRemove() {
        return remove;
    }

    AlarmHolder(View itemView) {
        super(itemView);
        num = itemView.findViewById(R.id.num);
        time = itemView.findViewById(R.id.time);
        remove = itemView.findViewById(R.id.remove);
    }
}
