package com.example.mytasker.activities;

import android.os.Bundle;

import androidx.fragment.app.Fragment;
import androidx.fragment.app.FragmentTransaction;

import com.example.mytasker.R;
import com.example.mytasker.fragments.ProfileFragment;
import com.example.mytasker.util.Tools;

public class ProfileActivity extends BaseActivity  {

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_profile);
        Tools.initMinToolbar(this,"Profile",false);
        loadFragment(new ProfileFragment(false));
    }

    private void loadFragment(Fragment fragment) {
        FragmentTransaction transaction = getSupportFragmentManager().beginTransaction();
        transaction.replace(R.id.frameLayout, fragment);
        transaction.commit();
    }
}
