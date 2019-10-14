package com.esselion.pass.activities;

import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.ViewGroup;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.esselion.pass.R;
import com.esselion.pass.holders.NotificationHolder;
import com.esselion.pass.models.Notification;
import com.esselion.pass.util.Tools;
import com.firebase.ui.database.FirebaseRecyclerAdapter;
import com.firebase.ui.database.FirebaseRecyclerOptions;
import com.google.firebase.database.Query;

import static com.esselion.pass.util.Cache.getDatabase;
import static com.esselion.pass.util.Cache.getUser;

public class NotificationActivity extends BaseActivity {


    private FirebaseRecyclerAdapter mAdapter;
    private RecyclerView mRecyclerView;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_list);
        Tools.initMinToolbar(this, "Notifications");
        mRecyclerView = findViewById(R.id.recyclerView);
        TextView textView = findViewById(R.id.empty_text);
        textView.setText("No Notifications Yet");
        callFireBase();
    }

    private void callFireBase() {
        mRecyclerView.setHasFixedSize(true);

        LinearLayoutManager mManager = new LinearLayoutManager(this);
        mRecyclerView.setLayoutManager(mManager);
        Query mQuery = getDatabase().child("/Notifications").child(getUser().getUid());
        //Initialize PagedList Configuration
        FirebaseRecyclerOptions<Notification> options =
                new FirebaseRecyclerOptions.Builder<Notification>()
                        .setQuery(mQuery, Notification.class).build();
        mAdapter = new FirebaseRecyclerAdapter<Notification, NotificationHolder>(options) {

            @NonNull
            @Override
            public NotificationHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
                return new NotificationHolder(LayoutInflater.from(parent.getContext()).inflate(R.layout.card_notification, parent, false));
            }

            @Override
            protected void onBindViewHolder(@NonNull NotificationHolder holder,
                                            int position,
                                            @NonNull Notification model) {
                holder.setItem(model);
            }

        };
        //Set Adapter to RecyclerView
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

}
