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
    boolean mine = true;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        from = getIntent().getBooleanExtra("from", false);
        mine = getIntent().getBooleanExtra("mine", false);
        if (from) setContentView(R.layout.activity_portfolio);
        else setContentView(R.layout.activity_list);
        String uid = getIntent().getStringExtra("uid");
        feedActNFrag = new FeedActNFrag();
        initViews();
        feedActNFrag.callFireBase(uid, mine, this,
                findViewById(R.id.shimmer_container), from, mSwipeRefreshLayout,
                mRecyclerView, 2, null);
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
