package com.example.mytasker.activities;

import android.app.ProgressDialog;
import android.content.Intent;
import android.os.Bundle;
import android.util.Log;
import android.view.View;

import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.example.mytasker.R;
import com.example.mytasker.adapters.QuestionAdapter;
import com.example.mytasker.retrofit.JsonPlaceHolder;
import com.example.mytasker.retrofit.RetrofitFeedHelper;
import com.example.mytasker.util.Contracts;
import com.example.mytasker.util.Tools;

import java.util.ArrayList;

import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;
import retrofit2.Retrofit;
import retrofit2.converter.gson.GsonConverterFactory;

public class HistoryQues extends BaseActivity implements QuestionAdapter.RecyclerViewClickListener{


    private QuestionAdapter adapter;
    ProgressDialog dlg;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_history_ques);
        Tools.initMinToolbar(this, "My Questions", false);
        RecyclerView listView = findViewById(R.id.recyclerView);
        adapter = new QuestionAdapter(this, this,new ArrayList<>(),true);
        listView.setAdapter(adapter);
        listView.setLayoutManager(new LinearLayoutManager(this));
        dlg = new ProgressDialog(this);
        dlg.setTitle("Getting Your Questions, Please Wait....");
        dlg.show();
        callRetrofit();
    }

    private void callRetrofit() {
        Retrofit retrofit = new Retrofit.Builder()
                .baseUrl(Contracts.BASE_GET_URL)
                .addConverterFactory(GsonConverterFactory.create())
                .build();

        JsonPlaceHolder jsonPlaceHolder = retrofit.create(JsonPlaceHolder.class);
//        Call<RetrofitFeedHelper> call = jsonPlaceHolder.historyQues("rakesh");
        Call<RetrofitFeedHelper> call = jsonPlaceHolder.getQuestions(new double[]{25.0, 25.0});
        call.enqueue(new Callback<RetrofitFeedHelper>() {
            @Override
            public void onResponse(Call<RetrofitFeedHelper> call, Response<RetrofitFeedHelper> response) {
                dlg.dismiss();
                if (!response.isSuccessful()) {
                    Log.e("Code: ", response.toString());
                    return;
                }
                adapter.update(response.body().toQuesList());
            }

            @Override
            public void onFailure(Call<RetrofitFeedHelper> call, Throwable t) {
                Log.e("error ", t.getMessage());
                dlg.dismiss();
            }
        });
    }

    @Override
    public void onClick(View view, int position) {
        QuestionDetailActivity.FROM = true;
        Intent intent = new Intent(this,QuestionDetailActivity.class);
        intent.putExtra("position",position);
        startActivity(intent);
    }
}
