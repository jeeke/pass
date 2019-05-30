package com.example.mytasker;

import androidx.appcompat.app.AppCompatActivity;
import androidx.constraintlayout.widget.ConstraintLayout;
import androidx.core.view.ViewCompat;
import androidx.fragment.app.FragmentManager;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import android.graphics.drawable.AnimatedVectorDrawable;
import android.graphics.drawable.Drawable;
import android.graphics.drawable.GradientDrawable;
import android.os.Bundle;
import android.os.Handler;
import android.view.View;
import android.widget.TextView;
import java.util.ArrayList;

import com.example.mytasker.adapters.TaskListAdapter;
import com.example.mytasker.models.Task;
import com.facebook.shimmer.ShimmerFrameLayout;
import com.google.android.material.floatingactionbutton.FloatingActionButton;

public class DashboardActivity extends AppCompatActivity implements TaskListAdapter.RecyclerViewClickListener{

    TextView bhome, brun, btab, bmsg;
    AnimatedVectorDrawable bottomAppBar;
    FloatingActionButton fab;
    ShimmerFrameLayout shimmerContainer;

    @Override
    public void onClick(View view, int position) {
        FragmentManager fm = getSupportFragmentManager();
        androidx.fragment.app.FragmentTransaction ft = fm.beginTransaction();
        androidx.fragment.app.Fragment prev = fm.findFragmentByTag("dialog");
        if (prev != null) {
            ft.remove(prev);
        }
        ft.addToBackStack(null);

//        DialogFragment dialogFragment = new TaskDetailFragment();
//        dialogFragment.show(fm, "dialog");
    }


    @Override
    protected void onPause() {
        super.onPause();
        shimmerContainer.stopShimmer();
    }

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_dashboard);

        shimmerContainer = findViewById(R.id.shimmer_container);
        shimmerContainer.startShimmer();
        final RecyclerView listView = findViewById(R.id.ListView_dashboard);

        final Handler handler = new Handler();
        handler.postDelayed(new Runnable() {
            @Override
            public void run() {
                shimmerContainer.stopShimmer();
                shimmerContainer.animate().alpha(0.0f).setDuration(500).start();
                listView.animate().alpha(1.0f).setDuration(1000).start();
                listView.scrollToPosition(0);
            }
        }, 2000);
        ConstraintLayout layout = findViewById(R.id.root);
//        TODO
//        layout.setBackgroundColor(SettingActivity.toolbar);
        Drawable drawable = getDrawable(R.drawable.list_header);
        ArrayList<Task> list = new ArrayList();
        for (int i = 0; i < 2000; i++) {
            Task data1 = new Task("Want my House cleaned", "$500", "Jalandhar", "0.4 kms", "28 jan", R.drawable.fbsmall);
            list.add(data1);
        }

//        CustomListAdapter adapter = new CustomListAdapter(list, getApplicationContext());

        TaskListAdapter adapter = new TaskListAdapter(this,list);
        bhome = findViewById(R.id.textView9);
        brun = findViewById(R.id.textView10);
        btab = findViewById(R.id.textView11);
        bmsg = findViewById(R.id.textView12);
        bottomAppBar = (AnimatedVectorDrawable) findViewById(R.id.bottom_app_bar).getBackground();
//        bottomAppBar.setBackgroundTintList(ColorStateList.valueOf(SettingActivity.bottombar));
//        listView.setLayoutAnimation(AnimationUtils.loadLayoutAnimation(this,R.anim.slide_in_bottom));
        listView.setAdapter(adapter);
        listView.setLayoutManager(new LinearLayoutManager(this));
//        listView.setBackgroundColor(SettingActivity.list);
        fab = findViewById(R.id.fab);
//        fab.setBackgroundTintList(ColorStateList.valueOf(SettingActivity.fab));
        ConstraintLayout listHead = findViewById(R.id.list_head);
        GradientDrawable shape = new GradientDrawable();
        shape.setShape(GradientDrawable.RECTANGLE);
        shape.setCornerRadii(new float[]{320, 320, 320, 320, 64, 64, 64, 64});
//        shape.setColor(SettingActivity.listHead);
        listHead.setBackground(shape);

    }

//    TODO
//    public void profile(View v) {
//        Intent intent = new Intent(DashboardActivity.this, ProfileActivity.class);
//        startActivity(intent);
//        overridePendingTransition(R.anim.slide_from_right, R.anim.slide_to_left);
//    }

//    public void setting(View v) {
//        Intent intent = new Intent(DashboardActivity.this, SettingActivity.class);
//        startActivity(intent);
//        overridePendingTransition(R.anim.slide_from_right, R.anim.slide_to_left);
//    }
//
//    public void support(View v)
//    {
//        Intent intent = new Intent(DashboardActivity.this,Support_activity.class);
//        startActivity(intent);
//        overridePendingTransition(R.anim.slide_from_right, R.anim.slide_to_left);
//    }

    public void post(View v) {
//        fab.setEnabled(false);
        ViewCompat.animate(fab).yBy(-120).setDuration(330).start();
        bottomAppBar.start();
        final Handler handler = new Handler();
        handler.postDelayed(new Runnable() {
            @Override
            public void run() {
                ViewCompat.animate(fab).yBy(120).setDuration(330).start();
                handler.postDelayed(new Runnable() {
                    @Override
                    public void run() {
//                        Intent intent = new Intent(DashboardActivity.this, CreateTask.class);
//                        startActivityForResult(intent, 100);
                        overridePendingTransition(R.anim.slide_from_right, R.anim.slide_to_left);
                    }
                }, 350);
            }
        }, 350);
    }
    public void homeselected(View v) {
        bhome.setCompoundDrawablesWithIntrinsicBounds(0, R.mipmap.ic_home_black_24dp, 0, 0);
        brun.setCompoundDrawablesWithIntrinsicBounds(0, R.mipmap.ic_directions_run_before_24dp, 0, 0);
        bmsg.setCompoundDrawablesWithIntrinsicBounds(0, R.mipmap.ic_chat_before_24dp, 0, 0);
        btab.setCompoundDrawablesWithIntrinsicBounds(0, R.mipmap.ic_tap_and_play_before_24dp, 0, 0);
    }

    public void msgselected(View v) {
        bhome.setCompoundDrawablesWithIntrinsicBounds(0, R.mipmap.ic_home_before_24dp, 0, 0);
        brun.setCompoundDrawablesWithIntrinsicBounds(0, R.mipmap.ic_directions_run_before_24dp, 0, 0);
        bmsg.setCompoundDrawablesWithIntrinsicBounds(0, R.mipmap.ic_chat_black_24dp, 0, 0);
        btab.setCompoundDrawablesWithIntrinsicBounds(0, R.mipmap.ic_tap_and_play_before_24dp, 0, 0);
    }

    public void runselected(View v) {
        bhome.setCompoundDrawablesWithIntrinsicBounds(0, R.mipmap.ic_home_before_24dp, 0, 0);
        brun.setCompoundDrawablesWithIntrinsicBounds(0, R.mipmap.ic_directions_run_black_24dp, 0, 0);
        bmsg.setCompoundDrawablesWithIntrinsicBounds(0, R.mipmap.ic_chat_before_24dp, 0, 0);
        btab.setCompoundDrawablesWithIntrinsicBounds(0, R.mipmap.ic_tap_and_play_before_24dp, 0, 0);
    }

    public void tabbselected(View v) {
        bhome.setCompoundDrawablesWithIntrinsicBounds(0, R.mipmap.ic_home_before_24dp, 0, 0);
        brun.setCompoundDrawablesWithIntrinsicBounds(0, R.mipmap.ic_directions_run_before_24dp, 0, 0);
        bmsg.setCompoundDrawablesWithIntrinsicBounds(0, R.mipmap.ic_chat_before_24dp, 0, 0);
        btab.setCompoundDrawablesWithIntrinsicBounds(0, R.mipmap.ic_tap_and_play_black_24dp, 0, 0);
//        Intent intent = new Intent(this, TappActivity.class);
//        startActivity(intent);
        overridePendingTransition(R.anim.slide_from_right, R.anim.slide_to_left);
    }


}
