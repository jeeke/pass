package com.example.mytasker.activities;

import android.app.ProgressDialog;
import android.os.Bundle;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.paging.PagedList;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;
import androidx.swiperefreshlayout.widget.SwipeRefreshLayout;

import com.example.mytasker.R;
import com.example.mytasker.holders.BidHolder;
import com.example.mytasker.models.Bid;
import com.example.mytasker.models.Message;
import com.example.mytasker.models.Task;
import com.example.mytasker.retrofit.JsonPlaceHolder;
import com.example.mytasker.util.Tools;
import com.google.android.material.dialog.MaterialAlertDialogBuilder;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.Query;
import com.shreyaspatil.firebase.recyclerpagination.DatabasePagingOptions;
import com.shreyaspatil.firebase.recyclerpagination.FirebaseRecyclerPagingAdapter;
import com.shreyaspatil.firebase.recyclerpagination.LoadingState;

import java.util.HashMap;
import java.util.Map;

import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;
import retrofit2.Retrofit;

import static com.example.mytasker.util.Tools.getRetrofit;

public class BidsListActivity extends BaseActivity implements BidHolder.Listener {

    FirebaseRecyclerPagingAdapter mAdapter;
    RecyclerView mRecyclerView;
    SwipeRefreshLayout mSwipeRefreshLayout;
    private Task task;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        task = (Task) getIntent().getSerializableExtra("task");
        if (task == null) finish();
        setContentView(R.layout.activity_list);
        Tools.initMinToolbar(this,"ALL BIDS",false);
        initViews();
        callFireBase();
    }

    private void initViews() {
        mRecyclerView = findViewById(R.id.recyclerView);
        mSwipeRefreshLayout = findViewById(R.id.swipe_refresh_layout);
    }

    private void callFireBase() {
        mSwipeRefreshLayout.setColorSchemeResources(
                android.R.color.holo_red_light,

                android.R.color.holo_orange_light,

                android.R.color.holo_blue_bright,

                android.R.color.holo_green_light);

        //Initialize RecyclerView
        mRecyclerView.setHasFixedSize(true);

        LinearLayoutManager mManager = new LinearLayoutManager(this);
        mRecyclerView.setLayoutManager(mManager);

        //Initialize Database
        Query mQuery = FirebaseDatabase.getInstance().getReference().child("Bids").child(task.getId());

        //Initialize PagedList Configuration
        PagedList.Config config = new PagedList.Config.Builder()
                .setEnablePlaceholders(false)
                .setPrefetchDistance(5)
                .setPageSize(10)
                .build();

        //Initialize FirebasePagingOptions
        DatabasePagingOptions<Bid> options = new DatabasePagingOptions.Builder<Bid>()
                .setLifecycleOwner(this)
                .setQuery(mQuery, config, Bid.class)
                .build();

        //Initialize Adapter
        mAdapter = new FirebaseRecyclerPagingAdapter<Bid, BidHolder>(options) {
            private int retryCount = 4;

            @NonNull
            @Override
            public BidHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
                return new BidHolder(LayoutInflater.from(parent.getContext()).inflate(R.layout.card_bid_list, parent, false), BidsListActivity.this);
            }

            @Override
            protected void onBindViewHolder(@NonNull BidHolder holder,
                                            int position,
                                            @NonNull Bid model) {
                holder.setItem(model);
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
                        if (--retryCount > 0) retry();
                        break;
                }
            }

            @Override
            protected void onError(@NonNull DatabaseError databaseError) {
                super.onError(databaseError);
                mSwipeRefreshLayout.setRefreshing(false);
                databaseError.toException().printStackTrace();
            }
        };

        //Set Adapter to RecyclerView
        mRecyclerView.setAdapter(mAdapter);

        //Set listener to SwipeRefreshLayout for refresh action
        mSwipeRefreshLayout.setOnRefreshListener(() -> mAdapter.refresh());
    }


    private void verifyNCall(String tasker_id) {
        FirebaseUser mUser = FirebaseAuth.getInstance().getCurrentUser();
        mUser.getIdToken(true)
                .addOnCompleteListener(task -> {
                    if (task.isSuccessful()) {
                        callRetrofit(task.getResult().getToken(), tasker_id);
                    } else {
                        // Handle error -> task.getException();
                        Toast.makeText(this, "Authentication Error!", Toast.LENGTH_SHORT).show();
                    }
                });
    }

    private void callRetrofit(String token, String tasker_id) {
        ProgressDialog dlg = new ProgressDialog(this);
        dlg.setTitle("Assigning Task...");
        dlg.show();
        Retrofit retrofit = getRetrofit(token);
        JsonPlaceHolder jsonPlaceHolder = retrofit.create(JsonPlaceHolder.class);
        Map map = new HashMap();
        map.put("task", task);
        map.put("tasker_id", tasker_id);
        Call<Message> call = jsonPlaceHolder.assignTask(map);

        call.enqueue(new Callback<Message>() {
            @Override
            public void onResponse(Call<Message> call, Response<Message> response) {
                dlg.dismiss();
                if (!response.isSuccessful()) {
                    Log.v("Code: ", response.code() + " Message: " + response.body());
                    Toast.makeText(BidsListActivity.this, "Task couldn't be assigned", Toast.LENGTH_SHORT).show();
                } else
                    Toast.makeText(BidsListActivity.this, "Task assigned successfully", Toast.LENGTH_SHORT).show();
            }

            @Override
            public void onFailure(Call<Message> call, Throwable t) {
                Log.e("error ", t.getMessage());
                dlg.dismiss();
                Toast.makeText(BidsListActivity.this, "Task couldn't be assigned", Toast.LENGTH_SHORT).show();
            }
        });
    }

//    TODO why we save tasks separately if we are saving in prev tasks

    @Override
    public void onClick(View v, String tasker_id) {
        new MaterialAlertDialogBuilder(this, R.style.AlertDialogTheme)
                .setTitle("ASSIGN TASK")
                .setMessage("Do you want to assign the task to this bid ?")
                .setPositiveButton("YES, ASSIGN", (dialog, which) -> verifyNCall(tasker_id))
                .show();
    }
}
