package com.esselion.pass.activities;

import android.content.Intent;
import android.os.Bundle;
import android.view.View;

import androidx.recyclerview.widget.RecyclerView;
import androidx.swiperefreshlayout.widget.SwipeRefreshLayout;

import com.esselion.pass.R;
import com.esselion.pass.util.FeedActNFrag;

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
        if (from) {
            setContentView(R.layout.activity_portfolio);
            if (mine) {
                View fab = findViewById(R.id.add_portfolio);
                fab.setOnClickListener(v -> addPortItem());
                fab.setVisibility(View.VISIBLE);
            }

        } else setContentView(R.layout.activity_list);
        String uid = getIntent().getStringExtra("uid");
        feedActNFrag = new FeedActNFrag();
        initViews();
        feedActNFrag.callFireBase(uid, mine, this,
                findViewById(R.id.shimmer_container), from, mSwipeRefreshLayout,
                mRecyclerView, 2, null);
    }

    private void addPortItem() {
        Intent intent = new Intent(HistoryFeed.this, PostFeed.class);
        intent.putExtra("fromPortfolio", true);
        startActivity(intent);
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
