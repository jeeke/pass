package com.esselion.pass.fragments;

import android.content.Intent;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.MenuItem;
import android.view.View;
import android.view.ViewGroup;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.widget.Toolbar;
import androidx.fragment.app.Fragment;
import androidx.paging.PagedList;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;
import androidx.swiperefreshlayout.widget.SwipeRefreshLayout;

import com.esselion.pass.R;
import com.esselion.pass.activities.HistoryFeed;
import com.esselion.pass.activities.ProfileActivity;
import com.esselion.pass.holders.FeedHolder;
import com.esselion.pass.models.Feed;
import com.facebook.shimmer.ShimmerFrameLayout;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.Query;
import com.shreyaspatil.firebase.recyclerpagination.DatabasePagingOptions;
import com.shreyaspatil.firebase.recyclerpagination.FirebaseRecyclerPagingAdapter;
import com.shreyaspatil.firebase.recyclerpagination.LoadingState;

import static com.esselion.pass.holders.FeedHolder.onlikeClicked;
import static com.esselion.pass.util.Cache.getDatabase;
import static com.esselion.pass.util.Cache.getUser;
import static com.esselion.pass.util.Tools.launchActivity;

public class FeedFragment extends Fragment {
    public FeedFragment() {
    }

    public FirebaseRecyclerPagingAdapter<Feed, FeedHolder> mAdapter;

    private void initToolbar(View v) {
        Toolbar toolbar = v.findViewById(R.id.toolbar);
        toolbar.setTitle("Events");
        toolbar.setTitleTextColor(getContext().getResources().getColor(R.color.blue_grey));
        ((AppCompatActivity) getActivity()).setSupportActionBar(toolbar);
//        Tools.setSystemBarColor(getActivity(), R.color.green_800);
    }

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setHasOptionsMenu(true);
    }

    @Override
    public void onCreateOptionsMenu(Menu menu, MenuInflater inflater) {
        inflater.inflate(R.menu.menu_history, menu);
        super.onCreateOptionsMenu(menu, inflater);
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        if (item.getItemId() == R.id.action_history) {
            Intent intent = new Intent(getContext(), HistoryFeed.class);
            intent.putExtra("uid", getUser(getActivity()).getUid());
            intent.putExtra("mine", true);
            launchActivity(getActivity(), intent);
            return true;
        }
        return false;
    }

    private RecyclerView mRecyclerView;
    private SwipeRefreshLayout mSwipeRefreshLayout;

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        View v = inflater.inflate(R.layout.fragment_feed, container, false);
        initToolbar(v);
        initViews(v);
        initRecyclerAdapter(v);
//        mRecyclerView.setHasFixedSize(true);
        return v;
    }


    private void initRecyclerAdapter(View v) {

        String uid = getUser(getActivity()).getUid();
        Query mQuery = getDatabase().child("Feeds");
        ShimmerFrameLayout shimmerContainer = v.findViewById(R.id.shimmer_container);
        mSwipeRefreshLayout.setColorSchemeResources(

                android.R.color.holo_blue_bright,

                android.R.color.holo_green_light);

        //Initialize RecyclerView

        LinearLayoutManager mManager = new LinearLayoutManager(getContext());
        mRecyclerView.setLayoutManager(mManager);
        //Initialize PagedList Configuration
        PagedList.Config config = new PagedList.Config.Builder()
                .setEnablePlaceholders(false)
                .setPrefetchDistance(5)
                .setPageSize(10)
                .build();

        //Initialize FirebasePagingOptions
        DatabasePagingOptions<Feed> options = new DatabasePagingOptions.Builder<Feed>()
                .setLifecycleOwner(getActivity())
                .setQuery(mQuery, config, Feed.class)
                .build();

        //Initialize Adapter
        mAdapter = new FirebaseRecyclerPagingAdapter<Feed, FeedHolder>(options) {


            private int count = 4;

            @NonNull
            @Override
            public FeedHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
                return new FeedHolder(LayoutInflater.from(parent.getContext()).inflate(R.layout.card_feed, parent, false), 0);
            }

            @Override
            protected void onBindViewHolder(@NonNull FeedHolder holder,
                                            int position,
                                            @NonNull Feed model) {
                holder.setItem(model, v -> {
                    if (v.getId() == R.id.action_profile) {
                        Intent intent = new Intent(getContext(), ProfileActivity.class);
                        intent.putExtra("id", model.getPoster_id());
                        intent.putExtra("name", model.getPoster_name());
                        intent.putExtra("avatar", model.getPoster_avatar());
                        launchActivity(getActivity(), intent);
                    } else if (v.getId() == R.id.likeButton) {
                        final DatabaseReference postRef = getRef(position);
                        DatabaseReference globalFeedRef = getDatabase().child("Feeds").child(postRef.getKey());
                        DatabaseReference userFeedRef = getDatabase().child("PrevFeeds").child(model.getPoster_id()).child(postRef.getKey());
                        // Run two transactions
                        onlikeClicked(globalFeedRef, uid);
                        onlikeClicked(userFeedRef, uid);
                    }
                }, uid, false);
            }

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

    //Start Listening Adapter
    @Override
    public void onStart() {
        super.onStart();
        mAdapter.startListening();
    }

    //Stop Listening Adapter
    @Override
    public void onStop() {
        super.onStop();
        mAdapter.stopListening();
    }

    private void initViews(View v) {
        mRecyclerView = v.findViewById(R.id.list);
        mSwipeRefreshLayout = v.findViewById(R.id.swipe_refresh_layout);
    }
}
