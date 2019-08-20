package com.example.mytasker.adapters;

import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import com.example.mytasker.R;
import com.example.mytasker.models.Answer;

import java.util.ArrayList;
import java.util.List;

public class AnswerAdapter extends RecyclerView.Adapter<AnswerAdapter.holder> {

    private ArrayList<Answer> list;

    public interface RecyclerViewClickListener {
        void onClick(View view, int position);
    }

    public AnswerAdapter( ArrayList<Answer> list) {
        this.list = list;
    }

    @NonNull
    @Override
    public holder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(parent.getContext()).inflate(R.layout.card_answer, parent, false);
        return new holder(view);
    }

    public void update(List<Answer> list) {
        this.list.clear();
        this.list.addAll(list);
        notifyDataSetChanged();
    }

    public void addItem(Answer answer) {
        list.add(answer);
        notifyDataSetChanged();
    }

    @Override
    public void onBindViewHolder(@NonNull holder holder, int position) {
        View mView = holder.itemView;
        Answer current = list.get(position);
        ((TextView) mView.findViewById(R.id.name)).setText("by "+ current.getName());
        ((TextView) mView.findViewById(R.id.answer)).setText(current.getAnswer());
    }

    @Override
    public int getItemCount() {
        return list.size();
    }

    public class holder extends RecyclerView.ViewHolder {
        View itemView;

        holder(@NonNull View itemView) {
            super(itemView);
            this.itemView = itemView;
        }

    }
}


