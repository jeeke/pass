package com.example.mytasker.activities;

import android.content.Intent;
import android.os.Bundle;

import androidx.fragment.app.Fragment;
import androidx.fragment.app.FragmentTransaction;

import com.example.mytasker.R;
import com.example.mytasker.fragments.ProfileFragment;
import com.example.mytasker.util.Tools;

public class ProfileActivity extends BaseActivity implements ProfileFragment.ActivityListener {

    String uid, name, imageUrl;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_profile);
        Tools.initMinToolbar(this, "Profile");
        Intent intent = getIntent();
        uid = intent.getStringExtra("id");
        name = intent.getStringExtra("name");
        imageUrl = intent.getStringExtra("avatar");
        loadFragment(new ProfileFragment());
    }

    private void loadFragment(Fragment fragment) {
        FragmentTransaction transaction = getSupportFragmentManager().beginTransaction();
        transaction.replace(R.id.frameLayout, fragment);
        transaction.commit();
    }

    @Override
    public boolean getMine() {
        return false;
    }

    @Override
    public String getUId() {
        return uid;
    }

    @Override
    public String getUName() {
        return name;
    }

    @Override
    public String getImageUrl() {
        return imageUrl;
    }
}
