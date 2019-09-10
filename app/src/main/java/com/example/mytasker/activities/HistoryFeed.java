package com.example.mytasker.activities;

import android.os.Bundle;

import androidx.recyclerview.widget.RecyclerView;
import androidx.swiperefreshlayout.widget.SwipeRefreshLayout;

import com.example.mytasker.R;
import com.example.mytasker.util.FeedActNFrag;

public class HistoryFeed extends BaseActivity {
    private FeedActNFrag feedActNFrag;
    private RecyclerView mRecyclerView;
    private SwipeRefreshLayout mSwipeRefreshLayout;
    boolean from = false;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_list);
        from = getIntent().getBooleanExtra("from", false);
        feedActNFrag = new FeedActNFrag();
        initViews();
        feedActNFrag.callFireBase(this, findViewById(R.id.shimmer_container), from, mSwipeRefreshLayout, mRecyclerView, 2, null);
    }

    private void initViews() {
        mRecyclerView = findViewById(R.id.recyclerView);
        mSwipeRefreshLayout = findViewById(R.id.swipe_refresh_layout);
    }

    //Start Listening Adapter
    @Override
    public void onStart() {
        super.onStart();
        feedActNFrag.mAdapter.startListening();
    }

    //Stop Listening Adapter
    @Override
    public void onStop() {
        super.onStop();
        feedActNFrag.mAdapter.stopListening();
    }
}
