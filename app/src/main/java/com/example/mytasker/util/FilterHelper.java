package com.example.mytasker.util;

import androidx.constraintlayout.motion.widget.MotionLayout;

import com.appyvet.materialrangebar.RangeBar;
import com.example.mytasker.R;
import com.google.android.material.chip.ChipGroup;

import java.util.ArrayList;
import java.util.Arrays;

public class FilterHelper {

    private FilterListener listener;

    public ArrayList<String> tags;
    public int radius;
    public int[] price = {0, 100};
    public boolean remote = false;


    private MotionLayout filter;
    private boolean open;
    private RangeBar radiusBar, priceBar;
    //    private Switch remoteTask;
    private ChipAdapter adapter;

    public FilterHelper(FilterListener listener, MotionLayout v) {
        this.listener = listener;
        open = false;
        radius = 100;
        filter = v;
        String[] chipTitle = v.getContext().getResources().getStringArray(R.array.categ_names);
        v.findViewById(R.id.list_head).setOnClickListener(view -> toggle());
        ChipGroup chipGroup = v.findViewById(R.id.chipGroup1);
        radiusBar = v.findViewById(R.id.radius_bar);
        priceBar = v.findViewById(R.id.price_bar);
//        remoteTask = v.findViewById(R.id.remote_task);
        tags = new ArrayList<>(Arrays.asList(chipTitle));
        adapter = new ChipAdapter(chipGroup, tags, true);
    }

    private void toggle() {
        if (open) {
            filter.transitionToStart();
            tags = adapter.getList();
            radius = radiusBar.getRightIndex() * (int) radiusBar.getTickInterval();
            price[0] = priceBar.getLeftIndex() * (int) priceBar.getTickInterval() * 1000;
            price[1] = priceBar.getRightIndex() * (int) priceBar.getTickInterval() * 1000;
//            remote = remoteTask.isChecked();
            listener.closedMenu();
            //Log.e("tags",tags.toString() + "\n" + radius + " "  + price[0] + " " + price[1]);
        } else {
            filter.transitionToEnd();
        }
        open = !open;
    }

    public interface FilterListener {
        void closedMenu();
    }
}
