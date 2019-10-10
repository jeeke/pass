package com.esselion.pass.holders;

import android.graphics.drawable.Drawable;
import android.view.View;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import com.esselion.pass.R;
import com.esselion.pass.models.Task;
import com.esselion.pass.util.Contracts;

public class TaskHolder extends RecyclerView.ViewHolder implements View.OnClickListener {
    private RecyclerViewClickListener mListener;
    private TextView name, price, dotText;
    private View bg, dot;
    private Task task;

    public TaskHolder(@NonNull View itemView, RecyclerViewClickListener listener) {
        super(itemView);
        this.mListener = listener;
        itemView.setOnClickListener(this);
        name = itemView.findViewById(R.id.poster_name);
        price = itemView.findViewById(R.id.price);
        bg = itemView.findViewById(R.id.lyt_parent);
        dotText = itemView.findViewById(R.id.dotText);
        dot = itemView.findViewById(R.id.dot);
    }

    public Task getTask() {
        return task;
    }

    @Override
    public void onClick(View v) {
        mListener.onClick(v, task);
    }

    public void setItem(Task current, boolean type, Drawable drawable, Drawable drawable2, int c) {
        bg.setBackground(drawable);
        task = current;
        name.setText(current.getTitle());
        price.setText("₹" + current.getCost());
        if (type) setStage(current.getStage(), drawable2, c);
        else dotText.setVisibility(View.GONE);
    }

    private void setStage(int stage, Drawable drawable, int c) {
        dotText.setVisibility(View.VISIBLE);
        dot.setVisibility(View.VISIBLE);
        drawable.setTint(c);
        dot.setBackground(drawable);
        dotText.setText(Contracts.TASK_STAGE_TEXT[stage]);
    }

    public interface RecyclerViewClickListener {
        void onClick(View view, Task task);
    }
}