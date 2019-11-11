package com.esselion.pass.activities;

import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.esselion.pass.R;
import com.esselion.pass.Server;
import com.esselion.pass.holders.LocationHolder;
import com.esselion.pass.models.Location;
import com.esselion.pass.util.Cache;
import com.esselion.pass.util.SharedPrefAdapter;
import com.esselion.pass.util.Tools;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.Query;
import com.google.firebase.database.ValueEventListener;

import java.util.ArrayList;

import static com.esselion.pass.util.Tools.launchActivity;

public class LocationActivity extends BaseActivity implements LocationHolder.RecyclerViewClickListener {

    private RecyclerView mRecyclerView;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_location);
        Tools.initMinToolbar(this, "My Locations");
        findViewById(R.id.use_current).setOnClickListener(v -> {
            Cache.clearLocationPreferences();
            finish();
        });
        findViewById(R.id.save_location).setOnClickListener(v -> launchActivity(this, AddLocationActivity.class));
        mRecyclerView = findViewById(R.id.recyclerView);
        callFireBase();
    }

    //TODO change to recycler adater

    private void callFireBase() {
        mRecyclerView.setHasFixedSize(true);
        LinearLayoutManager mManager = new LinearLayoutManager(this);
        mRecyclerView.setLayoutManager(mManager);
        //Initialize Database
        ArrayList<Location> locations = new ArrayList<>();
        Query mQuery = FirebaseDatabase.getInstance().getReference().child("Locations/" + Cache.getUser().getUid());
        mQuery.addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
                for (DataSnapshot d : dataSnapshot.getChildren()) {
                    try {
                        locations.add(d.getValue(Location.class));
                    } catch (Exception e) {
                        e.printStackTrace();
                    }
                }
                RecyclerView.Adapter<LocationHolder> adapter = new RecyclerView.Adapter<LocationHolder>() {
                    @NonNull
                    @Override
                    public LocationHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
                        return new LocationHolder(LayoutInflater.from(parent.getContext()).inflate(R.layout.card_location, parent, false), LocationActivity.this);
                    }

                    @Override
                    public void onBindViewHolder(@NonNull LocationHolder holder, int position) {
                        holder.setItem(locations.get(position));
                    }

                    @Override
                    public int getItemCount() {
                        return locations.size();
                    }
                };
                mRecyclerView.setAdapter(adapter);
            }

            @Override
            public void onCancelled(@NonNull DatabaseError databaseError) {

            }
        });

    }


    @Override
    public void onClick(View view, Location location) {
        if (view.getId() == R.id.action_delete) {
            server.deleteLocation(Cache.getUser().getUid(), location.id);
        } else {
            SharedPrefAdapter adapter = SharedPrefAdapter.getInstance();
            adapter.setLocation(location);
            Cache.clearLocationCache();
            finish();
        }
    }

    @Override
    public void onServerCallSuccess(int methodId, String title) {
        super.onServerCallSuccess(methodId, title);
        if (methodId == Server.SERVER_DELETE_LOCATION) callFireBase();
    }
}
