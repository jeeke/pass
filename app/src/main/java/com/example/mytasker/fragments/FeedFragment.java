package com.example.mytasker.fragments;

import android.os.Bundle;
import android.view.LayoutInflater;
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

import com.example.mytasker.R;
import com.example.mytasker.adapters.FeedAdapter;
import com.example.mytasker.models.Feed;
import com.example.mytasker.models.FeedViewHolder;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.Query;
import com.shreyaspatil.firebase.recyclerpagination.DatabasePagingOptions;
import com.shreyaspatil.firebase.recyclerpagination.FirebaseRecyclerPagingAdapter;
import com.shreyaspatil.firebase.recyclerpagination.LoadingState;

public class FeedFragment extends Fragment implements FeedAdapter.RecyclerViewClickListener {
    public FeedFragment() {
    }

    private void initToolbar(View v) {
        Toolbar toolbar = v.findViewById(R.id.toolbar);
        toolbar.setTitle("FEED");
        toolbar.setTitleTextColor(getContext().getResources().getColor(R.color.blue_grey));
        ((AppCompatActivity) getActivity()).setSupportActionBar(toolbar);
//        Tools.setSystemBarColor(getActivity(), R.color.green_800);
    }

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

    }

    private void callFireBase() {
        mSwipeRefreshLayout.setColorSchemeResources(
                android.R.color.holo_red_light,

                android.R.color.holo_orange_light,

                android.R.color.holo_blue_bright,

                android.R.color.holo_green_light);

        //Initialize RecyclerView
        mRecyclerView.setHasFixedSize(true);

        LinearLayoutManager mManager = new LinearLayoutManager(getContext());
        mRecyclerView.setLayoutManager(mManager);

        //Initialize Database
        Query mQuery = FirebaseDatabase.getInstance().getReference().child("Feeds").orderByChild("createdAt");

        //Initialize PagedList Configuration
        PagedList.Config config = new PagedList.Config.Builder()
                .setEnablePlaceholders(false)
                .setPrefetchDistance(5)
                .setPageSize(10)
                .build();

        //Initialize FirebasePagingOptions
        DatabasePagingOptions<Feed> options = new DatabasePagingOptions.Builder<Feed>()
                .setLifecycleOwner(this)
                .setQuery(mQuery,config,Feed.class)
                .build();

        //Initialize Adapter
        mAdapter = new FirebaseRecyclerPagingAdapter<Feed, FeedViewHolder>(options) {
            @NonNull
            @Override
            public FeedViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
                return new FeedViewHolder(LayoutInflater.from(parent.getContext()).inflate(R.layout.card_feed, parent, false));
            }

            @Override
            protected void onBindViewHolder(@NonNull FeedViewHolder holder,
                                            int position,
                                            @NonNull Feed model) {
                holder.setItem(model);
            }

            @Override
            protected void onLoadingStateChanged(@NonNull LoadingState state) {
                switch (state) {
                    case LOADING_INITIAL:
                    case LOADING_MORE:
                        // Do your loading animation
                        mSwipeRefreshLayout.setRefreshing(true);
                        break;

                    case LOADED:
                        // Stop Animation
                        mSwipeRefreshLayout.setRefreshing(false);
                        break;

                    case FINISHED:
                        //Reached end of Data set
                        mSwipeRefreshLayout.setRefreshing(false);
                        break;

                    case ERROR:
                        retry();
                        break;
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

        //Set listener to SwipeRefreshLayout for refresh action
        mSwipeRefreshLayout.setOnRefreshListener(() -> mAdapter.refresh());
    }

    private RecyclerView mRecyclerView;
    private SwipeRefreshLayout mSwipeRefreshLayout;
    FirebaseRecyclerPagingAdapter<Feed, FeedViewHolder> mAdapter;

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        View v = inflater.inflate(R.layout.fragment_feed, container, false);
        initToolbar(v);
        initViews(v);
        callFireBase();
        return v;
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

    @Override
    public void onClick(View view, int position) {

    }
}
