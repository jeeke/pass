package com.example.mytasker.activities.ui.main;

import android.app.ProgressDialog;
import android.os.Bundle;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import androidx.annotation.NonNull;
import androidx.fragment.app.Fragment;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.example.mytasker.R;
import com.example.mytasker.models.PrevPostModel;
import com.example.mytasker.retrofit.JsonPlaceHolder;
import com.example.mytasker.util.Contracts;
import com.google.android.material.tabs.TabLayout;

import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;
import retrofit2.Retrofit;
import retrofit2.converter.gson.GsonConverterFactory;


/**
 * A placeholder fragment containing a simple view.
 */
public class PlaceholderFragment extends Fragment {
    private int indexTop;
    ProgressDialog dialog;
    private int indexBot;
    private RecyclerView recyclerView;


    PlaceholderFragment(int indexTop) {
//        Log.e("Constructor",indexTop + "");
        this.indexTop = indexTop;
    }

    @Override
    public View onCreateView(
            @NonNull LayoutInflater inflater, ViewGroup container,
            Bundle savedInstanceState) {
        View root = inflater.inflate(R.layout.fragment_prev_posts, container, false);
        recyclerView = root.findViewById(R.id.recyclerView);
        TabLayout tabLayout = root.findViewById(R.id.tabLayoutInner);
        dialog = new ProgressDialog(getContext());
        dialog.setTitle("Fetching List, Please Wait....");
        tabLayout.addOnTabSelectedListener(new TabLayout.OnTabSelectedListener() {
            @Override
            public void onTabSelected(TabLayout.Tab tab) {
                indexBot = tab.getPosition();
            }

            @Override
            public void onTabUnselected(TabLayout.Tab tab) {

            }

            @Override
            public void onTabReselected(TabLayout.Tab tab) {

            }
        });
        dialog.show();
        setupapi();
        return root;
    }

    void setupapi() {
        Retrofit retrofit = new Retrofit.Builder()
                .baseUrl(Contracts.BASE_GET_URL)
//                .client(okHttpClient)
                .addConverterFactory(GsonConverterFactory.create())
                .build();

        JsonPlaceHolder jsonPlaceHolder = retrofit.create(JsonPlaceHolder.class);
        Call<PrevPostModel> call = jsonPlaceHolder.getPrevTask("rakesh",indexTop);

        call.enqueue(new Callback<PrevPostModel>() {
            @Override
            public void onResponse(Call<PrevPostModel> call, Response<PrevPostModel> response) {

                dialog.dismiss();
                if (!response.isSuccessful()) {
                    Log.v("Code: ", String.valueOf(response.code()));
                    return;
                }
                PrevPostModel details = response.body();
                PrevPostAdapter adapter = new PrevPostAdapter(details, indexTop,indexBot);
                RecyclerView.LayoutManager layoutManager = new LinearLayoutManager(getContext());
                recyclerView.setLayoutManager(layoutManager);
                recyclerView.setAdapter(adapter);

//                if (details != null) {
//                    NetworkCache.tasks = details.getTasks();
//                }

            }

            @Override
            public void onFailure(Call<PrevPostModel> call, Throwable t) {
                Log.e("error ", t.getMessage());
                dialog.dismiss();
            }
        });
    }
}