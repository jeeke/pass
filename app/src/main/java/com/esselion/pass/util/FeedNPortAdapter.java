package com.esselion.pass.util;

import android.content.Intent;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.airbnb.lottie.LottieAnimationView;
import com.esselion.pass.R;
import com.esselion.pass.activities.ProfileActivity;
import com.esselion.pass.holders.FeedHolder;
import com.esselion.pass.models.Feed;
import com.firebase.ui.database.FirebaseRecyclerAdapter;
import com.firebase.ui.database.FirebaseRecyclerOptions;
import com.google.firebase.database.Query;

import java.util.HashMap;
import java.util.Map;

import static com.esselion.pass.util.Cache.getDatabase;
import static com.esselion.pass.util.Tools.launchActivity;
import static com.esselion.pass.util.Tools.showSnackBar;

public class FeedNPortAdapter {

    public FirebaseRecyclerAdapter<Feed, FeedHolder> mAdapter;

    public FeedNPortAdapter() {
    }

    public void callFireBase(String uid, boolean mine, AppCompatActivity context,
                             boolean fromPortfolio,
                             RecyclerView mRecyclerView) {
//        uid = getUser(context).getUid();
        Query mQuery;
        //Initialize RecyclerView
        mRecyclerView.setHasFixedSize(true);
        LottieAnimationView animationView = context.findViewById(R.id.lottie_anim);
        TextView textView = context.findViewById(R.id.empty_text);
        if (fromPortfolio) {
            animationView.setAnimation(R.raw.empty_port);
            if (mine) textView.setText("Add items to your portfolio");
            else textView.setText("Empty Portfolio");
            Tools.initMinToolbar(context, "Portfolio");
            mQuery = getDatabase().child("Portfolios").child(uid);
        } else {
            animationView.setAnimation(R.raw.mascot);
            textView.setText("No Posts Yet");
            Tools.initMinToolbar(context, "My Posts");
            mQuery = getDatabase().child("PrevFeeds").child(uid);
        }
        LinearLayoutManager mManager = new LinearLayoutManager(context);
        mRecyclerView.setLayoutManager(mManager);

        FirebaseRecyclerOptions<Feed> options =
                new FirebaseRecyclerOptions.Builder<Feed>()
                        .setQuery(mQuery, Feed.class).build();
        View anim = context.findViewById(R.id.anim);
        mAdapter = new FirebaseRecyclerAdapter<Feed, FeedHolder>(options) {
            @NonNull
            @Override
            public FeedHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
                anim.setVisibility(View.GONE);
                return new FeedHolder(LayoutInflater.from(parent.getContext()).inflate(R.layout.card_feed, parent, false), 1);
            }

            @Override
            protected void onBindViewHolder(@NonNull FeedHolder holder,
                                            int position,
                                            @NonNull Feed model) {
                holder.setItem(model, v -> {
                    if (v.getId() == R.id.action_profile) {
                        Intent intent = new Intent(context, ProfileActivity.class);
                        intent.putExtra("id", model.getPoster_id());
                        intent.putExtra("name", model.getPoster_name());
                        intent.putExtra("avatar", model.getPoster_avatar());
                        launchActivity(context, intent);
                    } else if (v.getId() == R.id.action_delete) {
                        Map<String, Object> map = new HashMap<>();
                        if (fromPortfolio) {
                            map.put("Portfolios/" + uid + '/' + model.getId(), null);
                        } else {
                            map.put("Feeds/" + model.getId(), null);
                            map.put("PrevFeeds/" + uid + '/' + model.getId(), null);
                        }
                        getDatabase().updateChildren(map).addOnCompleteListener(task -> {
                            if (task.isSuccessful()) {
                                showSnackBar(context, "Deleted Successfully");
                            } else showSnackBar(context, "Could not be deleted");
                        });
                    }
                }, uid, mine);
            }
        };
        mRecyclerView.setAdapter(mAdapter);
    }
}
