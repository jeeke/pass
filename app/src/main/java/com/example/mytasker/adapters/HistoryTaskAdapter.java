package com.example.mytasker.adapters;

import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;
import androidx.fragment.app.FragmentManager;
import androidx.fragment.app.FragmentPagerAdapter;

import com.example.mytasker.fragments.HistoryTaskFrag;
import com.example.mytasker.models.Task;
import com.example.mytasker.util.NetworkCache;

import java.util.ArrayList;


public class HistoryTaskAdapter extends FragmentPagerAdapter {

    //    @StringRes
    private static final String[] TAB_TITLES = new String[]{"Poster", "Tasker"};
    private final TaskListAdapter.RecyclerViewClickListener mContext;
    private ArrayList<Task> list;

    public HistoryTaskAdapter(TaskListAdapter.RecyclerViewClickListener context, FragmentManager fm) {
        super(fm);
        mContext = context;
    }

    private  void initList(int position){
        if(position==0) list = NetworkCache.prevPostModel.getPosted();
        else list = NetworkCache.prevPostModel.getDone();
    }


    @Override
    public Fragment getItem(int position) {
        initList(position);
        return new HistoryTaskFrag(mContext,list);
    }

    @Nullable
    @Override
    public CharSequence getPageTitle(int position) {
        return TAB_TITLES[position];
    }



    @Override
    public int getCount() {
        return 2;
    }
}