package com.example.mytasker.adapters;

import android.content.Context;
import android.graphics.drawable.Drawable;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import com.bumptech.glide.Glide;
import com.bumptech.glide.request.RequestOptions;
import com.example.mytasker.R;

import java.util.ArrayList;
import java.util.Arrays;

import static com.example.mytasker.util.Contracts.avatars;

public class AvatarAdapter extends RecyclerView.Adapter<AvatarAdapter.holder> {

    private ArrayList<String> list;
    private RecyclerViewClickListener mListener;
    private Drawable drawable;

    public AvatarAdapter(Context context) {
        this.list = new ArrayList<>(Arrays.asList(avatars));
        this.mListener = (RecyclerViewClickListener) context;
    }

    @NonNull
    @Override
    public holder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(parent.getContext()).inflate(R.layout.card_avatar, parent, false);
        return new holder(view);
    }

    public void setPickedDrawable(Drawable drawable) {
        this.drawable = drawable;
    }

    @Override
    public void onBindViewHolder(@NonNull holder holder, int position) {
        ImageView image = (ImageView) holder.itemView;
        if (position == 0) {
            if (drawable == null) image.setImageResource(R.drawable.ic_gallery);
            else image.setImageDrawable(drawable);
        } else Glide.with(image.getContext())
                .load(list.get(position - 1))
                .apply(RequestOptions.circleCropTransform())
                .into(image);
    }

    @Override
    public int getItemCount() {
        return list.size() + 1;
    }

    public interface RecyclerViewClickListener {
        void onClick(ImageView view, int position, boolean selected);
    }

    public class holder extends RecyclerView.ViewHolder implements View.OnClickListener {
        View itemView;
        boolean selected = false;

        holder(@NonNull View itemView) {
            super(itemView);
            this.itemView = itemView;
            itemView.setOnClickListener(this);
        }

        @Override
        public void onClick(View v) {
            selected = !selected;
            mListener.onClick((ImageView) v, getAdapterPosition(), selected);
        }
    }
}


