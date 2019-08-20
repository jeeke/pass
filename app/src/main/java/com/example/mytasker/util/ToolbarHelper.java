package com.example.mytasker.util;

import android.app.Activity;
import android.view.View;

import androidx.constraintlayout.motion.widget.MotionLayout;

import com.example.mytasker.R;
import com.example.mytasker.activities.ContactUs;
import com.example.mytasker.chat.DialogsActivity;

import static com.example.mytasker.util.Tools.launchActivity;

public class ToolbarHelper {
    private MotionLayout toolbar;
    private boolean open;

    public ToolbarHelper(Activity context, MotionLayout v,Class className){
        toolbar = v;
        open = false;
        v.findViewById(R.id.action_search).setOnClickListener((View view) -> launchActivity(context, className));
        v.findViewById(R.id.menu_icon).setOnClickListener(this::toggleToolBar);
        v.findViewById(R.id.chats).setOnClickListener((View view) -> launchActivity(context, DialogsActivity.class));
        v.findViewById(R.id.support).setOnClickListener((View view) -> launchActivity(context, ContactUs.class));
//        v.findViewById(R.id.my_posts).setOnClickListener((View view) -> launchActivity(context, HistoryTask.class));
    }

    private void toggleToolBar(View v){
        if(open){
            toolbar.transitionToStart();
        }else{
            toolbar.transitionToEnd();
        }
        open = !open;
    }
}
