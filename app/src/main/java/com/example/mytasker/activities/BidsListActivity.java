package com.example.mytasker.activities;

import android.app.ProgressDialog;
import android.os.Bundle;
import android.util.Log;
import android.view.View;

import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.example.mytasker.R;
import com.example.mytasker.adapters.BidsAdapter;
import com.example.mytasker.models.Bid;
import com.example.mytasker.retrofit.JsonPlaceHolder;
import com.example.mytasker.util.Contracts;
import com.example.mytasker.util.Tools;

import java.util.ArrayList;

import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;
import retrofit2.Retrofit;
import retrofit2.converter.gson.GsonConverterFactory;

public class BidsListActivity extends BaseActivity implements BidsAdapter.RecyclerViewClickListener {

    BidsAdapter adapter;
    
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_bid_list);
        Tools.initMinToolbar(this,"ALL BIDS",false);
        RecyclerView listView = findViewById(R.id.recyclerView);
        adapter = new BidsAdapter(this,new ArrayList<>());
        listView.setAdapter(adapter);
        listView.setLayoutManager(new LinearLayoutManager(this));
        callRetrofit();
    }

    private void callRetrofit() {
        ProgressDialog dlg = new ProgressDialog(this);
        dlg.setTitle("Getting Bids, Please Wait....");
        dlg.show();
        Retrofit retrofit = new Retrofit.Builder()
                .baseUrl(Contracts.BASE_GET_URL)
                .addConverterFactory(GsonConverterFactory.create())
                .build();

        JsonPlaceHolder jsonPlaceHolder = retrofit.create(JsonPlaceHolder.class);
        Call<ArrayList<Bid>> call = jsonPlaceHolder.getBids("rakesh");
        call.enqueue(new Callback<ArrayList<Bid>>() {
            @Override
            public void onResponse(Call<ArrayList<Bid>> call, Response<ArrayList<Bid>> response) {
                dlg.dismiss();
                if (!response.isSuccessful()) {
                    Log.e("Code: ", response.toString());
                    return;
                }
                adapter.update(response.body());
            }

            @Override
            public void onFailure(Call<ArrayList<Bid>> call, Throwable t) {
                Log.e("error ", t.getMessage());
                dlg.dismiss();
            }
        });
    }

    @Override
    public void onClick(View view, int position) {

    }
}
