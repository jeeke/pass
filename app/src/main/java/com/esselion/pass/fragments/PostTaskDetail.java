package com.esselion.pass.fragments;


import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.EditText;

import androidx.fragment.app.Fragment;

import com.esselion.pass.R;

import org.jetbrains.annotations.NotNull;

public class PostTaskDetail extends Fragment {

    private EditText title, desc;

    public String getTitle() {
        return title.getText().toString();
    }

    public String getDesc() {
        return desc.getText().toString();
    }
    public PostTaskDetail() {
        // Required empty public constructor
    }



    @Override
    public View onCreateView(@NotNull LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        // Inflate the layout for this fragment
        View root = inflater.inflate(R.layout.post_task_detail, container, false);
        iniComponent(root);
        return root;
    }

    private void iniComponent(View view) {
        title = view.findViewById(R.id.post_title);
        desc = view.findViewById(R.id.post_desc);
    }

}
