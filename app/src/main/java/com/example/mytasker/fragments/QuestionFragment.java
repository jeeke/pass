package com.example.mytasker.fragments;

import android.content.Context;
import android.os.Bundle;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import androidx.constraintlayout.motion.widget.MotionLayout;
import androidx.fragment.app.Fragment;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;
import androidx.swiperefreshlayout.widget.SwipeRefreshLayout;

import com.example.mytasker.R;
import com.example.mytasker.adapters.QuestionAdapter;
import com.example.mytasker.models.Question;
import com.example.mytasker.retrofit.JsonPlaceHolder;
import com.example.mytasker.retrofit.QuestionList;
import com.example.mytasker.util.FilterHelper;
import com.example.mytasker.util.NetworkCache;
import com.example.mytasker.util.ToolbarHelper;
import com.facebook.shimmer.ShimmerFrameLayout;

import java.util.ArrayList;

import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;
import retrofit2.Retrofit;
import retrofit2.converter.gson.GsonConverterFactory;

public class QuestionFragment extends Fragment {

    private OnListFragmentInteractionListener mListener;
    private ShimmerFrameLayout shimmerContainer;
    private FilterHelper filterHelper;
    private ToolbarHelper toolbarHelper;

    public QuestionFragment() {
    }


    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
    }


    private void callRetrofit() {
        shimmerContainer.startShimmer();
        Retrofit retrofit = new Retrofit.Builder()
                .baseUrl("https://nkliobv7w5.execute-api.ap-south-1.amazonaws.com/dev/")
//                .baseUrl("http://fc3b9623.ngrok.io")
                .addConverterFactory(GsonConverterFactory.create())
                .build();

        JsonPlaceHolder jsonPlaceHolder = retrofit.create(JsonPlaceHolder.class);
        Call<QuestionList> call = jsonPlaceHolder.getQuestions(
                new double[]{25.0, 25.0},
                100,
                new String[]{"tech", "null"}
        );

        call.enqueue(new Callback<QuestionList>() {
            @Override
            public void onResponse(Call<QuestionList> call, Response<QuestionList> response) {
                if (!response.isSuccessful()) {
                    Log.e("Code: ", response.toString());
                    return;
                }
                QuestionList details = response.body();

                if (details != null) {
                    NetworkCache.questions = details.getQuestions();
                }
                adapter.update(NetworkCache.questions);

                shimmerContainer.stopShimmer();
                shimmerContainer.animate().alpha(0.0f).setDuration(500).start();
                listView.animate().alpha(1.0f).setDuration(1000).start();
                swipeContainer.setRefreshing(false);
            }

            @Override
            public void onFailure(Call<QuestionList> call, Throwable t) {
                Log.e("error ", t.getMessage());
                swipeContainer.setRefreshing(false);
            }
        });
    }

    private void callRetrofitHelper() {
        if(NetworkCache.questions!=null) {
            shimmerContainer.setVisibility(View.GONE);
            adapter.update(NetworkCache.questions);
            listView.setAlpha(1.0f);
        }else {
            callRetrofit();
        }
    }

    private RecyclerView listView;
    private SwipeRefreshLayout swipeContainer;
    private QuestionAdapter adapter;

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        View v = inflater.inflate(R.layout.fragment_question_list, container, false);
        toolbarHelper = new ToolbarHelper((MotionLayout)v);
        filterHelper = new FilterHelper((MotionLayout) v);
        adapter = new QuestionAdapter(getContext(), new ArrayList<>());
        initViews(v);
        initListeners(v);
        listView.setAdapter(adapter);
        listView.setLayoutManager(new LinearLayoutManager(getContext()));
        callRetrofitHelper();
        swipeContainer.setColorSchemeResources(
                android.R.color.holo_red_light,

                android.R.color.holo_orange_light,

                android.R.color.holo_blue_bright,

                android.R.color.holo_green_light);
        return v;
    }

    private void initViews(View v) {
        listView = v.findViewById(R.id.ListView_dashboard);
        shimmerContainer = v.findViewById(R.id.shimmer_container);
        swipeContainer = v.findViewById(R.id.swipe_refresh_layout);
    }

    private void initListeners(View v) {
        swipeContainer.setOnRefreshListener(this::callRetrofit);
    }

    @Override
    public void onAttach(Context context) {
        super.onAttach(context);
        if (context instanceof OnListFragmentInteractionListener) {
            mListener = (OnListFragmentInteractionListener) context;
        } else {
            throw new RuntimeException(context.toString()
                    + " must implement OnListFragmentInteractionListener");
        }
    }

    @Override
    public void onDetach() {
        super.onDetach();
        mListener = null;
        shimmerContainer.stopShimmer();
    }

    public interface OnListFragmentInteractionListener {
        // TODO: Update argument type and name
        void onListFragmentInteraction(Question question);
    }
}
