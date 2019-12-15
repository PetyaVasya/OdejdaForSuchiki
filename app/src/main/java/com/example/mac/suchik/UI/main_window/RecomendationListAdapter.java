package com.example.mac.suchik.UI.main_window;

import androidx.recyclerview.widget.RecyclerView;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import com.example.mac.suchik.R;
import com.example.mac.suchik.UI.settings_page.VH;

import java.util.List;

public class RecomendationListAdapter extends RecyclerView.Adapter<VH> {
    TextView second;
    private List<String> mData;

    public RecomendationListAdapter(List<String> data) {
        super();
        mData = data;
    }
    @Override
    public VH onCreateViewHolder(ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(parent.getContext()).inflate(R.layout.recomendation_list_element, parent, false);
        return new VH(view);
    }

    public void onBindViewHolder(VH holder, int position) {
        String category = mData.get(position).substring(mData.get(position).length() - 1);
        String name;
        name = mData.get(position).substring(0, mData.get(position).length() - 1);
        Log.d("Clothes", "category = " + category);
        switch (Integer.valueOf(category)){
            case 0:
                holder.category.setImageResource(R.drawable.head);
                break;
            case 1:
                holder.category.setImageResource(R.drawable.glove);
                break;
            case 2:
                holder.category.setImageResource(R.drawable.scarf);
                break;
            case 3:
                holder.category.setImageResource(R.drawable.coat);
                break;
            case 4:
                holder.category.setImageResource(R.drawable.jeans);
                break;
            case 5:
                holder.category.setImageResource(R.drawable.tshirt);
                break;
            case 6:
                holder.category.setImageResource(R.drawable.boot);
                break;
            case 7:
                holder.category.setImageResource(R.drawable.eyeglasses);
                break;
            case 8:
                holder.category.setImageResource(R.drawable.joggerpants);
                break;
            case 9:
                holder.category.setImageResource(R.drawable.sweater);
                break;
        }
        holder.tv1.setText(name);
    }

    @Override
    public int getItemCount() {
        return mData.size();
    }


    public void setList(List<String> new_elements){
        mData.clear();
        mData.addAll(new_elements);
        notifyDataSetChanged();
    }


}

