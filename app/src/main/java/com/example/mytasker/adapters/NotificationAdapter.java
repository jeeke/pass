package com.example.mytasker.adapters;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import com.example.mytasker.R;
import com.example.mytasker.models.Notification;

import java.util.ArrayList;
import java.util.List;

public class NotificationAdapter extends RecyclerView.Adapter<NotificationAdapter.holder> {

    private ArrayList<Notification> list;
    private RecyclerViewClickListener mListener;

    public interface RecyclerViewClickListener {
        void onClick(View view, int position);
    }

    public NotificationAdapter(Context context, ArrayList<Notification> list) {
        this.list = list;
        this.mListener = (RecyclerViewClickListener) context;
    }

    @NonNull
    @Override
    public holder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(parent.getContext()).inflate(R.layout.card_notification, parent, false);
        return new holder(view);
    }


    public void update(List<Notification> list) {
        this.list.clear();
        if(list!=null)
            this.list.addAll(list);
        notifyDataSetChanged();
    }

    @Override
    public void onBindViewHolder(@NonNull holder holder, int position) {
        View v = holder.itemView;
    }

    @Override
    public int getItemCount() {
//        TODO
        return list.size();
    }

    public class holder extends RecyclerView.ViewHolder implements View.OnClickListener {
        View itemView;

        holder(@NonNull View itemView) {
            super(itemView);
            this.itemView = itemView;
            itemView.setOnClickListener(this);
        }

        @Override
        public void onClick(View v) {
            mListener.onClick(v, getAdapterPosition());
        }
    }
}


