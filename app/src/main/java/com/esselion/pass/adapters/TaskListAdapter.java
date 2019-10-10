package com.esselion.pass.adapters;

import android.content.Context;
import android.graphics.drawable.Drawable;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import com.esselion.pass.R;
import com.esselion.pass.holders.TaskHolder;
import com.esselion.pass.models.Task;
import com.esselion.pass.util.Contracts;

import java.util.ArrayList;
import java.util.List;

public class TaskListAdapter extends RecyclerView.Adapter<TaskHolder> {

    private ArrayList<Task> list;
    private boolean type;
    private Context context;
    private TaskHolder.RecyclerViewClickListener mListener;

    public TaskListAdapter(Context context, TaskHolder.RecyclerViewClickListener listener, ArrayList<Task> list, boolean type) {
        this.list = list;
        this.context = context;
        this.type = type;
        this.mListener = listener;
    }

    @NonNull
    @Override
    public TaskHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(parent.getContext()).inflate(R.layout.card_task, parent, false);
        return new TaskHolder(view, mListener);
    }

    public void update(List<Task> list) {
        this.list.clear();
        this.list.addAll(list);
        notifyDataSetChanged();
    }

    public void addItem(Task list) {
        this.list.add(list);
        notifyDataSetChanged();
    }

    @Override
    public void onBindViewHolder(@NonNull TaskHolder holder, int position) {
        Drawable drawable;
        switch (position % 4) {
            case 0:
                drawable = context.getDrawable(R.drawable.bg_soft_orange);
                break;
            case 2:
                drawable = context.getDrawable(R.drawable.bg_green);
                break;
            case 1:
                drawable = context.getDrawable(R.drawable.bg_blue);
                break;
            default:
                drawable = context.getDrawable(R.drawable.bg_orange);
        }
        Drawable drawable2 = (context.getDrawable(R.drawable.notification_dot_indicator));
        int c = context.getResources().getColor(Contracts.TASK_STAGE_COLORS[list.get(position).getStage()]);
        holder.setItem(list.get(position), false, drawable, drawable2, c);
    }

    @Override
    public int getItemCount() {
        return list.size();
    }
}


