package com.example.mytasker.models;

import android.content.Context;
import android.content.res.TypedArray;

import com.example.mytasker.R;
import com.example.mytasker.util.Tools;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;

@SuppressWarnings("ResourceType")
public class DataGenerator {

    public static List<People> getPeopleData(Context ctx) {
        List<People> items = new ArrayList<>();
        TypedArray drw_arr = ctx.getResources().obtainTypedArray(R.array.people_images);
        String name_arr[] = ctx.getResources().getStringArray(R.array.people_names);

        for (int i = 0; i < drw_arr.length(); i++) {
            People obj = new People();
            obj.image = drw_arr.getResourceId(i, -1);
            obj.name = name_arr[i];
            obj.email = Tools.getEmailFromName(obj.name);
            obj.imageDrw = ctx.getResources().getDrawable(obj.image);
            items.add(obj);
        }
        Collections.shuffle(items);
        return items;
    }
}
