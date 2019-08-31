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
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.Query;
import com.shreyaspatil.firebase.recyclerpagination.DatabasePagingOptions;
import com.shreyaspatil.firebase.recyclerpagination.FirebaseRecyclerPagingAdapter;
import com.shreyaspatil.firebase.recyclerpagination.LoadingState;

public class HistoryQues extends BaseActivity implements QuestionHolder.RecyclerViewClickListener {


    private FirebaseRecyclerPagingAdapter mAdapter;
    private RecyclerView mRecyclerView;
    private SwipeRefreshLayout mSwipeRefreshLayout;
    ProgressDialog dlg;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_list);
        Tools.initMinToolbar(this, "My Questions", false);
        mSwipeRefreshLayout = findViewById(R.id.swipe_refresh_layout);
        mRecyclerView = findViewById(R.id.recyclerView);
        callFireBase();
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
        //TODO remodel table to increase efficiency
        Query mQuery = FirebaseDatabase.getInstance().getReference().child("PrevQues").child(FirebaseAuth.getInstance().getCurrentUser().getUid());
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
                return new QuestionHolder(LayoutInflater.from(parent.getContext()).inflate(R.layout.card_question2, parent, false), HistoryQues.this);
            }

            @Override
            protected void onBindViewHolder(@NonNull QuestionHolder holder,
                                            int position,
                                            @NonNull Question model) {
//                Drawable drawable;
//                switch (position % 4) {
//                    case 0:
//                        drawable = getDrawable(R.drawable.bg_soft_orange);
//                        break;
//                    case 1:
//                        drawable = getDrawable(R.drawable.bg_orange);
//                        break;
//                    case 2:
//                        drawable = getDrawable(R.drawable.bg_green);
//                        break;
//                    default:
//                        drawable = getDrawable(R.drawable.bg_blue);
//                }
                Drawable drawable2 = (getDrawable(R.drawable.notification_dot_indicator));
                int c = getResources().getColor(Contracts.TASK_STAGE_COLORS[model.getStage()]);
                holder.setItem(model, true, null, drawable2, c);
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
    public void onClick(View view, Question question) {
        Intent intent = new Intent(this,QuestionDetailActivity.class);
        intent.putExtra("ques", question);
        intent.putExtra("from", true);
        startActivity(intent);
    }
}
