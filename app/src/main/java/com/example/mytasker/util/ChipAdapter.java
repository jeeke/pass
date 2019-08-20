package com.example.mytasker.util;

import android.content.Context;
import android.content.res.ColorStateList;
import android.content.res.TypedArray;
import android.util.TypedValue;

import com.example.mytasker.R;
import com.google.android.material.chip.Chip;
import com.google.android.material.chip.ChipGroup;

import java.util.ArrayList;

import static com.example.mytasker.util.Contracts.dpToPx;

public class ChipAdapter {
    ChipGroup parent;
    ArrayList<String> list;
    public ChipAdapter(ChipGroup parent, ArrayList<String> list){
        this.parent = parent;
        this.list = list;
        initCatChips();
    }

    private int Color(int c) {
        TypedValue typedValue = new TypedValue();

        TypedArray a = parent.getContext().obtainStyledAttributes(typedValue.data, new int[] { c});
        int color = a.getColor(0, 0);

        a.recycle();

        return color;
    }

    private Chip createChip(Context context, String title) {
        Chip chip = new Chip(context);
        chip.setText(title);
        chip.setHeight(dpToPx(40));
        chip.setTextColor(Color(R.attr.colorAccent));
        chip.setChipBackgroundColor(ColorStateList.valueOf(Color(R.attr.colorPrimary)));
        chip.setChipStrokeColor(ColorStateList.valueOf(parent.getContext().getResources().getColor(R.color.green_A400)));
        chip.setChipStrokeWidth(dpToPx(2));
        return chip;
    }

    private void initCatChips() {
        for (String s : list) {
            parent.addView(createChip(parent.getContext(), s));
        }
    }

    public boolean isSafe(String title){
        return !list.contains(title);
    }

    public void addChild(String title){
        list.add(title);
        parent.addView(createChip(parent.getContext(), title));
    }
}
