package com.example.mytasker.fragments;

import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import androidx.fragment.app.Fragment;

import com.example.mytasker.R;
import com.example.mytasker.activities.SearchActivity;

import static com.example.mytasker.util.Tools.launchActivity;

public class PostTaskCat extends Fragment {

    public static String category;

    public PostTaskCat() {
        // Required empty public constructor
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.post_task_cat, container, false);
        view.findViewById(R.id.task_cat).setOnClickListener(this::click);
        view.findViewById(R.id.task_tag).setOnClickListener(this::click);
        view.findViewById(R.id.task_must_haves).setOnClickListener(this::click);
        return view;
    }

    private void click(View v) {
        launchActivity(getActivity(), SearchActivity.class);
    }


}