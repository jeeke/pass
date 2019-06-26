package com.example.mytasker.util;

import android.content.Context;
import android.view.View;

import androidx.constraintlayout.motion.widget.MotionLayout;

import com.example.mytasker.R;
import com.google.android.material.chip.Chip;
import com.google.android.material.chip.ChipGroup;

public class FilterHelper {


    private MotionLayout filter;
    private boolean open;
    public FilterHelper(MotionLayout v){
        open = false;
        filter = v.findViewById(R.id.scrollable);
        v.findViewById(R.id.imageView8).setOnClickListener(this::toggle);
        chipGroup1 = v.findViewById(R.id.chipGroup1);
        chipGroup2 = v.findViewById(R.id.chipGroup2);
        initCatChips();
    }

    private Chip createChip(Context context, String title) {
        Chip chip = new Chip(context);
        chip.setText(title);
        chip.setCheckable(true);
        chip.setClickable(true);
        return chip;
    }

    private ChipGroup chipGroup1, chipGroup2;

    private void initCatChips() {
        String[] chipTitle = {"one", "two", "three", "four", "five", "six", "seven", "eight", "nine", "ten", "eleven", "twelve"};
        for (int i = 0; i < chipTitle.length; i++) {
            if (i % 2 == 0) {
                View view;
                chipGroup1.addView(createChip(chipGroup1.getContext(), chipTitle[i]));
            } else {
                chipGroup2.addView(createChip(chipGroup2.getContext(), chipTitle[i]));
            }

        }
    }

    private void toggle(View v){
        if(open){
            filter.transitionToStart();
        }else{
            filter.transitionToEnd();
        }
        open = !open;
    }
}
