package com.example.mytasker.activities;

import android.os.Bundle;

import androidx.recyclerview.widget.RecyclerView;
import androidx.swiperefreshlayout.widget.SwipeRefreshLayout;

import com.example.mytasker.R;
import com.example.mytasker.util.FeedActNFrag;
import com.example.mytasker.util.Tools;
import com.google.firebase.database.Query;

import static com.example.mytasker.util.Cache.getDatabase;
import static com.example.mytasker.util.Cache.getUser;

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
        String title;
        Query mQuery;
        if (from) {
            title = "Portfolio";
            mQuery = getDatabase().child("Portfolios").child(getUser().getUid());
        } else {
            title = "My Posts";
            mQuery = getDatabase().child("PrevFeeds").child(getUser().getUid());
        }
        Tools.initMinToolbar(this, title, false);
        initViews();
        feedActNFrag = new FeedActNFrag();
        feedActNFrag.callFireBase(this, findViewById(R.id.shimmer_container), mQuery, mSwipeRefreshLayout, mRecyclerView, true);
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
