package com.example.mytasker.fragments;


import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;

import androidx.fragment.app.Fragment;

import com.example.mytasker.R;

public class PostFrag3 extends Fragment {

    ImageView clock,imageView1,imageView2,imageView3,imageView4;
    public PostFrag3() {
        // Required empty public constructor
    }
    public static PostFrag3 newInstance(String param1, String param2) {
        PostFrag3 fragment = new PostFrag3();
        return fragment;
    }

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        if (getArguments() != null) {
        }
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        // Inflate the layout for this fragment
        View layout =  inflater.inflate(R.layout.frag_post3, container, false);
        imageView1 = layout.findViewById(R.id.imageView14);
        imageView2 = layout.findViewById(R.id.imageView15);
        imageView3 = layout.findViewById(R.id.imageView16);
        imageView4 = layout.findViewById(R.id.imageView17);
//        //clock = layout.findViewById(R.id.clock);
//
//
//        imageView1.setOnClickListener(new View.OnClickListener() {
//            @Override
//            public void onClick(View v) {
//                clock.setRotation(0);
//            }
//        });
//        imageView2.setOnClickListener(new View.OnClickListener() {
//            @Override
//            public void onClick(View v) {
//                clock.setRotation(90);
//            }
//        });
//
//        imageView4.setOnClickListener(new View.OnClickListener() {
//            @Override
//            public void onClick(View v) {
//                clock.setRotation(180);
//            }
//        });
//        imageView3.setOnClickListener(new View.OnClickListener() {
//            @Override
//            public void onClick(View v) {
//                clock.setRotation(270);
//            }
//        });
        return layout;
    }

}
