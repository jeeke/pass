package com.esselion.pass.activities;

import android.content.Intent;
import android.os.Bundle;

import androidx.fragment.app.Fragment;
import androidx.fragment.app.FragmentTransaction;

import com.esselion.pass.R;
import com.esselion.pass.fragments.ProfileFragment;

public class ProfileActivity extends BaseActivity implements ProfileFragment.ActivityListener {

    String uid, name, imageUrl;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_profile);
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
