package com.example.mytasker.activities;

import android.app.ProgressDialog;
import android.os.Bundle;
import android.util.Log;
import android.view.View;

import androidx.appcompat.widget.Toolbar;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;
import androidx.swiperefreshlayout.widget.SwipeRefreshLayout;

import com.example.mytasker.R;
import com.example.mytasker.adapters.NotificationAdapter;
import com.example.mytasker.retrofit.JsonPlaceHolder;
import com.example.mytasker.retrofit.NotificationList;
import com.example.mytasker.util.Contracts;

import java.util.ArrayList;

import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;
import retrofit2.Retrofit;
import retrofit2.converter.gson.GsonConverterFactory;

public class NotificationActivity extends BaseActivity implements NotificationAdapter.RecyclerViewClickListener {

    ProgressDialog dlg;
    RecyclerView listView;
    NotificationAdapter adapter;
    SwipeRefreshLayout swipeContainer;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_notification);
        Toolbar toolbar = findViewById(R.id.toolbar);
        toolbar.setTitle("Notifications");
        setSupportActionBar(toolbar);
        getSupportActionBar().setDisplayHomeAsUpEnabled(true);


        swipeContainer = findViewById(R.id.swipe_refresh_layout);
        listView = findViewById(R.id.list_notification);

        dlg = new ProgressDialog(this);
        dlg.setTitle("Fetching notifications, Please Wait....");
        adapter = new NotificationAdapter(this, new ArrayList<>());
        listView.setAdapter(adapter);
        listView.setLayoutManager(new LinearLayoutManager(this));
        swipeContainer.setOnRefreshListener(this::callRetrofit);
        callRetrofit();
    }



    private void callRetrofit() {
//        dlg.show();
        Retrofit retrofit = new Retrofit.Builder()
                .baseUrl(Contracts.BASE_GET_URL)
                .addConverterFactory(GsonConverterFactory.create())
                .build();

        JsonPlaceHolder jsonPlaceHolder = retrofit.create(JsonPlaceHolder.class);
        Call<NotificationList> call = jsonPlaceHolder.getNotifications("Rakesh Pandey");

        call.enqueue(new Callback<NotificationList>() {


            @Override
            public void onResponse(Call<NotificationList> call, Response<NotificationList> response) {
                if (!response.isSuccessful()) {
                    Log.v("Code: ", String.valueOf(response.code()));
                    return;
                }
                NotificationList details = response.body();


                if (details != null) {
                    adapter.update(details.getNotifications());
                }

                dlg.dismiss();
//                listView.animate().alpha(1.0f).setDuration(1000).start();
                swipeContainer.setRefreshing(false);
            }

            @Override
            public void onFailure(Call<NotificationList> call, Throwable t) {
                Log.e("error ", t.getMessage());
                dlg.dismiss();
                swipeContainer.setRefreshing(false);
            }
        });
    }

    @Override
    public void onClick(View view, int position) {
    }
}
