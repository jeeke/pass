package com.esselion.pass.activities;

import android.content.Intent;
import android.os.Bundle;
import android.view.View;

import com.esselion.pass.R;
import com.esselion.pass.util.FeedNPortAdapter;

import static com.esselion.pass.util.Tools.launchActivity;

public class HistoryFeed extends BaseActivity {
    private FeedNPortAdapter feedNPortAdapter;
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
        feedNPortAdapter = new FeedNPortAdapter();
        feedNPortAdapter.callFireBase(uid, mine, this, from, findViewById(R.id.recyclerView));
    }

    private void addPortItem() {
        Intent intent = new Intent(HistoryFeed.this, PostFeed.class);
        intent.putExtra("fromPortfolio", true);
        launchActivity(this, intent);
    }

    //Start Listening Adapter
    @Override
    public void onStart() {
        super.onStart();
        if (feedNPortAdapter != null && feedNPortAdapter.mAdapter != null)
            feedNPortAdapter.mAdapter.startListening();
    }

    //Stop Listening Adapter
    @Override
    public void onStop() {
        super.onStop();
        if (feedNPortAdapter != null && feedNPortAdapter.mAdapter != null)
            feedNPortAdapter.mAdapter.stopListening();
    }
}
