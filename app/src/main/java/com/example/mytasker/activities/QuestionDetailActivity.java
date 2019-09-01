package com.example.mytasker.activities;

import android.app.ProgressDialog;
import android.content.Context;
import android.os.Bundle;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.view.inputmethod.InputMethodManager;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.paging.PagedList;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;
import androidx.swiperefreshlayout.widget.SwipeRefreshLayout;

import com.bumptech.glide.Glide;
import com.bumptech.glide.request.RequestOptions;
import com.example.mytasker.R;
import com.example.mytasker.holders.AnswerHolder;
import com.example.mytasker.models.Answer;
import com.example.mytasker.models.Question;
import com.example.mytasker.util.Tools;
import com.google.android.material.floatingactionbutton.FloatingActionButton;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.Query;
import com.shreyaspatil.firebase.recyclerpagination.DatabasePagingOptions;
import com.shreyaspatil.firebase.recyclerpagination.FirebaseRecyclerPagingAdapter;
import com.shreyaspatil.firebase.recyclerpagination.LoadingState;

import static com.example.mytasker.util.Cache.getUser;

public class QuestionDetailActivity extends BaseActivity {

    ProgressDialog dlg;
    Question current;
    private FirebaseRecyclerPagingAdapter mAdapter;
    private RecyclerView mRecyclerView;
    EditText answer;
    FloatingActionButton fab;
    private SwipeRefreshLayout mSwipeRefreshLayout;

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
        Query mQuery = FirebaseDatabase.getInstance().getReference().child("Answers").child(current.getId());
        //Initialize PagedList Configuration
        PagedList.Config config = new PagedList.Config.Builder()
                .setEnablePlaceholders(false)
                .setPrefetchDistance(5)
                .setPageSize(10)
                .build();
        //Initialize FirebasePagingOptions
        DatabasePagingOptions<Answer> options = new DatabasePagingOptions.Builder<Answer>()
                .setLifecycleOwner(this)
                .setQuery(mQuery, config, Answer.class)
                .build();
        //Initialize Adapter
        mAdapter = new FirebaseRecyclerPagingAdapter<Answer, AnswerHolder>(options) {

            private int retryCount = 3;

            @NonNull
            @Override
            public AnswerHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
                return new AnswerHolder(LayoutInflater.from(parent.getContext()).inflate(R.layout.card_answer, parent, false));
            }

            @Override
            protected void onBindViewHolder(@NonNull AnswerHolder holder,
                                            int position,
                                            @NonNull Answer model) {
                holder.setItem(model);
            }

            @Override
            protected void onLoadingStateChanged(@NonNull LoadingState state) {
                switch (state) {
                    case LOADING_INITIAL:
                    case LOADING_MORE:
                        mSwipeRefreshLayout.setRefreshing(false);
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
        mRecyclerView.setAdapter(mAdapter);
        mSwipeRefreshLayout.setOnRefreshListener(() -> mAdapter.refresh());
    }


    boolean prevCallResolved = true;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        current = (Question) getIntent().getSerializableExtra("ques");
        boolean from = getIntent().getBooleanExtra("from", false);
        if (from) ((EditText) findViewById(R.id.editText)).setHint("Type Some Comment Here");
        if (current == null) finish();
        setContentView(R.layout.activity_question_detail);
        TextView name = findViewById(R.id.person_name);
        TextView ques = findViewById(R.id.question);
        ImageView image = findViewById(R.id.person);
        name.setText(current.getPosterName());
        ques.setText(current.getQues());
        Glide.with(this).load(current.getPoster_image()).apply(new RequestOptions().placeholder(R.drawable.person)).into(image);
        Tools.initMinToolbar(this, "Answers", false);
        mSwipeRefreshLayout = findViewById(R.id.swipe_refresh_layout);
        mRecyclerView = findViewById(R.id.recyclerView);
        callFireBase();
        fab = findViewById(R.id.submit_answer);
        answer = findViewById(R.id.editText);
        fab.setOnClickListener(v -> {
            if (answer.getText().toString().equals(""))
                Toast.makeText(QuestionDetailActivity.this, "Enter valid answer", Toast.LENGTH_SHORT).show();
            else {
                submitAnswer();
            }
        });
    }

    private void submitAnswer() {
        if (!prevCallResolved) return;
        FirebaseUser user = getUser();
        if (user != null) {
            Answer ans = new Answer(user.getUid(), user.getDisplayName(), user.getPhotoUrl().toString(), current.getPoster_id(), answer.getText().toString());
            FirebaseDatabase.getInstance().getReference().child("Answers")
                    .child(current.getId()).push().setValue(ans).addOnCompleteListener(task -> {
                prevCallResolved = true;
                if (task.isSuccessful()) {
                    answer.setText("");
                    mAdapter.refresh();
                    closeKeyboard();
                    Toast.makeText(this, "Posted", Toast.LENGTH_SHORT).show();
                } else {
                    Toast.makeText(this, "Posting error", Toast.LENGTH_SHORT).show();
                }
            });
            prevCallResolved = false;
        }
    }


    private void closeKeyboard() {
        View view = this.getCurrentFocus();
        if (view != null) {
            InputMethodManager imm = (InputMethodManager) getSystemService(Context.INPUT_METHOD_SERVICE);
            imm.hideSoftInputFromWindow(view.getWindowToken(), 0);
        }
    }
}
