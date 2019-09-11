package com.example.mytasker.activities;

import android.content.Intent;
import android.os.Bundle;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import androidx.annotation.NonNull;
import androidx.paging.PagedList;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;
import androidx.swiperefreshlayout.widget.SwipeRefreshLayout;

import com.example.mytasker.R;
import com.example.mytasker.holders.BidHolder;
import com.example.mytasker.models.Bid;
import com.example.mytasker.models.Task;
import com.example.mytasker.util.Tools;
import com.facebook.shimmer.ShimmerFrameLayout;
import com.google.android.material.dialog.MaterialAlertDialogBuilder;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.Query;
import com.shreyaspatil.firebase.recyclerpagination.DatabasePagingOptions;
import com.shreyaspatil.firebase.recyclerpagination.FirebaseRecyclerPagingAdapter;
import com.shreyaspatil.firebase.recyclerpagination.LoadingState;

import static com.example.mytasker.util.Cache.getDatabase;
import static com.example.mytasker.util.Cache.getToken;

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
            private int retryCount = 4;

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
                        if (--retryCount > 0) retry();
                        else {
                            shimmerContainer.stopShimmer();
                            shimmerContainer.animate().alpha(0.0f).setDuration(200).start();
                            mRecyclerView.animate().alpha(1.0f).setDuration(200).start();
                        }
                        break;
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

    private void verifyNCall(String tasker_id) {
        if (prevCallResolved && server != null) {
            getToken(token -> server.assignTsk(token, tasker_id, task));
        }
    }


//    TODO why we save tasks separately if we are saving in prev tasks

    @Override
    public void onClick(View v, String tasker_id, String tasker_name, String tasker_avatar) {
        if (v.getId() == R.id.assign) {

            new MaterialAlertDialogBuilder(this, R.style.AlertDialogTheme)
                    .setTitle("ASSIGN TASK")
                    .setMessage("Do you want to assign the task to this bid ?")
                    .setPositiveButton("YES, ASSIGN", (dialog, which) -> verifyNCall(tasker_id))
                    .show();
        } else {
            Intent intent = new Intent(this, ProfileActivity.class);
            intent.putExtra("id", tasker_id);
            intent.putExtra("name", tasker_name);
            intent.putExtra("avatar", tasker_avatar);
            startActivity(intent);
        }
    }
}
