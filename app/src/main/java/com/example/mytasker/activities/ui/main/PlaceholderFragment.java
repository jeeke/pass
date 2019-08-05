package com.example.mytasker.activities.ui.main;

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
import com.example.mytasker.models.PrevTaskModel;
import com.example.mytasker.retrofit.JsonPlaceHolder;
import com.example.mytasker.util.Contracts;

import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;
import retrofit2.Retrofit;
import retrofit2.converter.gson.GsonConverterFactory;


/**
 * A placeholder fragment containing a simple view.
 */
public class PlaceholderFragment extends Fragment {
    private static int index;
    private RecyclerView recyclerView;
    public static PlaceholderFragment newInstance(int index) {
        PlaceholderFragment fragment = new PlaceholderFragment();
        PlaceholderFragment.index = index;
        return fragment;
    }

    @Override
    public View onCreateView(
            @NonNull LayoutInflater inflater, ViewGroup container,
            Bundle savedInstanceState) {
        View root = inflater.inflate(R.layout.fragment_prev_posts, container, false);
        recyclerView = root.findViewById(R.id.recyclerView);
        return root;
    }

    void setupapi(int index) {
        Retrofit retrofit = new Retrofit.Builder()
                .baseUrl(Contracts.BASE_GET_URL)
//                .client(okHttpClient)
                .addConverterFactory(GsonConverterFactory.create())
                .build();

        JsonPlaceHolder jsonPlaceHolder = retrofit.create(JsonPlaceHolder.class);
        Call<PrevTaskModel> call = jsonPlaceHolder.getPrevTask("rakesh");

        call.enqueue(new Callback<PrevTaskModel>() {
            @Override
            public void onResponse(Call<PrevTaskModel> call, Response<PrevTaskModel> response) {
                if (!response.isSuccessful()) {
                    Log.v("Code: ", String.valueOf(response.code()));
                    return;
                }
                PrevTaskModel details = response.body();
                PrevPostAdapter adapter = new PrevPostAdapter(details, index);
                RecyclerView.LayoutManager layoutManager = new LinearLayoutManager(getContext());
                recyclerView.setLayoutManager(layoutManager);
                recyclerView.setAdapter(adapter);

//                if (details != null) {
//                    NetworkCache.tasks = details.getTasks();
//                }

            }

            @Override
            public void onFailure(Call<PrevTaskModel> call, Throwable t) {
                Log.e("error ", t.getMessage());
            }
        });
    }
}