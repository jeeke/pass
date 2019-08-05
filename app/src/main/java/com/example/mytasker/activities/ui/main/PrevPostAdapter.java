package com.example.mytasker.activities.ui.main;

import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import com.example.mytasker.R;
import com.example.mytasker.models.Task;

import java.util.ArrayList;

public class PrevPostAdapter extends RecyclerView.Adapter<PrevPostAdapter.ViewHolder> {

    int index;
    ArrayList<Task> list;

    public PrevPostAdapter(ArrayList<Task> list, int index) {
        this.index = index;
    }

    @NonNull
    @Override
    public ViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        LayoutInflater inflater = LayoutInflater.from(parent.getContext());
        View item = inflater.inflate(getId(), parent, false);
        return new ViewHolder(item);
    }

    private int getId() {
        switch (index) {
            case 1:
                return R.layout.card_task_posted;
            case 2:
            case 3:
                return R.layout.card_task_done;
            case 4:
            case 5:
            case 6:
            case 7:
            case 8:
            case 9:
            default:
                return 0;
        }
    }

    @Override
    public void onBindViewHolder(ViewHolder holder, int position) {
        holder.title.setText(list.get(position).getTitle() + "");
        holder.price.setText("$ " + list.get(position).getCost());
    }

    @Override
    public int getItemCount() {
        return list.size();
    }

    public static class ViewHolder extends RecyclerView.ViewHolder implements View.OnClickListener {
        TextView title, price, bids, comments, details;
        ViewHolder.MyClickListener listener;

        public ViewHolder(@NonNull View itemView) {
            super(itemView);
            title = itemView.findViewById(R.id.task_title);
            price = itemView.findViewById(R.id.task_price);
            bids = itemView.findViewById(R.id.bids);
            comments = itemView.findViewById(R.id.comments);
            details = itemView.findViewById(R.id.details);
            bids.setOnClickListener(this);
            comments.setOnClickListener(this);
            details.setOnClickListener(this);
        }

        @Override
        public void onClick(View v) {
            switch (v.getId()) {
                case R.id.bids:
                    listener.onBid(getLayoutPosition());
                    break;
                case R.id.comments:
                    listener.onComment(getLayoutPosition());
                    break;
                case R.id.details:
                    listener.onDetail(getLayoutPosition());
                    break;
            }
        }

        public interface MyClickListener {
            void onBid(int p);

            void onComment(int p);

            void onDetail(int p);
        }
    }
}
