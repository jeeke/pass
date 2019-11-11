package com.esselion.pass.activities;

import android.os.Bundle;

import com.esselion.pass.R;
import com.esselion.pass.util.Tools;

public class AddLocationActivity extends BaseActivity {

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_add_location);
        Tools.initMinToolbar(this, "My Locations");

    }
}
