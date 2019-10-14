package com.esselion.pass.activities;

import android.content.Intent;
import android.graphics.drawable.Drawable;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.esselion.pass.R;
import com.esselion.pass.holders.QuestionHolder;
import com.esselion.pass.models.Question;
import com.esselion.pass.util.Contracts;
import com.esselion.pass.util.Tools;
import com.firebase.ui.database.FirebaseRecyclerAdapter;
import com.firebase.ui.database.FirebaseRecyclerOptions;
import com.google.firebase.database.Query;

import static com.esselion.pass.util.Cache.getDatabase;
import static com.esselion.pass.util.Cache.getUser;
import static com.esselion.pass.util.Tools.launchActivity;

public class HistoryQues extends BaseActivity implements QuestionHolder.RecyclerViewClickListener {


    private FirebaseRecyclerAdapter mAdapter;
    private RecyclerView mRecyclerView;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_list);
        Tools.initMinToolbar(this, "My Questions");
        mRecyclerView = findViewById(R.id.recyclerView);
        callFireBase();
    }

    private void callFireBase() {
        View anim = findViewById(R.id.anim);
        mRecyclerView.setHasFixedSize(true);
        LinearLayoutManager mManager = new LinearLayoutManager(this);
        mRecyclerView.setLayoutManager(mManager);
        Query mQuery = getDatabase().child("PrevQues").child(getUser().getUid());
        //Initialize

        FirebaseRecyclerOptions<Question> options =
                new FirebaseRecyclerOptions.Builder<Question>()
                        .setQuery(mQuery, Question.class).build();
        mAdapter = new FirebaseRecyclerAdapter<Question, QuestionHolder>(options) {
            @NonNull
            @Override
            public QuestionHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
                anim.setVisibility(View.GONE);
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
    public void onClick(View view, Question question) {
        Intent intent = new Intent(this, QuestionDetailActivity.class);
        intent.putExtra("ques", question);
        intent.putExtra("from", true);
        launchActivity(this, intent);
    }
}

