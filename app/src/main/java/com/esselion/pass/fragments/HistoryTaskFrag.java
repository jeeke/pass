package com.esselion.pass.fragments;

import android.content.Intent;
import android.graphics.drawable.Drawable;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import androidx.annotation.NonNull;
import androidx.fragment.app.Fragment;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.esselion.pass.R;
import com.esselion.pass.activities.TaskDetailActivity;
import com.esselion.pass.holders.TaskHolder;
import com.esselion.pass.models.Task;
import com.esselion.pass.util.Contracts;
import com.firebase.ui.database.FirebaseRecyclerAdapter;
import com.firebase.ui.database.FirebaseRecyclerOptions;
import com.google.firebase.database.Query;

import static com.esselion.pass.util.Cache.getDatabase;
import static com.esselion.pass.util.Cache.getUser;
import static com.esselion.pass.util.Tools.launchActivity;


public class HistoryTaskFrag extends Fragment implements TaskHolder.RecyclerViewClickListener {
    private int type;
    private FirebaseRecyclerAdapter mAdapter;
    private RecyclerView mRecyclerView;

    //TODO remove parameters from fragment constructors

    public HistoryTaskFrag(int type) {
        this.type = type;
    }

    public HistoryTaskFrag() {
    }

    @Override
    public View onCreateView(
            @NonNull LayoutInflater inflater, ViewGroup container,
            Bundle savedInstanceState) {
        mRecyclerView = (RecyclerView) inflater.inflate(R.layout.frag_his_task, container, false);
        callFireBase();
        return mRecyclerView;
    }


    private void callFireBase() {
        mRecyclerView.setHasFixedSize(true);
        LinearLayoutManager mManager = new LinearLayoutManager(getContext());
        mRecyclerView.setLayoutManager(mManager);
        //Initialize Database
        String c;
        if (type == 2) c = "AsTasker";
        else c = "AsPoster";
        //TODO remodel table to increase efficiency
        Query mQuery = getDatabase().child("PrevTasks").child(getUser().getUid()).child(c);
        //Initialize FirebasePagingOptions
        FirebaseRecyclerOptions<Task> options =
                new FirebaseRecyclerOptions.Builder<Task>()
                        .setQuery(mQuery, Task.class).build();
        //Initialize Adapter
        mAdapter = new FirebaseRecyclerAdapter<Task, TaskHolder>(options) {
            @NonNull
            @Override
            public TaskHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
                return new TaskHolder(LayoutInflater.from(parent.getContext()).inflate(R.layout.card_task, parent, false), HistoryTaskFrag.this);
            }

            @Override
            protected void onBindViewHolder(@NonNull TaskHolder taskHolder, int position, @NonNull Task model) {
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
                taskHolder.setItem(model, true, drawable, drawable2, c);
            }
        };
        mRecyclerView.setAdapter(mAdapter);
    }

    @Override
    public void onStart() {
        super.onStart();
        mAdapter.startListening();
    }

    @Override
    public void onStop() {
        super.onStop();
        mAdapter.stopListening();
    }

    @Override
    public void onClick(View view, Task task) {
        Intent intent = new Intent(getContext(), TaskDetailActivity.class);
        intent.putExtra("from", type);
        intent.putExtra("task", task);
        launchActivity(getActivity(), intent);
    }
}