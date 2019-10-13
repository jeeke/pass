package com.esselion.pass.activities;

import android.content.Intent;
import android.net.Uri;
import android.os.Bundle;
import android.view.View;

import androidx.annotation.NonNull;
import androidx.appcompat.widget.Toolbar;

import com.esselion.pass.R;
import com.esselion.pass.chat.MessagesActivity;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;

import java.util.ArrayList;
import java.util.Random;

import static com.esselion.pass.util.Tools.finishNLaunchActivity;
import static com.esselion.pass.util.Tools.launchActivity;

public class ContactUs extends BaseActivity {

    ArrayList<String> supportIds = new ArrayList<>();
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        FirebaseDatabase.getInstance().getReference()
                .child("Support").addListenerForSingleValueEvent(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
                for (DataSnapshot d : dataSnapshot.getChildren()) {
                    supportIds.add(d.getValue(String.class));
                }
            }

            @Override
            public void onCancelled(@NonNull DatabaseError databaseError) {

            }
        });
        setContentView(R.layout.activity_contact_us);
        Toolbar toolbar = findViewById(R.id.toolbar);
        toolbar.setTitle("CONTACT US");
        setSupportActionBar(toolbar);
        getSupportActionBar().setDisplayHomeAsUpEnabled(true);
        findViewById(R.id.cardView6).setOnClickListener(this::callUs);
        findViewById(R.id.cardView7).setOnClickListener(v -> {
            Intent intent = new Intent(this, MessagesActivity.class);
            intent.putExtra("id", getSupportId());
            intent.putExtra("name", "Pass Support Team");
            finishNLaunchActivity(this, intent);
        });
    }

    private String getSupportId() {
        if (supportIds.size() > 1) {
            Random rand = new Random();
            return supportIds.get(rand.nextInt(supportIds.size()));
        } else return "akLR5tDZnZaT8aOJxTvmnfRXwDz2";
    }

    private void callUs(View view) {
        Intent intent = new Intent(Intent.ACTION_DIAL);
        intent.setData(Uri.parse("tel:+919679280831"));
        launchActivity(this, intent);
    }
}
