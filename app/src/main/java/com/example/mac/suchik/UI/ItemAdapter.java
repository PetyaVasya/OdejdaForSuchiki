package com.example.mac.suchik.UI;

import android.content.Context;

import androidx.constraintlayout.widget.ConstraintLayout;
import androidx.recyclerview.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.TextView;

import com.example.mac.suchik.R;

import java.util.ArrayList;
import java.util.List;


public class ItemAdapter extends RecyclerView.Adapter<ItemAdapter.VH_ShowItems> {

    //OkHttp
    private List<String> mData;
    private LayoutInflater mInflater;
    private ItemClickListener mClickListener;

    // data is passed into the constructor
    ItemAdapter(Context context, List<String> data) {
        this.mInflater = LayoutInflater.from(context);
        this.mData = data;
    }

    // inflates the row layout from xml when needed
    @Override
    public VH_ShowItems onCreateViewHolder(ViewGroup parent, int viewType) {
        //LayoutInflater.from(parent.getContext()).inflate(R.layout.homework_layout, parent, false);
        View view = mInflater.inflate(R.layout.item_of_clothes_for_rv, parent, false);
        return new VH_ShowItems(view);
    }

    // binds the data to the TextView in each row
    @Override
    public void onBindViewHolder(VH_ShowItems holder, int position) {
        holder.itemTextView.setText(mData.get(position));
    }

    // total number of rows
    @Override
    public int getItemCount() {
        return mData.size();
    }



    // convenience method for getting data at click position
    String getItem(int id) {
        return mData.get(id);
    }

    // allows clicks events to be caught
    void setClickListener(ItemClickListener itemClickListener) {
        this.mClickListener = itemClickListener;
    }

    // parent activity will implement this method to respond to click events
    public interface ItemClickListener {
        void onItemClick(View view, int position);
    }

    public class VH_ShowItems extends RecyclerView.ViewHolder implements View.OnClickListener {


        //private ItemAdapter.ItemClickListener mClickListener;
        public TextView itemTextView;
        public ConstraintLayout recyclerView;
        private ImageView deleteImageView;

        VH_ShowItems(View itemView) {
            super(itemView);
            itemTextView = itemView.findViewById(R.id.text_list_item);
            recyclerView  = itemView.findViewById(R.id.bg);
            deleteImageView = itemView.findViewById(R.id.del);
            deleteImageView.setOnClickListener(this);
            //itemView.setOnClickListener(this);
        }

        @Override
        public void onClick(View view) {
            if (mClickListener != null) mClickListener.onItemClick(view, getAdapterPosition());
        }

    }
    public void setList(ArrayList<String> new_elements){
        mData.clear();
        mData.addAll(new_elements);
        notifyDataSetChanged();
    }

}