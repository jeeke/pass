package com.example.mytasker.fragments;


import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.EditText;

import androidx.fragment.app.Fragment;

import com.example.mytasker.R;


/**
 * A simple {@link Fragment} subclass.
 */
public class PostTaskDetail extends Fragment {
    public static EditText title,description;
    public PostTaskDetail() {
        // Required empty public constructor
    }


    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        // Inflate the layout for this fragment
        View root = inflater.inflate(R.layout.post_task_detail, container, false);
        iniComponent(root);
        return root;
    }

    private void iniComponent(View view) {

    }

}
