package com.example.mytasker.util;

import android.view.View;

import androidx.constraintlayout.motion.widget.MotionLayout;

import com.example.mytasker.R;

public class ToolbarHelper {
    private MotionLayout toolbar;
    private boolean open;
    public ToolbarHelper(MotionLayout v){
        toolbar = v;
        open = false;
        v.findViewById(R.id.menu_icon).setOnClickListener(this::toggleToolBar);
        v.findViewById(R.id.settings).setOnClickListener(this::setting);
        v.findViewById(R.id.support).setOnClickListener(this::support);
        v.findViewById(R.id.notify).setOnClickListener(this::profile);
    }

    private void toggleToolBar(View v){
        if(open){
            toolbar.transitionToStart();
        }else{
            toolbar.transitionToEnd();
        }
        open = !open;
    }

    private void profile(View v) {
//        Intent intent = new Intent(DashboardActivity.this, ProfileActivity.class);
//        startActivity(intent);
//        overridePendingTransition(R.anim.slide_from_right, R.anim.slide_to_left);
    }

    private void setting(View v) {
//        Intent intent = new Intent(getActivity(), SettingActivity.class);
//        startActivityForResult(intent, CODE_SETTINGS_ACTIVITY);
//        overridePendingTransition(R.anim.slide_from_right, R.anim.slide_to_left);
    }

    private void support(View v) {
//        Intent intent = new Intent(DashboardActivity.this,Support_activity.class);
//        startActivity(intent);
//        overridePendingTransition(R.anim.slide_from_right, R.anim.slide_to_left);
    }
}
