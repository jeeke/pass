package com.esselion.pass.holders;

import android.graphics.drawable.Drawable;
import android.util.Log;
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
import com.esselion.pass.R;
import com.esselion.pass.models.Feed;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.MutableData;
import com.google.firebase.database.Transaction;
import com.like.LikeButton;
import com.like.OnLikeListener;

import static com.esselion.pass.util.Contracts.dpToPx;
import static com.esselion.pass.util.Tools.elapsedTime;
import static com.esselion.pass.util.Tools.formatCount;

public class FeedHolder extends RecyclerView.ViewHolder {
    private TextView title, numLikes, name, date;
    private ImageView avatar, image;
    private LikeButton likeView;

    public FeedHolder(@NonNull View itemView, int type) {
        super(itemView);
        if (type > 0) itemView.findViewById(R.id.likeButton).setVisibility(View.GONE);
        else itemView.findViewById(R.id.action_delete).setVisibility(View.GONE);
        title = itemView.findViewById(R.id.title);
        numLikes = itemView.findViewById(R.id.numLikes);
        likeView = itemView.findViewById(R.id.likeView);
        name = itemView.findViewById(R.id.poster_name);
        date = itemView.findViewById(R.id.createdAt);
        avatar = itemView.findViewById(R.id.poster_image);
        image = itemView.findViewById(R.id.image);
    }

    public static void onlikeClicked(DatabaseReference FeedRef, String uid) {
        FeedRef.runTransaction(new Transaction.Handler() {
            @Override
            public Transaction.Result doTransaction(MutableData mutableData) {
                Feed p = mutableData.getValue(Feed.class);
                if (p == null) {
                    return Transaction.success(mutableData);
                }
                if (p.likes.containsKey(uid)) {
                    // Unlike the Feed and remove self from likes
                    p.likeCount = p.likeCount - 1;
                    p.likes.remove(uid);
                } else {
                    // like the Feed and add self to likes
                    p.likeCount = p.likeCount + 1;
                    p.likes.put(uid, true);
                }
                // Set value and report transaction success
                mutableData.setValue(p);
                return Transaction.success(mutableData);
            }

            @Override
            public void onComplete(DatabaseError databaseError, boolean b,
                                   DataSnapshot dataSnapshot) {
                // Transaction completed
                Log.d("FeedActNFragment", "FeedTransaction:onComplete:" + databaseError);
            }
        });
    }

    public void setItem(Feed feed, View.OnClickListener listener, String uid, boolean mine) {
        if (!mine) itemView.findViewById(R.id.action_delete).setVisibility(View.GONE);
        if (feed.getText().equals("")) {
            title.setVisibility(View.GONE);
        } else {
            title.setVisibility(View.VISIBLE);
            title.setText(feed.getText());
        }
        if (feed.getImage() != null) {
            image.setVisibility(View.VISIBLE);
            itemView.findViewById(R.id.loading).setVisibility(View.VISIBLE);
            title.setTextSize(TypedValue.COMPLEX_UNIT_SP, 14);
            title.setPadding(0, 0, 0, 0);
            title.setGravity(Gravity.START);
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
                listener.onClick(likeButton);
            }

            @Override
            public void unLiked(LikeButton likeButton) {
                feed.setLikeCount(feed.getLikeCount() - 1);
                numLikes.setText(formatCount(feed.getLikeCount()));
                listener.onClick(likeButton);
            }
        });
        itemView.findViewById(R.id.likeButton).setOnClickListener(v -> likeView.onClick(likeView));
    }
}