package com.example.mytasker.adapters;

import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;
import androidx.fragment.app.FragmentManager;
import androidx.fragment.app.FragmentPagerAdapter;

import com.example.mytasker.fragments.HistoryTaskFrag;


public class HistoryTaskAdapter extends FragmentPagerAdapter {
    //    @StringRes
    private static final String[] TAB_TITLES = new String[]{"Poster", "Tasker"};

    public HistoryTaskAdapter(FragmentManager fm) {
        super(fm);
    }


    @Override
    public Fragment getItem(int position) {
        return new HistoryTaskFrag(position + 1);
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