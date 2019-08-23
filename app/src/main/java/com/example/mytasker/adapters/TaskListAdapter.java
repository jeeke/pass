package com.example.mytasker.adapters;

import android.content.Context;
import android.graphics.drawable.Drawable;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import com.example.mytasker.R;
import com.example.mytasker.models.Task;
import com.example.mytasker.util.Contracts;

import java.util.ArrayList;
import java.util.List;

public class TaskListAdapter extends RecyclerView.Adapter<TaskListAdapter.holder> {

    private ArrayList<Task> list;
    private RecyclerViewClickListener mListener;

    public interface RecyclerViewClickListener {
        void onClick(View view, int position);
    }

    boolean type;
    Context context;

    public TaskListAdapter(Context context, RecyclerViewClickListener listener, ArrayList<Task> list, boolean type) {
        this.list = list;
        this.mListener = listener;
        this.context = context;
        this.type = type;
    }

    private void setStage(View v,int stage){
        View dot = v.findViewById(R.id.dot);
        TextView dotText = v.findViewById(R.id.dotText);
        dotText.setVisibility(View.VISIBLE);
        dot.setVisibility(View.VISIBLE);
        Drawable drawable = (context.getDrawable(R.drawable.notification_dot_indicator));
        drawable.setTint(context.getResources().getColor(Contracts.TASK_STAGE_COLORS[stage]));
        dot.setBackground(drawable);
//        dot.setBackgroundColor(((Context)mListener).getResources().getColor(Contracts.QUES_STAGE_COLORS[stage]));
        dotText.setText(Contracts.TASK_STAGE_TEXT[stage]);
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
        Task current = list.get(position);
        ((TextView) mView.findViewById(R.id.tasker_name)).setText(current.getTitle());
        TextView textView = mView.findViewById(R.id.bid_price);
        textView.setText("$" + current.getCost());
        if(type) setStage(mView,current.getStage());
        else mView.findViewById(R.id.dotText).setVisibility(View.GONE);

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


