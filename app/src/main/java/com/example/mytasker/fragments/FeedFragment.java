package com.example.mytasker.fragments;

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

import com.example.mytasker.R;
import com.example.mytasker.activities.HistoryFeed;
import com.example.mytasker.util.FeedActNFrag;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.Query;

import static com.example.mytasker.util.Tools.launchActivity;

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
            launchActivity((AppCompatActivity) getActivity(), HistoryFeed.class);
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
        Query mQuery = FirebaseDatabase.getInstance().getReference().child("Feeds");
        feedActNFrag = new FeedActNFrag();
        feedActNFrag.callFireBase(getActivity(), mQuery, mSwipeRefreshLayout, mRecyclerView, false, FirebaseDatabase.getInstance().getReference());
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
