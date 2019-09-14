package com.example.mytasker.holders;

import android.view.View;
import android.widget.ImageView;
import android.widget.TextView;

import androidx.recyclerview.widget.RecyclerView;

import com.bumptech.glide.Glide;
import com.bumptech.glide.request.RequestOptions;
import com.example.mytasker.R;
import com.example.mytasker.models.Notification;

public class NotificationHolder extends RecyclerView.ViewHolder {
    private TextView title, content;
    private ImageView image;

    public NotificationHolder(View itemView) {
        super(itemView);
        title = itemView.findViewById(R.id.title);
        content = itemView.findViewById(R.id.content);
        image = itemView.findViewById(R.id.image);
    }

    public void setItem(Notification current) {
        title.setText(current.fetchTitle());
        content.setText(current.content);
        Glide.with(image.getContext()).load(current.image).apply(RequestOptions.circleCropTransform()).into(image);
    }
}


