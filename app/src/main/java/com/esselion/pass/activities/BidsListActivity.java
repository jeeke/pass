package com.esselion.pass.activities;

import android.content.Intent;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.airbnb.lottie.LottieAnimationView;
import com.esselion.pass.R;
import com.esselion.pass.holders.BidHolder;
import com.esselion.pass.models.Bid;
import com.esselion.pass.models.Task;
import com.esselion.pass.util.Tools;
import com.firebase.ui.database.FirebaseRecyclerAdapter;
import com.firebase.ui.database.FirebaseRecyclerOptions;
import com.google.android.material.dialog.MaterialAlertDialogBuilder;
import com.google.firebase.database.Query;

import static com.esselion.pass.Server.SERVER_ASSIGN_TASK;
import static com.esselion.pass.util.Cache.getDatabase;
import static com.esselion.pass.util.Cache.getToken;
import static com.esselion.pass.util.Tools.launchActivity;

public class BidsListActivity extends BaseActivity implements BidHolder.Listener {

    FirebaseRecyclerAdapter mAdapter;
    RecyclerView mRecyclerView;
    private Task task;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        task = (Task) getIntent().getSerializableExtra("task");
        if (task == null) finish();
        setContentView(R.layout.activity_list);
        LottieAnimationView animationView = findViewById(R.id.lottie_anim);
        animationView.setAnimation(R.raw.no_bids);
        TextView textView = findViewById(R.id.empty_text);
        textView.setText("No Bids Yet");
        Tools.initMinToolbar(this, "ALL BIDS");
        mRecyclerView = findViewById(R.id.recyclerView);
        callFireBase();
    }

    private void callFireBase() {
        View anim = findViewById(R.id.anim);
        mRecyclerView.setHasFixedSize(true);

        LinearLayoutManager mManager = new LinearLayoutManager(this);
        mRecyclerView.setLayoutManager(mManager);

        //Initialize Database
        Query mQuery = getDatabase().child("Bids").child(task.getId());

        FirebaseRecyclerOptions<Bid> options =
                new FirebaseRecyclerOptions.Builder<Bid>()
                        .setQuery(mQuery, Bid.class).build();
        //Initialize Adapter
        mAdapter = new FirebaseRecyclerAdapter<Bid, BidHolder>(options) {
            @NonNull
            @Override
            public BidHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
                anim.setVisibility(View.GONE);
                return new BidHolder(LayoutInflater.from(parent.getContext()).inflate(R.layout.card_bid_list, parent, false), BidsListActivity.this);
            }

            @Override
            protected void onBindViewHolder(@NonNull BidHolder holder,
                                            int position,
                                            @NonNull Bid model) {
                holder.setItem(model);
            }
        };

        //Set Adapter to RecyclerView
        mRecyclerView.setAdapter(mAdapter);
    }

    private void verifyNCall(String taskerId, String taskerName, String taskerAvatar) {
        if (prevCallResolved && server != null) {
            task.setTasker_avatar(taskerAvatar);
            task.setTasker_id(taskerId);
            task.setTasker_name(taskerName);
            getToken(token -> server.assignTask(token, task));
        }
    }

    @Override
    public void onServerCallSuccess(int methodId, String title) {
        super.onServerCallSuccess(methodId, title);
        if (methodId == SERVER_ASSIGN_TASK) {
            finish();
        }
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

    //    TODO why we save tasks separately if we are saving in prev tasks

    @Override
    public void onClick(View v, String taskerId, String tasker_name, String tasker_avatar) {
        if (v.getId() == R.id.assign) {

            new MaterialAlertDialogBuilder(this, R.style.AlertDialogTheme)
                    .setTitle("ASSIGN TASK")
                    .setMessage("Do you want to assign the task to this bid ?")
                    .setPositiveButton("YES, ASSIGN", (dialog, which) -> verifyNCall(taskerId, tasker_name, tasker_avatar))
                    .show();
        } else {
            Intent intent = new Intent(this, ProfileActivity.class);
            intent.putExtra("id", taskerId);
            intent.putExtra("name", tasker_name);
            intent.putExtra("avatar", tasker_avatar);
            launchActivity(this, intent);
        }
    }
}
