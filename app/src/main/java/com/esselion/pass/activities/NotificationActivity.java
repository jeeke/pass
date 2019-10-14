package com.esselion.pass.activities;

import android.os.Bundle;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.paging.PagedList;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;
import androidx.swiperefreshlayout.widget.SwipeRefreshLayout;

import com.airbnb.lottie.LottieAnimationView;
import com.esselion.pass.R;
import com.esselion.pass.holders.NotificationHolder;
import com.esselion.pass.models.Notification;
import com.esselion.pass.util.Contracts;
import com.esselion.pass.util.Tools;
import com.facebook.shimmer.ShimmerFrameLayout;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.Query;
import com.shreyaspatil.firebase.recyclerpagination.DatabasePagingOptions;
import com.shreyaspatil.firebase.recyclerpagination.FirebaseRecyclerPagingAdapter;
import com.shreyaspatil.firebase.recyclerpagination.LoadingState;

import static com.esselion.pass.util.Cache.getDatabase;
import static com.esselion.pass.util.Cache.getUser;

public class NotificationActivity extends BaseActivity {


    private FirebaseRecyclerPagingAdapter mAdapter;
    private RecyclerView mRecyclerView;
    private SwipeRefreshLayout mSwipeRefreshLayout;
    private ShimmerFrameLayout shimmerContainer;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_list);
        Tools.initMinToolbar(this, "Notifications");
        mSwipeRefreshLayout = findViewById(R.id.swipe_refresh_layout);
        mRecyclerView = findViewById(R.id.recyclerView);
        shimmerContainer = findViewById(R.id.shimmer_container);
        TextView textView = findViewById(R.id.empty_text);
        textView.setText("No Notifications Yet");
        callFireBase();
    }

    private void callFireBase() {
        mSwipeRefreshLayout.setColorSchemeResources(

                android.R.color.holo_blue_bright,

                android.R.color.holo_green_light);

        //Initialize RecyclerView
        mRecyclerView.setHasFixedSize(true);

        LinearLayoutManager mManager = new LinearLayoutManager(this);
        mRecyclerView.setLayoutManager(mManager);

        //Initialize Database
        //TODO remodel table to increase efficiency
        Query mQuery = getDatabase().child("/Notifications").child(getUser().getUid());
        //Initialize PagedList Configuration
        PagedList.Config config = new PagedList.Config.Builder()
                .setEnablePlaceholders(false)
                .setPrefetchDistance(5)
                .setPageSize(10)
                .build();
        //Initialize FirebasePagingOptions
        DatabasePagingOptions<Notification> options = new DatabasePagingOptions.Builder<Notification>()
                .setLifecycleOwner(this)
                .setQuery(mQuery, config, Notification.class)
                .build();
        //Initialize Adapter
        mAdapter = new FirebaseRecyclerPagingAdapter<Notification, NotificationHolder>(options) {

            private int RETRY_COUNT = Contracts.RETRY_COUNT;

            @NonNull
            @Override
            public NotificationHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
                return new NotificationHolder(LayoutInflater.from(parent.getContext()).inflate(R.layout.card_notification, parent, false));
            }

            @Override
            protected void onBindViewHolder(@NonNull NotificationHolder holder,
                                            int position,
                                            @NonNull Notification model) {
                holder.setItem(model);
            }

            @Override
            protected void onLoadingStateChanged(@NonNull LoadingState state) {
                switch (state) {
                    case LOADING_INITIAL:
                        mRecyclerView.animate().alpha(0.0f).start();
                        shimmerContainer.animate().alpha(1.0f).start();
                        shimmerContainer.startShimmer();
                        break;
                    case LOADING_MORE:
                        // Do your loading animation
                        mSwipeRefreshLayout.setRefreshing(true);
                        break;

                    case LOADED:
                        // Stop Animation
                        shimmerContainer.stopShimmer();
                        shimmerContainer.animate().alpha(0.0f).setDuration(200).start();
                        mRecyclerView.animate().alpha(1.0f).setDuration(200).start();
                        mSwipeRefreshLayout.setRefreshing(false);
                        mSwipeRefreshLayout.setRefreshing(false);
                        break;

                    case FINISHED:
                        //Reached end of Data set
                        mSwipeRefreshLayout.setRefreshing(false);
                        break;

                    case ERROR:
                        if (--RETRY_COUNT > 0)
                            retry();
                        else {
                            LottieAnimationView anim = findViewById(R.id.lottie_anim);
                            anim.setAnimation(R.raw.bell);
                            anim.setSpeed(2.0f);
                            findViewById(R.id.anim).setVisibility(View.VISIBLE);
                            shimmerContainer.stopShimmer();
                            shimmerContainer.animate().alpha(0.0f).setDuration(200).start();
                            mRecyclerView.animate().alpha(1.0f).setDuration(200).start();
                        }
                }
            }

            @Override
            protected void onError(@NonNull DatabaseError databaseError) {
                super.onError(databaseError);
                mSwipeRefreshLayout.setRefreshing(false);
                Log.e("Error", databaseError.toString());
                databaseError.toException().printStackTrace();
            }
        };

        //Set Adapter to RecyclerView
        mRecyclerView.setAdapter(mAdapter);

        //Set listener to SwipeRefreshLayout for refresh action
        mSwipeRefreshLayout.setOnRefreshListener(() -> mAdapter.refresh());
    }

}
