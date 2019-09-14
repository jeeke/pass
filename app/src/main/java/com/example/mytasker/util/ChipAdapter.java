package com.example.mytasker.util;

import android.content.Context;
import android.content.res.ColorStateList;

import com.example.mytasker.R;
import com.google.android.material.chip.Chip;
import com.google.android.material.chip.ChipGroup;

import java.util.ArrayList;

import static com.example.mytasker.util.Contracts.dpToPx;

public class ChipAdapter {
    private ChipGroup parent;
    private ArrayList<String> list;
    private OnChipRemovedListener mListener;

    public ChipAdapter(OnChipRemovedListener listener, ChipGroup parent, ArrayList<String> list) {
        this.parent = parent;
        this.list = list;
        mListener = listener;
        initCatChips();
    }

    private Boolean type;
    private ColorStateList[] stateList = new ColorStateList[4];

    public ChipAdapter(ChipGroup parent, ArrayList<String> list) {
        this.parent = parent;
        this.list = list;
        initCatChips();
    }

//    public enum CHIP_TYPE {
//        TYPE_CLOSE_CIRCLED,
//        TYPE_CLOSE_UNCIRCLED,
//        TYPE_UNSELECT
//    }

    public ArrayList<String> getList() {
        return list;
    }

    ChipAdapter(ChipGroup parent, ArrayList<String> list, Boolean type) {
        this.parent = parent;
        this.list = list;
        this.type = type;
        initCatChips();
    }

    private Chip createChip(Context context, String title) {
        Chip chip = new Chip(context);
        chip.setText(title);
        chip.setHeight(dpToPx(40));
        chip.setTextColor(stateList[3]);
        chip.setChipBackgroundColor(stateList[2]);
        chip.setChipStrokeColor(stateList[0]);
        chip.setChipStrokeWidth(dpToPx(2));
        if (type == null) {
            chip.setClickable(false);
            if (mListener != null) {
//                chip.setCloseIconResource(R.drawable.ic_close);
                chip.setCloseIconVisible(true);
                chip.setOnCloseIconClickListener(v -> {
                    mListener.onChipRemoved(title);
                    list.remove(title);
                    parent.removeView(chip);
                });
            }
        } else {
            chip.setClickable(true);
            chip.setSelected(true);
            chip.setOnClickListener(v -> {
                if (chip.isSelected()) {
                    chip.setSelected(false);
                    chip.setChipStrokeColor(stateList[1]);
                    list.remove(title);
                } else {
                    chip.setSelected(true);
                    chip.setChipStrokeColor(stateList[0]);
                    list.add(title);
                }
            });

        }
        return chip;
    }

    public interface OnChipRemovedListener {
        void onChipRemoved(String title);
    }

    private void initCatChips() {
        stateList[0] = ColorStateList.valueOf(parent.getContext().getResources().getColor(R.color.green_A400));
        stateList[1] = ColorStateList.valueOf(parent.getContext().getResources().getColor(R.color.grey_200));
        stateList[2] = ColorStateList.valueOf(parent.getContext().getResources().getColor(R.color.white));
        stateList[3] = ColorStateList.valueOf(parent.getContext().getResources().getColor(R.color.blue_grey));
        for (String s : list) {
            parent.addView(createChip(parent.getContext(), s));
        }
    }

    public boolean isSafe(String title) {
        return (!list.contains(title)) && (!title.equals(""));
    }

    public void addChild(String title) {
        String[] values = title.split("[\\s,]+");
        for (String x : values) {
            if (isSafe(x)) {
                list.add(x);
                parent.addView(createChip(parent.getContext(), x));
            }
        }
    }
}
