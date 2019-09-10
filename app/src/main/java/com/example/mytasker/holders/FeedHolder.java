package com.example.mytasker.holders;

import android.graphics.drawable.Drawable;
import android.util.TypedValue;
import android.view.Gravity;
import android.view.View;
import android.widget.ImageView;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.recyclerview.widget.RecyclerView;

import com.bumptech.glide.Glide;
import com.bumptech.glide.load.DataSource;
import com.bumptech.glide.load.engine.GlideException;
import com.bumptech.glide.request.RequestListener;
import com.bumptech.glide.request.RequestOptions;
import com.bumptech.glide.request.target.Target;
import com.example.mytasker.R;
import com.example.mytasker.models.Feed;
import com.like.LikeButton;
import com.like.OnLikeListener;

import static com.example.mytasker.util.Contracts.dpToPx;
import static com.example.mytasker.util.Tools.elapsedTime;
import static com.example.mytasker.util.Tools.formatCount;

public class FeedHolder extends RecyclerView.ViewHolder {
    private TextView title, numLikes, name, date;
    private ImageView avatar, image;
    private LikeButton likeView;

    public FeedHolder(@NonNull View itemView, int type) {
        super(itemView);
        if (type > 0) itemView.findViewById(R.id.likeButton).setVisibility(View.GONE);
        if (type != 2) itemView.findViewById(R.id.action_delete).setVisibility(View.GONE);
        title = itemView.findViewById(R.id.title);
        numLikes = itemView.findViewById(R.id.numLikes);
        likeView = itemView.findViewById(R.id.likeView);
        name = itemView.findViewById(R.id.poster_name);
        date = itemView.findViewById(R.id.createdAt);
        avatar = itemView.findViewById(R.id.poster_image);
        image = itemView.findViewById(R.id.image);
    }

    public void setItem(Feed feed, View.OnClickListener listener, String uid) {
        numLikes.setText(formatCount(feed.getLikeCount()));
        name.setText(feed.getPoster_name());
        date.setText(elapsedTime(feed.getC_date()));
        itemView.findViewById(R.id.action_profile).setOnClickListener(listener);
        itemView.findViewById(R.id.action_delete).setOnClickListener(listener);
        likeView.setLiked(feed.likes.containsKey(uid));
        likeView.setOnLikeListener(new OnLikeListener() {
            @Override
            public void liked(LikeButton likeButton) {
                feed.setLikeCount(feed.getLikeCount() + 1);
                numLikes.setText(formatCount(feed.getLikeCount()));
                listener.onClick(null);
            }

            @Override
            public void unLiked(LikeButton likeButton) {
                feed.setLikeCount(feed.getLikeCount() - 1);
                numLikes.setText(formatCount(feed.getLikeCount()));
                listener.onClick(null);
            }
        });
        itemView.findViewById(R.id.likeButton).setOnClickListener(v -> {
            likeView.onClick(likeView);
        });

        if (feed.getText().equals("")) {
            title.setVisibility(View.GONE);
        } else {
            title.setText(feed.getText());
        }
        if (feed.getImage() != null) {
            Glide.with(image.getContext()).load(feed.getImage())
                    .listener(new RequestListener<Drawable>() {
                        @Override
                        public boolean onLoadFailed(@Nullable GlideException e, Object model, Target<Drawable> target, boolean isFirstResource) {
                            itemView.findViewById(R.id.loading).setVisibility(View.GONE);
                            return false;
                        }

                        @Override
                        public boolean onResourceReady(Drawable resource, Object model, Target<Drawable> target, DataSource dataSource, boolean isFirstResource) {
                            itemView.findViewById(R.id.loading).setVisibility(View.GONE);
                            return false;
                        }
                    }).into(image);
        } else {
            image.setVisibility(View.GONE);
            itemView.findViewById(R.id.loading).setVisibility(View.GONE);
            title.setTextSize(TypedValue.COMPLEX_UNIT_SP, 32);
            title.setGravity(Gravity.CENTER);
            int d = dpToPx(24);
            title.setPadding(d, d, d, d);
        }
        Glide.with(avatar.getContext())
                .load(feed.getPoster_avatar())
                .apply(RequestOptions.circleCropTransform())
                .into(avatar);
    }
}