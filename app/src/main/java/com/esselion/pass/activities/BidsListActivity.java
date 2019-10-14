package com.esselion.pass.activities;

import android.content.Intent;
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
import com.esselion.pass.holders.BidHolder;
import com.esselion.pass.models.Bid;
import com.esselion.pass.models.Task;
import com.esselion.pass.util.Contracts;
import com.esselion.pass.util.Tools;
import com.facebook.shimmer.ShimmerFrameLayout;
import com.google.android.material.dialog.MaterialAlertDialogBuilder;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.Query;
import com.shreyaspatil.firebase.recyclerpagination.DatabasePagingOptions;
import com.shreyaspatil.firebase.recyclerpagination.FirebaseRecyclerPagingAdapter;
import com.shreyaspatil.firebase.recyclerpagination.LoadingState;

import static com.esselion.pass.Server.SERVER_ASSIGN_TASK;
import static com.esselion.pass.util.Cache.getDatabase;
import static com.esselion.pass.util.Cache.getToken;
import static com.esselion.pass.util.Tools.launchActivity;

public class BidsListActivity extends BaseActivity implements BidHolder.Listener {

    FirebaseRecyclerPagingAdapter mAdapter;
    RecyclerView mRecyclerView;
    SwipeRefreshLayout mSwipeRefreshLayout;
    private Task task;
    private ShimmerFrameLayout shimmerContainer;

    private void initViews() {
        mRecyclerView = findViewById(R.id.recyclerView);
        mSwipeRefreshLayout = findViewById(R.id.swipe_refresh_layout);
        shimmerContainer = findViewById(R.id.shimmer_container);
    }

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        task = (Task) getIntent().getSerializableExtra("task");
        if (task == null) finish();
        setContentView(R.layout.activity_list);
        Tools.initMinToolbar(this, "ALL BIDS");
        initViews();
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
        Query mQuery = getDatabase().child("Bids").child(task.getId());

        //Initialize PagedList Configuration
        PagedList.Config config = new PagedList.Config.Builder()
                .setEnablePlaceholders(false)
                .setPrefetchDistance(5)
                .setPageSize(10)
                .build();

        //Initialize FirebasePagingOptions
        DatabasePagingOptions<Bid> options = new DatabasePagingOptions.Builder<Bid>()
                .setLifecycleOwner(this)
                .setQuery(mQuery, config, Bid.class)
                .build();

        //Initialize Adapter
        mAdapter = new FirebaseRecyclerPagingAdapter<Bid, BidHolder>(options) {
            private int RETRY_COUNT = Contracts.RETRY_COUNT;


            @NonNull
            @Override
            public BidHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
                return new BidHolder(LayoutInflater.from(parent.getContext()).inflate(R.layout.card_bid_list, parent, false), BidsListActivity.this);
            }

            @Override
            protected void onBindViewHolder(@NonNull BidHolder holder,
                                            int position,
                                            @NonNull Bid model) {
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
                        if (--RETRY_COUNT > 0) retry();
                        else {
                            LottieAnimationView emptyAnim = findViewById(R.id.lottie_anim);
                            TextView emptyText = findViewById(R.id.empty_text);
                            emptyAnim.setAnimation(R.raw.no_bids);
                            emptyText.setText("No Bids Yet");
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

    private void verifyNCall(String taskerId, String taskerName, String taskerAvatar) {
        if (prevCallResolved && server != null) {
            task.setTasker_avatar(taskerAvatar);
            task.setTasker_id(taskerId);
            task.setTasker_name(taskerName);
            getToken(token -> server.assignTask(token, task));
        }
    }

    @Override
    public void onServerCallSuccess(int methodId, String title) {
        super.onServerCallSuccess(methodId, title);
        if (methodId == SERVER_ASSIGN_TASK) {
            finish();
        }
    }

    //    TODO why we save tasks separately if we are saving in prev tasks

    @Override
    public void onClick(View v, String taskerId, String tasker_name, String tasker_avatar) {
        if (v.getId() == R.id.assign) {

            new MaterialAlertDialogBuilder(this, R.style.AlertDialogTheme)
                    .setTitle("ASSIGN TASK")
                    .setMessage("Do you want to assign the task to this bid ?")
                    .setPositiveButton("YES, ASSIGN", (dialog, which) -> verifyNCall(taskerId, tasker_name, tasker_avatar))
                    .show();
        } else {
            Intent intent = new Intent(this, ProfileActivity.class);
            intent.putExtra("id", taskerId);
            intent.putExtra("name", tasker_name);
            intent.putExtra("avatar", tasker_avatar);
            launchActivity(this, intent);
        }
    }
}
