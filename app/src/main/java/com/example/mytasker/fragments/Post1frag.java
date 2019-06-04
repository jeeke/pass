package com.example.mytasker.fragments;

import android.os.Build;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.EditText;

import androidx.annotation.RequiresApi;
import androidx.fragment.app.Fragment;
import androidx.viewpager.widget.ViewPager;

import com.example.mytasker.R;
import com.example.mytasker.adapters.MyViewPager;
import com.example.mytasker.models.ModelPost;

import java.util.ArrayList;
import java.util.List;

import me.crosswall.lib.coverflow.CoverFlow;
import me.crosswall.lib.coverflow.core.PagerContainer;

/**
 * A simple {@link Fragment} subclass.
 */
public class Post1frag extends Fragment {

    public static  String category;

    ViewPager pager;
    MyViewPager pagerList;
    Post1Adapter adapter;
    List<ModelPost> models;

    public Post1frag() {
        // Required empty public constructor
    }


    @RequiresApi(api = Build.VERSION_CODES.M)
    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.frag_post_1, container, false);


        models = new ArrayList<>();
        models.add(new ModelPost("Coder",R.drawable.handyman));
        models.add(new ModelPost("Teacher",R.drawable.handyman));
        models.add(new ModelPost("Photographer",R.drawable.handyman));
        models.add(new ModelPost("Trainer",R.drawable.handyman));
        models.add(new ModelPost("Handy Man",R.drawable.handyman));
        models.add(new ModelPost("Delivery",R.drawable.handyman));
        adapter = new Post1Adapter(models, getContext());
        EditText et = view.findViewById(R.id.editText);
        et.setActivated(false);
        et.setPressed(false);
        PagerContainer mContainer = (PagerContainer) view.findViewById(R.id.pager_container_post);
        pager = mContainer.getViewPager();
        pager.setAdapter(adapter);
        pager.setOffscreenPageLimit(adapter.getCount());
        pager.setClipChildren(false);
        pager.setOnPageChangeListener(new ViewPager.OnPageChangeListener() {

            @Override
            public void onPageSelected(int position) {
            category = models.get(position).getTitle();

            }

            @Override
            public void onPageScrolled(int position, float positionOffset, int positionOffsetPixels) {
                // TODO Auto-generated method stub

            }

            @Override
            public void onPageScrollStateChanged(int state) {
                // TODO Auto-generated method stub

            }
        });



        boolean showRotate =  true;

        if (showRotate) {
            new CoverFlow.Builder()
                    .with(pager)
                    .scale(0.5f)
                    .pagerMargin(0f)
                    .spaceSize(0f)
                    .rotationY(0f)
                    .build();

        }
        return view;
    }


}