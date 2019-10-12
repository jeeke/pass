package com.esselion.pass.util;

import android.content.Intent;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.paging.PagedList;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;
import androidx.swiperefreshlayout.widget.SwipeRefreshLayout;

import com.airbnb.lottie.LottieAnimationView;
import com.esselion.pass.R;
import com.esselion.pass.activities.ProfileActivity;
import com.esselion.pass.holders.FeedHolder;
import com.esselion.pass.models.Feed;
import com.facebook.shimmer.ShimmerFrameLayout;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.Query;
import com.shreyaspatil.firebase.recyclerpagination.DatabasePagingOptions;
import com.shreyaspatil.firebase.recyclerpagination.FirebaseRecyclerPagingAdapter;
import com.shreyaspatil.firebase.recyclerpagination.LoadingState;

import java.util.HashMap;
import java.util.Map;

import static com.esselion.pass.util.Cache.getDatabase;
import static com.esselion.pass.util.Tools.showSnackBar;

public class FeedNPortAdapter {

    public FirebaseRecyclerPagingAdapter<Feed, FeedHolder> mAdapter;

    public FeedNPortAdapter() {
    }

    public void callFireBase(String uid, boolean mine, AppCompatActivity context, ShimmerFrameLayout shimmerContainer,
                             boolean fromPortfolio, SwipeRefreshLayout mSwipeRefreshLayout,
                             RecyclerView mRecyclerView) {
//        uid = getUser(context).getUid();
        Query mQuery;
        mSwipeRefreshLayout.setColorSchemeResources(

                android.R.color.holo_blue_bright,

                android.R.color.holo_green_light);

        //Initialize RecyclerView
        mRecyclerView.setHasFixedSize(true);
        if (fromPortfolio) {
            Tools.initMinToolbar(context, "Portfolio");
            mQuery = getDatabase().child("Portfolios").child(uid);
        } else {
            Tools.initMinToolbar(context, "My Posts");
            mQuery = getDatabase().child("PrevFeeds").child(uid);
        }

        LinearLayoutManager mManager = new LinearLayoutManager(context);
        mRecyclerView.setLayoutManager(mManager);
        //Initialize PagedList Configuration
        PagedList.Config config = new PagedList.Config.Builder()
                .setEnablePlaceholders(false)
                .setPrefetchDistance(5)
                .setPageSize(10)
                .build();

        //Initialize FirebasePagingOptions
        DatabasePagingOptions<Feed> options = new DatabasePagingOptions.Builder<Feed>()
                .setLifecycleOwner(context)
                .setQuery(mQuery, config, Feed.class)
                .build();

        //Initialize Adapter
        mAdapter = new FirebaseRecyclerPagingAdapter<Feed, FeedHolder>(options) {


            private int count = 4;

            @NonNull
            @Override
            public FeedHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
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
                        context.startActivity(intent);
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
                                mAdapter.refresh();
                            } else showSnackBar(context, "Could not be deleted");
                        });
                    }
                }, uid, mine);
            }

            @Override
            protected void onLoadingStateChanged(@NonNull LoadingState state) {
                LottieAnimationView emptyAnim = context.findViewById(R.id.lottie_anim);
                TextView emptyText = context.findViewById(R.id.empty_text);
                switch (state) {
                    case LOADING_INITIAL:
                        if (fromPortfolio) {
                            context.findViewById(R.id.anim).setVisibility(View.GONE);
                        }
                        mRecyclerView.animate().alpha(0.0f).setDuration(0).start();
                        shimmerContainer.animate().alpha(1.0f).setDuration(0).start();
                        shimmerContainer.startShimmer();
                        break;
                    case LOADING_MORE:
                        // Do your loadingPng animation
                        mSwipeRefreshLayout.setRefreshing(true);
                        break;

                    case LOADED:
                        // Stop Animation
                        shimmerContainer.stopShimmer();
                        shimmerContainer.animate().alpha(0.0f).setDuration(0).start();
                        mRecyclerView.animate().alpha(1.0f).setDuration(100).start();
                        mSwipeRefreshLayout.setRefreshing(false);
                        break;

                    case FINISHED:
                        //Reached end of Data set
                        mSwipeRefreshLayout.setRefreshing(false);
                        break;

                    case ERROR:
                        if (--count > 0) {
                            retry();
                        } else {
                            if (fromPortfolio) {
                                emptyAnim.setAnimation(R.raw.empty_port);
                                if (mine) emptyText.setText("Add items to your portfolio");
                                else emptyText.setText("Empty Portfolio");
                                context.findViewById(R.id.anim).setVisibility(View.VISIBLE);
                            }
                            mSwipeRefreshLayout.setRefreshing(false);
                            shimmerContainer.stopShimmer();
                            shimmerContainer.animate().alpha(0.0f).setDuration(0).start();
                            mRecyclerView.animate().alpha(1.0f).setDuration(100).start();
                        }
                }
            }

            @Override
            protected void onError(@NonNull DatabaseError databaseError) {
                super.onError(databaseError);
                mSwipeRefreshLayout.setRefreshing(false);
                databaseError.toException().printStackTrace();
            }
        };
        //Set Adapter to RecyclerView
        mRecyclerView.setAdapter(mAdapter);
        mSwipeRefreshLayout.setOnRefreshListener(mAdapter::refresh);
    }
}
