package com.example.mytasker.models;

import android.util.TypedValue;
import android.view.Gravity;
import android.view.View;
import android.widget.ImageView;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import com.example.mytasker.R;
import com.squareup.picasso.Callback;
import com.squareup.picasso.Picasso;

import static com.example.mytasker.util.Tools.elapsedTime;

public class FeedViewHolder extends RecyclerView.ViewHolder {
    private TextView title,likes,name,date;
    private ImageView avatar,image;
    private View loading;

    public FeedViewHolder(@NonNull View itemView) {
        super(itemView);
        title = itemView.findViewById(R.id.title);
        likes = itemView.findViewById(R.id.numLikes);
        name = itemView.findViewById(R.id.name);
        date = itemView.findViewById(R.id.createdAt);
        avatar = itemView.findViewById(R.id.avatar);
        image = itemView.findViewById(R.id.image);
        loading = itemView.findViewById(R.id.loading);
    }

    public void setItem(Feed feed){
        title.setText(feed.getText());
        likes.setText(feed.getLikes()+"");
        name.setText(feed.getPoster_name());
        date.setText(elapsedTime(feed.getC_date()));
//        Log.e("Image",feed.getImage());
//        Log.e("Avatar",feed.getPoster_avatar());
        if (feed.getImage() != null && !feed.getImage().isEmpty()) {
            Picasso.with(itemView.getContext()).load(feed.getImage()).fit()
                    .error(R.drawable.feed_image).into(image, new Callback() {
                @Override
                public void onSuccess() {
                    loading.setVisibility(View.GONE);
                }

                @Override
                public void onError() {
                    loading.setVisibility(View.GONE);
                }
            });
        } else {
            title.setGravity(Gravity.CENTER);
            title.setTextSize(TypedValue.COMPLEX_UNIT_SP, 30);
            image.setVisibility(View.GONE);
            loading.setVisibility(View.GONE);
        }
        if(feed.getPoster_avatar()!=null)
            Picasso.with(itemView.getContext()).load(feed.getPoster_avatar()).placeholder(R.drawable.person)
                    .error(R.drawable.person).into(avatar);
    }
}