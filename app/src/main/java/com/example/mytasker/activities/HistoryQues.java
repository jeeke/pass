package com.example.mytasker.activities;

import android.app.ProgressDialog;
import android.content.Intent;
import android.graphics.drawable.Drawable;
import android.os.Bundle;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import androidx.annotation.NonNull;
import androidx.paging.PagedList;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;
import androidx.swiperefreshlayout.widget.SwipeRefreshLayout;

import com.example.mytasker.R;
import com.example.mytasker.holders.QuestionHolder;
import com.example.mytasker.models.Question;
import com.example.mytasker.util.Contracts;
import com.example.mytasker.util.Tools;
import com.facebook.shimmer.ShimmerFrameLayout;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.Query;
import com.shreyaspatil.firebase.recyclerpagination.DatabasePagingOptions;
import com.shreyaspatil.firebase.recyclerpagination.FirebaseRecyclerPagingAdapter;
import com.shreyaspatil.firebase.recyclerpagination.LoadingState;

import static com.example.mytasker.util.Cache.getDatabase;
import static com.example.mytasker.util.Cache.getUser;

public class HistoryQues extends BaseActivity implements QuestionHolder.RecyclerViewClickListener {


    private FirebaseRecyclerPagingAdapter mAdapter;
    private RecyclerView mRecyclerView;
    private SwipeRefreshLayout mSwipeRefreshLayout;
    private ShimmerFrameLayout shimmerContainer;
    ProgressDialog dlg;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_list);
        Tools.initMinToolbar(this, "My Questions", false);
        mSwipeRefreshLayout = findViewById(R.id.swipe_refresh_layout);
        mRecyclerView = findViewById(R.id.recyclerView);
        shimmerContainer = findViewById(R.id.shimmer_container);
        callFireBase();
    }

    private void callFireBase() {
        mSwipeRefreshLayout.setColorSchemeResources(

                android.R.color.holo_blue_bright,

                android.R.color.holo_green_light);

        //Initialize RecyclerView
        mRecyclerView.setHasFixedSize(true);

        LinearLayoutManager mManager = new LinearLayoutManager(this);
        mRecyclerView.setLayoutManager(mManager);

        //Initialize Database
        //TODO remodel table to increase efficiency
        Query mQuery = getDatabase().child("PrevQues").child(getUser().getUid());
        //Initialize PagedList Configuration
        PagedList.Config config = new PagedList.Config.Builder()
                .setEnablePlaceholders(false)
                .setPrefetchDistance(5)
                .setPageSize(10)
                .build();
        //Initialize FirebasePagingOptions
        DatabasePagingOptions<Question> options = new DatabasePagingOptions.Builder<Question>()
                .setLifecycleOwner(this)
                .setQuery(mQuery, config, Question.class)
                .build();
        //Initialize Adapter
        mAdapter = new FirebaseRecyclerPagingAdapter<Question, QuestionHolder>(options) {

            private int retryCount = 4;

            @NonNull
            @Override
            public QuestionHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
                return new QuestionHolder(LayoutInflater.from(parent.getContext()).inflate(R.layout.card_question, parent, false), HistoryQues.this);
            }

            @Override
            protected void onBindViewHolder(@NonNull QuestionHolder holder,
                                            int position,
                                            @NonNull Question model) {
                Drawable drawable2 = (getDrawable(R.drawable.notification_dot_indicator));
                int c = getResources().getColor(Contracts.TASK_STAGE_COLORS[model.getStage()]);
                holder.setItem(model, true, null, drawable2, c);
            }

            @Override
            protected void onLoadingStateChanged(@NonNull LoadingState state) {
                switch (state) {
                    case LOADING_INITIAL:
                        mRecyclerView.animate().alpha(0.0f).start();
                        shimmerContainer.animate().alpha(1.0f).start();
                        shimmerContainer.startShimmer();
                        break;
                    case LOADING_MORE:
                        // Do your loading animation
                        mSwipeRefreshLayout.setRefreshing(true);
                        break;

                    case LOADED:
                        // Stop Animation
                        shimmerContainer.stopShimmer();
                        shimmerContainer.animate().alpha(0.0f).setDuration(200).start();
                        mRecyclerView.animate().alpha(1.0f).setDuration(200).start();
                        mSwipeRefreshLayout.setRefreshing(false);
                        mSwipeRefreshLayout.setRefreshing(false);
                        break;

                    case FINISHED:
                        //Reached end of Data set
                        mSwipeRefreshLayout.setRefreshing(false);
                        break;

                    case ERROR:
                        if (--retryCount > 0)
                            retry();
                        else {
                            shimmerContainer.stopShimmer();
                            shimmerContainer.animate().alpha(0.0f).setDuration(200).start();
                            mRecyclerView.animate().alpha(1.0f).setDuration(200).start();
                        }
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
    public void onClick(View view, Question question) {
        Intent intent = new Intent(this, QuestionDetailActivity.class);
        intent.putExtra("ques", question);
        intent.putExtra("from", true);
        startActivity(intent);
    }
}
