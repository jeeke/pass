package com.example.mytasker.activities;

import android.content.Intent;
import android.graphics.drawable.AnimatedVectorDrawable;
import android.graphics.drawable.GradientDrawable;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.ImageView;

import androidx.appcompat.app.AppCompatActivity;
import androidx.constraintlayout.widget.ConstraintLayout;
import androidx.core.content.ContextCompat;
import androidx.core.graphics.drawable.DrawableCompat;
import androidx.fragment.app.FragmentManager;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.example.mytasker.R;
import com.example.mytasker.adapters.TaskListAdapter;
import com.example.mytasker.retrofit.JsonPlaceHolder;
import com.example.mytasker.retrofit.TaskDetail;
import com.facebook.shimmer.ShimmerFrameLayout;
import com.google.android.material.appbar.AppBarLayout;
import com.google.android.material.floatingactionbutton.FloatingActionButton;

import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;
import retrofit2.Retrofit;
import retrofit2.converter.gson.GsonConverterFactory;

public class DashboardActivity extends AppCompatActivity implements TaskListAdapter.RecyclerViewClickListener{

    ImageView bhome, brun, btab, bmsg;
    ImageView prevbselection;
    AnimatedVectorDrawable bottomAppBar;
    FloatingActionButton fab;
    ShimmerFrameLayout shimmerContainer;
    AppBarLayout appBarLayout;
    boolean appBarExpanded;

    public void toogleExpansion(View v){
        appBarExpanded = !appBarExpanded;
        appBarLayout.setExpanded(appBarExpanded);
    }

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

//        final Handler handler = new Handler();
//        handler.postDelayed(new Runnable() {
//            @Override
//            public void run() {
//                shimmerContainer.stopShimmer();
//                shimmerContainer.animate().alpha(0.0f).setDuration(500).start();
//                listView.animate().alpha(1.0f).setDuration(1000).start();
//                listView.scrollToPosition(0);
//            }
//        }, 2000);
        ConstraintLayout layout = findViewById(R.id.root);
//        TODO
//        layout.setBackgroundColor(SettingActivity.toolbar);
        
        Retrofit retrofit = new Retrofit.Builder()
                .baseUrl("https://nkliobv7w5.execute-api.ap-south-1.amazonaws.com/dev/")
                .addConverterFactory(GsonConverterFactory.create())
                .build();

        JsonPlaceHolder jsonPlaceHolder = retrofit.create(JsonPlaceHolder.class);
        Call<TaskDetail> call = jsonPlaceHolder.getDetail(
                new Integer[]{25,25},
                100,
                new String[]{"tech","null"}
        );

        call.enqueue(new Callback<TaskDetail>() {
            @Override
            public void onResponse(Call<TaskDetail> call, Response<TaskDetail> response) {
                if (!response.isSuccessful()) {
                    Log.v("Code: " , String.valueOf(response.code()));
                    return;
                }

                TaskDetail details = response.body();
                TaskListAdapter adapter = new TaskListAdapter(DashboardActivity.this,details.getTasks());
                shimmerContainer.stopShimmer();
                shimmerContainer.animate().alpha(0.0f).setDuration(500).start();
                listView.setAdapter(adapter);
                listView.setLayoutManager(new LinearLayoutManager(DashboardActivity.this));
                listView.scrollToPosition(0);
                listView.animate().alpha(1.0f).setDuration(1000).start();
            }

            @Override
            public void onFailure(Call<TaskDetail> call, Throwable t) {
                Log.v("error ",t.getMessage());
            }
        });

//        CustomListAdapter adapter = new CustomListAdapter(list, getApplicationContext());

        bhome = findViewById(R.id.textView9);
        brun = findViewById(R.id.textView10);
        btab = findViewById(R.id.textView11);
        bmsg = findViewById(R.id.textView12);
        appBarExpanded = false;
        appBarLayout = findViewById(R.id.app_bar);
        prevbselection = bhome;
//        listView.setBackgroundColor(SettingActivity.list);
        fab = findViewById(R.id.fab);
        ConstraintLayout listHead = findViewById(R.id.list_head);
        GradientDrawable shape = new GradientDrawable();
        shape.setShape(GradientDrawable.RECTANGLE);
        shape.setCornerRadii(new float[]{320, 320, 320, 320, 64, 64, 64, 64});
        shape.setColor(getResources().getColor(R.color.colorPrimary));
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
//        ViewCompat.animate(fab).yBy(-120).setDuration(330).start();
//        bottomAppBar.start();
//        final Handler handler = new Handler();
//        handler.postDelayed(new Runnable() {
//            @Override
//            public void run() {
//                ViewCompat.animate(fab).yBy(120).setDuration(330).start();
//                handler.postDelayed(new Runnable() {
//                    @Override
//                    public void run() {
//                        Intent intent = new Intent(DashboardActivity.this, CreateTask.class);
//                        startActivityForResult(intent, 100);
//                        overridePendingTransition(R.anim.slide_from_right, R.anim.slide_to_left);
//                    }
//                }, 350);
//            }
//        }, 350);

                        Intent intent = new Intent(DashboardActivity.this, PostActivity.class);
                        startActivity(intent);
    }

    public void bselected(View v) {
        ImageView current = (ImageView) v ;
        DrawableCompat.setTint(prevbselection.getDrawable(), ContextCompat.getColor(this, R.color.colorAppbarBUnselected));
        DrawableCompat.setTint(current.getDrawable(), ContextCompat.getColor(this, R.color.colorPrimary));
        prevbselection = current;
    }
//        overridePendingTransition(R.anim.slide_from_right, R.anim.slide_to_left);


}
