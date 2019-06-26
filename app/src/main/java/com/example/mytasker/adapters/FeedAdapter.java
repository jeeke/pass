package com.example.mytasker.adapters;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import com.example.mytasker.R;
import com.example.mytasker.models.Feed;

import java.util.ArrayList;
import java.util.List;

public class FeedAdapter extends RecyclerView.Adapter<FeedAdapter.holder> {

    private ArrayList<Feed> list;
    private RecyclerViewClickListener mListener;

    public interface RecyclerViewClickListener {
        void onClick(View view, int position);
    }

    public FeedAdapter(Context context, ArrayList<Feed> list) {
        this.list = list;
        this.mListener = (RecyclerViewClickListener) context;
    }

    @NonNull
    @Override
    public holder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(parent.getContext()).inflate(R.layout.card_feed, parent, false);
        return new holder(view);
    }


    public void update(List<Feed> list) {
        this.list.clear();
        if(list!=null)
            this.list.addAll(list);
        notifyDataSetChanged();
    }

    @Override
    public void onBindViewHolder(@NonNull holder holder, int position) {
        View v = holder.itemView.findViewById(R.id.lyt_parent);
//        switch (position%4){
//            case 0:v.setBackgroundColor(((Context)mListener).getResources().getColor(R.color.orange_100));
//            break;
//            case 1:v.setBackgroundColor(((Context)mListener).getResources().getColor(R.color.green_100));
//            break;
//            case 2:v.setBackgroundColor(((Context)mListener).getResources().getColor(R.color.blue_100));
//            break;
//            case 3:v.setBackgroundColor(((Context)mListener).getResources().getColor(R.color.yellow_100));
//            break;
//        }
    }

    @Override
    public int getItemCount() {
//        TODO
        return 7;
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


