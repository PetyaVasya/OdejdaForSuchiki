package com.example.mac.suchik.UI.settings_page;

import androidx.recyclerview.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import com.example.mac.suchik.R;

import java.util.List;

public class TimesListAdapter extends RecyclerView.Adapter<VH> {

    private List<String> mData;

    public TimesListAdapter(List<String> data) {
        super();
        mData = data;
    }

    @Override
    public VH onCreateViewHolder(ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(parent.getContext()).inflate(R.layout.recomendation_list_element, parent, false);
        return new VH(view);
    }

    public void onBindViewHolder(VH holder, int position) {
        holder.tv1.setText(mData.get(position));
    }

    @Override
    public int getItemCount() {
        return mData.size();
    }
}


