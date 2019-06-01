package com.example.mytasker.adapters;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import com.example.mytasker.R;
import com.example.mytasker.models.IndividualTask;

public class TaskListAdapter extends RecyclerView.Adapter<TaskListAdapter.holder> {

    private IndividualTask[] list;
    private View mView;
    private RecyclerViewClickListener mListener;

    public interface RecyclerViewClickListener {

        void onClick(View view, int position);
    }

    public TaskListAdapter(Context context, IndividualTask[] list)
    {
        this.list = list;
        this.mListener = (RecyclerViewClickListener) context;
    }
    @NonNull
    @Override
    public holder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(parent.getContext()).inflate(R.layout.card_task,parent,false);
        mView = view;
        return new holder(view);
    }

    @Override
    public void onBindViewHolder(@NonNull holder holder, int position) {

        String desciption = list[position].getTitle();
        holder.setupDescription(desciption);
        String distance = list[position].getDis();
        holder.setupDistance(distance);
        String date = list[position].getC_date();
        holder.setupDate(date);
        String price = list[position].getCost();
        holder.setupPrice(price);
        String location = list[position].getJob_des();
        holder.setupLocation(location);

        int imageid = R.drawable.google;
        holder.setupimage(imageid);

        setAnimation(holder.itemView, position);
    }

    @Override
    public int getItemCount() {
        return list.length;
    }

    public class holder extends RecyclerView.ViewHolder implements View.OnClickListener
    {

        public holder(@NonNull View itemView) {
            super(itemView);
            itemView.setOnClickListener(this);
        }

        public void setupDescription(String string)
        {
            TextView textView = mView.findViewById(R.id.task_desc);
            textView.setText(string);
        }
        public void setupLocation(String string)
        {
            TextView textView = mView.findViewById(R.id.task_dist);
            textView.setText(string);
        }
        public void setupDistance(String string)
        {
            TextView textView = mView.findViewById(R.id.task_dist);
            textView.setText(string);
        }
        public void setupPrice(String string)
        {
            TextView textView = mView.findViewById(R.id.task_price);
            textView.setText(string);
        }
        public void setupDate(String string)
        {
            TextView textView = mView.findViewById(R.id.task_time);
            textView.setText(string);
        }
        public void setupimage(int image)
        {
            ImageView imageView = mView.findViewById(R.id.task_cat_image);
            imageView.setImageResource(image);
        }

        @Override
        public void onClick(View v) {
            mListener.onClick(v, getAdapterPosition());
        }
    }

    private int lastPosition = -1;
    private boolean on_attach = true;

    private void setAnimation(View viewToAnimate, int position)
    {
//        TODO
// If the bound view wasn't previously displayed on screen, it's animated
//        if (position > lastPosition)
//        {
//            ItemAnimation.animate(viewToAnimate, on_attach ? position : -1, 4);
//            lastPosition = position;
//        }
    }


    @Override
    public void onViewDetachedFromWindow(@NonNull holder holder) {
        super.onViewDetachedFromWindow(holder);
        holder.itemView.clearAnimation();
    }

    @Override
    public void onAttachedToRecyclerView(RecyclerView recyclerView) {
        recyclerView.addOnScrollListener(new RecyclerView.OnScrollListener() {
            @Override
            public void onScrollStateChanged(RecyclerView recyclerView, int newState) {
                on_attach = false;
                super.onScrollStateChanged(recyclerView, newState);
            }
        });
        super.onAttachedToRecyclerView(recyclerView);
    }
}


