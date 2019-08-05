package com.example.mytasker.util;

import android.content.Context;
import android.view.View;
import android.widget.Switch;

import androidx.constraintlayout.motion.widget.MotionLayout;

import com.appyvet.materialrangebar.RangeBar;
import com.example.mytasker.R;
import com.google.android.material.chip.Chip;
import com.google.android.material.chip.ChipGroup;

import java.util.ArrayList;

public class FilterHelper {

    public ArrayList<String> tags = new ArrayList<>();
    public int radius;
    public boolean remote = false;
    public int[] price = new int[2];
    String[] chipTitle = {"one", "two", "three", "four", "five", "six", "seven", "eight", "nine", "ten", "eleven", "twelve"};
    float[] loc = new float[2];
    private MotionLayout filter;
    private boolean open;
    RangeBar radiusBar, priceBar;
    Switch remoteTask;
    private ChipGroup chipGroup;

    public FilterHelper(MotionLayout v){
        open = false;
        filter = v.findViewById(R.id.scrollable);
        v.findViewById(R.id.filter).setOnClickListener(this::toggle);
        chipGroup = v.findViewById(R.id.chipGroup1);
        radiusBar = v.findViewById(R.id.radius_bar);
        priceBar = v.findViewById(R.id.price_bar);
        remoteTask = v.findViewById(R.id.remote_task);
        initCatChips();
    }

    private Chip createChip(Context context, String title) {
        Chip chip = new Chip(context);
        chip.setText(title);
        chip.setCheckable(true);
        chip.setChecked(true);
        chip.setClickable(true);
        return chip;
    }

    private void initCatChips() {
        for (String s : chipTitle) {
            chipGroup.addView(createChip(chipGroup.getContext(), s));
        }
    }

    private void toggle(View v){
        if(open){
            filter.transitionToStart();
            for (int i = 0; i < chipGroup.getChildCount(); i++) {
                Chip chip = (Chip) chipGroup.getChildAt(i);
                if (chip.isChecked()) tags.add(chip.getText().toString());
            }
            radius = radiusBar.getRightIndex() * (int) radiusBar.getTickInterval();
            price[0] = priceBar.getLeftIndex() * (int) priceBar.getTickInterval();
            price[1] = priceBar.getRightIndex() * (int) priceBar.getTickInterval();
            remote = remoteTask.isActivated();
            //Log.e("tags",tags.toString() + "\n" + radius + " "  + price[0] + " " + price[1]);
        }else{
            filter.transitionToEnd();
        }
        open = !open;
    }
}
