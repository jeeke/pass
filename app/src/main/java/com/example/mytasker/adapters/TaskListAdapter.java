package com.example.mytasker.adapters;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import com.example.mytasker.R;
import com.example.mytasker.models.Task;

import java.util.ArrayList;
import java.util.List;

public class TaskListAdapter extends RecyclerView.Adapter<TaskListAdapter.holder> {

    private ArrayList<Task> list;
    private RecyclerViewClickListener mListener;

    public interface RecyclerViewClickListener {
        void onClick(View view, int position);
    }

    public TaskListAdapter(Context context, ArrayList<Task> list) {
        this.list = list;
        this.mListener = (RecyclerViewClickListener) context;
    }

    @NonNull
    @Override
    public holder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(parent.getContext()).inflate(R.layout.card_task, parent, false);
        return new holder(view);
    }

    public void update(List<Task> list) {
        this.list.clear();
        this.list.addAll(list);
        notifyDataSetChanged();
    }

    @Override
    public void onBindViewHolder(@NonNull holder holder, int position) {
        View mView = holder.itemView;
        Context context = (Context) mListener;
        View v = mView.findViewById(R.id.lyt_parent);
        switch (position % 4) {
            case 0:
                v.setBackground(context.getDrawable(R.drawable.bg_soft_orange));
                break;
            case 1:
                v.setBackground(context.getDrawable(R.drawable.bg_orange));
                break;
            case 2:
                v.setBackground(context.getDrawable(R.drawable.bg_green));
                break;
            case 3:
                v.setBackground(context.getDrawable(R.drawable.bg_blue));
        }
        ((TextView) mView.findViewById(R.id.task_desc)).setText(list.get(position).getTitle());
//        ((TextView) mView.findViewById(R.id.task_dist)).setText(list.get(position).getDistance());
//        ((TextView) mView.findViewById(R.id.task_time)).setText(list.get(position).getCategory());
        TextView textView = mView.findViewById(R.id.task_price);
        textView.setText("$" + list.get(position).getCost());
//        textView = mView.findViewById(R.id.task_dist);
//        textView.setText(list.get(position).getAddress());

//        int imageId = R.drawable.google;
//        ImageView imageView = mView.findViewById(R.id.task_cat_image);
//        imageView.setImageResource(imageId);
    }

    @Override
    public int getItemCount() {
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


