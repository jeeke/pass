package com.esselion.pass.activities;

import android.os.Bundle;

import com.esselion.pass.R;
import com.esselion.pass.util.Tools;

public class LocationActivity extends BaseActivity {

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_location);
        Tools.initMinToolbar(this, "My Locations");
        findViewById(R.id.use_current).setOnClickListener(v -> finish());
    }

}
