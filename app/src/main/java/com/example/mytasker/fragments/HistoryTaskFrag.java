package com.example.mytasker.fragments;

import android.content.Intent;
import android.graphics.drawable.Drawable;
import android.os.Bundle;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import androidx.annotation.NonNull;
import androidx.fragment.app.Fragment;
import androidx.paging.PagedList;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;
import androidx.swiperefreshlayout.widget.SwipeRefreshLayout;

import com.example.mytasker.R;
import com.example.mytasker.activities.TaskDetailActivity;
import com.example.mytasker.holders.TaskHolder;
import com.example.mytasker.models.Task;
import com.example.mytasker.util.Contracts;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.Query;
import com.shreyaspatil.firebase.recyclerpagination.DatabasePagingOptions;
import com.shreyaspatil.firebase.recyclerpagination.FirebaseRecyclerPagingAdapter;
import com.shreyaspatil.firebase.recyclerpagination.LoadingState;

import java.util.ArrayList;

import static com.example.mytasker.util.Cache.getDatabase;
import static com.example.mytasker.util.Cache.getUser;


public class HistoryTaskFrag extends Fragment implements TaskHolder.RecyclerViewClickListener {
    private ArrayList<Task> tasks;
    private int type;
    private FirebaseRecyclerPagingAdapter mAdapter;
    private RecyclerView mRecyclerView;
    private SwipeRefreshLayout mSwipeRefreshLayout;

    //TODO remove parameters from fragment constructors

    public HistoryTaskFrag(int type) {
        this.type = type;
        tasks = new ArrayList<>();
    }

    public HistoryTaskFrag() {
    }

    @Override
    public View onCreateView(
            @NonNull LayoutInflater inflater, ViewGroup container,
            Bundle savedInstanceState) {
        mSwipeRefreshLayout = (SwipeRefreshLayout) inflater.inflate(R.layout.frag_his_task, container, false);
        mRecyclerView = mSwipeRefreshLayout.findViewById(R.id.recyclerView);
        callFireBase();
        return mSwipeRefreshLayout;
    }

    private void callFireBase() {
        mSwipeRefreshLayout.setColorSchemeResources(

                android.R.color.holo_blue_bright,

                android.R.color.holo_green_light);

        //Initialize RecyclerView
        mRecyclerView.setHasFixedSize(true);

        LinearLayoutManager mManager = new LinearLayoutManager(getContext());
        mRecyclerView.setLayoutManager(mManager);

        //Initialize Database
        String c;
        if (type == 2) c = "AsTasker";
        else c = "AsPoster";
        //TODO remodel table to increase efficiency
        Query mQuery = getDatabase().child("PrevTasks").child(getUser().getUid()).child(c);
        //Initialize PagedList Configuration
        PagedList.Config config = new PagedList.Config.Builder()
                .setEnablePlaceholders(false)
                .setPrefetchDistance(5)
                .setPageSize(10)
                .build();
        //Initialize FirebasePagingOptions
        DatabasePagingOptions<Task> options = new DatabasePagingOptions.Builder<Task>()
                .setLifecycleOwner(this)
                .setQuery(mQuery, config, Task.class)
                .build();
        //Initialize Adapter
        mAdapter = new FirebaseRecyclerPagingAdapter<Task, TaskHolder>(options) {

            private int retryCount = 4;

            @NonNull
            @Override
            public TaskHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
                return new TaskHolder(LayoutInflater.from(parent.getContext()).inflate(R.layout.card_task, parent, false), HistoryTaskFrag.this);
            }

            @Override
            protected void onBindViewHolder(@NonNull TaskHolder holder,
                                            int position,
                                            @NonNull Task model) {
                Drawable drawable;
                switch (position % 4) {
                    case 0:
                        drawable = getContext().getDrawable(R.drawable.bg_soft_orange);
                        break;
                    case 1:
                        drawable = getContext().getDrawable(R.drawable.bg_orange);
                        break;
                    case 2:
                        drawable = getContext().getDrawable(R.drawable.bg_green);
                        break;
                    default:
                        drawable = getContext().getDrawable(R.drawable.bg_blue);
                }
                Drawable drawable2 = (getContext().getDrawable(R.drawable.notification_dot_indicator));
                int c = getContext().getResources().getColor(Contracts.TASK_STAGE_COLORS[model.getStage()]);
                holder.setItem(model, true, drawable, drawable2, c);
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
                        if (--retryCount > 0)
                            retry();
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

    @Override
    public void onClick(View view, Task task) {
        Intent intent = new Intent(getContext(), TaskDetailActivity.class);
        intent.putExtra("from", type);
        intent.putExtra("task", task);
        startActivity(intent);
    }
}