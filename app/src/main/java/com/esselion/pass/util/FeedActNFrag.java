package com.esselion.pass.util;

import android.content.Intent;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.fragment.app.FragmentActivity;
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
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.MutableData;
import com.google.firebase.database.Query;
import com.google.firebase.database.Transaction;
import com.shreyaspatil.firebase.recyclerpagination.DatabasePagingOptions;
import com.shreyaspatil.firebase.recyclerpagination.FirebaseRecyclerPagingAdapter;
import com.shreyaspatil.firebase.recyclerpagination.LoadingState;

import java.util.HashMap;
import java.util.Map;

import static com.esselion.pass.util.Cache.getDatabase;
import static com.esselion.pass.util.Tools.showSnackBar;

public class FeedActNFrag {

    public FirebaseRecyclerPagingAdapter<Feed, FeedHolder> mAdapter;

    public FeedActNFrag() {
    }

    public void callFireBase(String uid, boolean mine, FragmentActivity context, ShimmerFrameLayout shimmerContainer,
                             boolean fromPortfolio, SwipeRefreshLayout mSwipeRefreshLayout,
                             RecyclerView mRecyclerView, int type, Query mQuery) {
//        uid = getUser(context).getUid();
        mSwipeRefreshLayout.setColorSchemeResources(

                android.R.color.holo_blue_bright,

                android.R.color.holo_green_light);

        //Initialize RecyclerView
        mRecyclerView.setHasFixedSize(true);
        if (mQuery == null)
            if (fromPortfolio) {
                Tools.initMinToolbar((AppCompatActivity) context, "Portfolio");
                mQuery = getDatabase().child("Portfolios").child(uid);
            } else {
                Tools.initMinToolbar((AppCompatActivity) context, "My Posts");
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


            @NonNull
            @Override
            public FeedHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
                return new FeedHolder(LayoutInflater.from(parent.getContext()).inflate(R.layout.card_feed, parent, false), type);
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
                        map.put("Portfolios/" + uid + '/' + model.getId(), null);
                        if (!fromPortfolio) {
                            map.put("Feeds/" + model.getId(), null);
                            map.put("PrevFeeds/" + uid + '/' + model.getId(), null);
                        }
                        getDatabase().updateChildren(map).addOnCompleteListener(task -> {
                            if (task.isSuccessful()) {
                                showSnackBar(context, "Deleted Successfully");
                            } else showSnackBar(context, "Could not be deleted");
                        });
                    } else {
                        final DatabaseReference postRef = getRef(position);
                        DatabaseReference globalFeedRef = getDatabase().child("Feeds").child(postRef.getKey());
                        DatabaseReference userFeedRef = getDatabase().child("PrevFeeds").child(model.getPoster_id()).child(postRef.getKey());
                        // Run two transactions
                        onlikeClicked(globalFeedRef, uid);
                        onlikeClicked(userFeedRef, uid);
                    }
                }, uid, mine);
            }

            private int count = 4;

            @Override
            protected void onLoadingStateChanged(@NonNull LoadingState state) {
                switch (state) {
                    case LOADING_INITIAL:
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
                                LottieAnimationView emptyAnim = context.findViewById(R.id.lottie_anim);
                                emptyAnim.setAnimation(R.raw.empty_port);
                                TextView emptyText = context.findViewById(R.id.empty_text);
                                if (mine) emptyText.setText("Add items to your portfolio");
                                else emptyText.setText("Empty portfolio");
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

    private void onlikeClicked(DatabaseReference FeedRef, String uid) {
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
}
