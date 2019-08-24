package com.example.mytasker.models;

import android.view.View;
import android.widget.ImageView;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import com.example.mytasker.R;
import com.squareup.picasso.Callback;
import com.squareup.picasso.Picasso;

import java.util.Date;

public class FeedHolder extends RecyclerView.ViewHolder {
    private TextView title,likes,name,date;
    private ImageView avatar,image;

    public FeedHolder(@NonNull View itemView, boolean type) {
        super(itemView);
        if (type) itemView.findViewById(R.id.like).setVisibility(View.GONE);
        title = itemView.findViewById(R.id.title);
        likes = itemView.findViewById(R.id.numLikes);
        name = itemView.findViewById(R.id.name);
        date = itemView.findViewById(R.id.createdAt);
        avatar = itemView.findViewById(R.id.avatar);
        image = itemView.findViewById(R.id.image);
    }

    public void setItem(Feed feed){
        title.setText(feed.getText());
        likes.setText(feed.getLikes()+"");
        name.setText(feed.getPoster_name());
        date.setText(new Date(feed.getC_date()).toString());
        if (feed.getImage() != null) {
            Picasso.with(image.getContext()).load(feed.getImage()).fit().error(R.drawable.feed_image).into(image, new Callback() {
                @Override
                public void onSuccess() {
                    itemView.findViewById(R.id.loading).setVisibility(View.GONE);
                }

                @Override
                public void onError() {
                    itemView.findViewById(R.id.loading).setVisibility(View.GONE);
                }
            });
        }
        if(feed.getPoster_avatar()!=null)
            Picasso.with(avatar.getContext()).load(feed.getPoster_avatar()).into(avatar);
    }
}