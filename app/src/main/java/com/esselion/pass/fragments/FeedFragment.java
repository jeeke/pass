package com.esselion.pass.fragments;

import android.content.Intent;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.MenuItem;
import android.view.View;
import android.view.ViewGroup;

import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.widget.Toolbar;
import androidx.fragment.app.Fragment;
import androidx.recyclerview.widget.RecyclerView;
import androidx.swiperefreshlayout.widget.SwipeRefreshLayout;

import com.esselion.pass.R;
import com.esselion.pass.activities.HistoryFeed;
import com.esselion.pass.util.FeedActNFrag;
import com.google.firebase.database.Query;

import static com.esselion.pass.util.Cache.getDatabase;
import static com.esselion.pass.util.Cache.getUser;

public class FeedFragment extends Fragment {
    public FeedFragment() {
    }

    private void initToolbar(View v) {
        Toolbar toolbar = v.findViewById(R.id.toolbar);
        toolbar.setTitle("Events");
        toolbar.setTitleTextColor(getContext().getResources().getColor(R.color.blue_grey));
        ((AppCompatActivity) getActivity()).setSupportActionBar(toolbar);
//        Tools.setSystemBarColor(getActivity(), R.color.green_800);
    }

    private FeedActNFrag feedActNFrag;

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
            startActivity(intent);
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
        Query mQuery = getDatabase().child("Feeds");
        feedActNFrag = new FeedActNFrag();
        feedActNFrag.callFireBase(getUser(getActivity()).getUid(), false, getActivity(), v.findViewById(R.id.shimmer_container), false, mSwipeRefreshLayout, mRecyclerView, 0, mQuery);
        mRecyclerView.setHasFixedSize(true);
        return v;
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

    private void initViews(View v) {
        mRecyclerView = v.findViewById(R.id.list);
        mSwipeRefreshLayout = v.findViewById(R.id.swipe_refresh_layout);
    }
}
