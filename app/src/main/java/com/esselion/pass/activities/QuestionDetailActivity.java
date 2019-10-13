package com.esselion.pass.activities;

import android.content.Context;
import android.os.Bundle;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.view.ViewGroup;
import android.view.inputmethod.InputMethodManager;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.paging.PagedList;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;
import androidx.swiperefreshlayout.widget.SwipeRefreshLayout;

import com.bumptech.glide.Glide;
import com.bumptech.glide.request.RequestOptions;
import com.esselion.pass.R;
import com.esselion.pass.holders.AnswerHolder;
import com.esselion.pass.models.Answer;
import com.esselion.pass.models.Question;
import com.esselion.pass.util.Tools;
import com.google.android.material.floatingactionbutton.FloatingActionButton;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.Query;
import com.shreyaspatil.firebase.recyclerpagination.DatabasePagingOptions;
import com.shreyaspatil.firebase.recyclerpagination.FirebaseRecyclerPagingAdapter;
import com.shreyaspatil.firebase.recyclerpagination.LoadingState;

import static com.esselion.pass.util.Cache.getToken;
import static com.esselion.pass.util.Cache.getUser;
import static com.esselion.pass.util.Contracts.getPushKey;
import static com.esselion.pass.util.Tools.showSnackBar;

public class QuestionDetailActivity extends BaseActivity {

    Question current;
    private FirebaseRecyclerPagingAdapter mAdapter;
    private RecyclerView mRecyclerView;
    EditText answer;
    FloatingActionButton fab;
    private SwipeRefreshLayout mSwipeRefreshLayout;

    boolean from = false;

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


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_question_detail);

        current = (Question) getIntent().getSerializableExtra("ques");
        from = getIntent().getBooleanExtra("from", false);
        if (from) ((EditText) findViewById(R.id.editText)).setHint("Type Some Comment Here");
        if (current == null) finish();
        TextView name = findViewById(R.id.person_name);
        TextView ques = findViewById(R.id.question);
        ImageView image = findViewById(R.id.person);
        name.setText(current.getPosterName());
        ques.setText(current.getQues());
        Glide.with(this).load(current.getPoster_image())
                .apply(new RequestOptions()
                        .placeholder(R.drawable.person)
                        .circleCrop()).into(image);
        Tools.initMinToolbar(this, "Answers");
        mSwipeRefreshLayout = findViewById(R.id.swipe_refresh_layout);
        mRecyclerView = findViewById(R.id.recyclerView);
        callFireBase();
        fab = findViewById(R.id.submit_answer);
        answer = findViewById(R.id.editText);
        fab.setOnClickListener(v -> {
            if (answer.getText().toString().equals(""))
                showSnackBar(QuestionDetailActivity.this, "Enter valid answer");
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
            DatabaseReference ref = FirebaseDatabase.getInstance().getReference().child("Answers")
                    .child(current.getId());
            ref.child(getPushKey(ref)).setValue(ans).addOnCompleteListener(task -> {
                prevCallResolved = true;
                showProgressBar(false);
                if (task.isSuccessful()) {
                    answer.setText("");
                    mAdapter.refresh();
                    closeKeyboard();
                    showSnackBar(this, "Posted");
                } else {
                    showSnackBar(this, "Posting error");
                }
            });
            prevCallResolved = false;
        }
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        if (from)
            getMenuInflater().inflate(R.menu.delete_menu, menu);
        return super.onCreateOptionsMenu(menu);
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        int id = item.getItemId();
        if (id == R.id.action_delete && from) {
            verifyNCall();
        } else if (id == android.R.id.home) {
            finish();
        }
        return super.onOptionsItemSelected(item);
    }

    private void closeKeyboard() {
        View view = this.getCurrentFocus();
        if (view != null) {
            InputMethodManager imm = (InputMethodManager) getSystemService(Context.INPUT_METHOD_SERVICE);
            imm.hideSoftInputFromWindow(view.getWindowToken(), 0);
        }
    }

    private void verifyNCall() {
        if (!prevCallResolved || server == null) ;
        getToken(token -> server.deleteQuestion(token, current.getC_date(), current.getId()));
        prevCallResolved = false;
    }


}
