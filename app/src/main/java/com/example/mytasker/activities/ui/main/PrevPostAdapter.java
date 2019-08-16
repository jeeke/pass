package com.example.mytasker.activities.ui.main;

import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import com.example.mytasker.R;
import com.example.mytasker.models.PrevPostModel;
import com.example.mytasker.models.Task;

import java.util.ArrayList;

public class PrevPostAdapter extends RecyclerView.Adapter<PrevPostAdapter.ViewHolder> {

    int indexTop,indexBot;
    PrevPostModel model;
    int id;
    ArrayList<Task> list;

    public PrevPostAdapter(PrevPostModel list, int indexTop, int indexBot) {
        this.model = list;
        this.indexTop = indexTop;
        this.indexBot = indexBot;
        getId();
    }

    @NonNull
    @Override
    public ViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        LayoutInflater inflater = LayoutInflater.from(parent.getContext());
        View item = inflater.inflate(id, parent, false);
        return new ViewHolder(item);
    }

    private void getId() {
        if(indexTop==2){
            if(indexBot==2){
               id = R.layout.card_task_done;
               list = model.getSaved();
            }else if(indexBot==1){
                id =  R.layout.card_task_done;
                list = model.getDone();
            }else {
                id = R.layout.card_task_posted;
                list = model.getPosted();
            }
        }else if(indexTop==1){
            if(indexBot==2){

            }else if(indexBot==1){

            }else {

            }
        }else {
            if(indexBot==2){
                id = R.layout.card_task_done;
                list = model.getSaved();
            }else if(indexBot==1){
                id =  R.layout.card_task_done;
                list = model.getDone();
            }else {
                id = R.layout.card_task_posted;
                list = model.getPosted();
            }
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
            if(bids!=null)
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
