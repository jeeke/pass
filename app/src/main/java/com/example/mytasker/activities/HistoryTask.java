package com.example.mytasker.activities;

import android.app.ProgressDialog;
import android.content.Intent;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.Toast;

import androidx.viewpager.widget.ViewPager;

import com.example.mytasker.R;
import com.example.mytasker.adapters.HistoryTaskAdapter;
import com.example.mytasker.adapters.TaskListAdapter;
import com.example.mytasker.models.PrevPostModel;
import com.example.mytasker.retrofit.JsonPlaceHolder;
import com.example.mytasker.util.NetworkCache;
import com.example.mytasker.util.Tools;
import com.google.android.material.tabs.TabLayout;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;

import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;
import retrofit2.Retrofit;

import static com.example.mytasker.util.Tools.getRetrofit;

public class HistoryTask extends BaseActivity implements TaskListAdapter.RecyclerViewClickListener {

    TabLayout tabs;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_history_task);
        Tools.initMinToolbar(this, "My Tasks", true);
        verifyNCall();
    }

    private void verifyNCall() {
        FirebaseUser mUser = FirebaseAuth.getInstance().getCurrentUser();
        mUser.getIdToken(true)
                .addOnCompleteListener(task -> {
                    if (task.isSuccessful()) {
                        setupapi(task.getResult().getToken());
                    } else {
                        // Handle error -> task.getException();
                        Toast.makeText(HistoryTask.this, "Authentication Error!", Toast.LENGTH_SHORT).show();
                    }
                });
    }

    void setupapi(String token) {
        ProgressDialog dialog = new ProgressDialog(this);
        dialog.setTitle("Getting Your Previous Tasks, Please Wait....");
        dialog.show();
        Retrofit retrofit = getRetrofit(token);

        JsonPlaceHolder jsonPlaceHolder = retrofit.create(JsonPlaceHolder.class);
        Call<PrevPostModel> call = jsonPlaceHolder.getPrevTask();

        call.enqueue(new Callback<PrevPostModel>() {
            @Override
            public void onResponse(Call<PrevPostModel> call, Response<PrevPostModel> response) {
                dialog.dismiss();
                if (!response.isSuccessful()) {
                    Log.v("Code: ", String.valueOf(response.code()));
                    return;
                }
                NetworkCache.prevPostModel = response.body();
                HistoryTaskAdapter sectionsPagerAdapter = new HistoryTaskAdapter(HistoryTask.this, getSupportFragmentManager());
                ViewPager viewPager = findViewById(R.id.view_pager);
                viewPager.setAdapter(sectionsPagerAdapter);
                tabs = findViewById(R.id.tabs);
                tabs.setVisibility(View.VISIBLE);
                tabs.setupWithViewPager(viewPager);
            }

            @Override
            public void onFailure(Call<PrevPostModel> call, Throwable t) {
                Log.e("error ", t.getMessage());
                dialog.dismiss();
            }
        });
    }

    @Override
    public void onClick(View view, int position) {
        TaskDetailActivity.FROM = tabs.getSelectedTabPosition() + 1;
        Intent intent = new Intent(this,TaskDetailActivity.class);
        intent.putExtra("position",position);
        startActivity(intent);
    }
}