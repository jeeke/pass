package com.example.mytasker.fragments;

import android.os.Bundle;
import android.text.InputType;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.EditText;
import android.widget.ListView;
import android.widget.TextView;

import androidx.appcompat.app.AlertDialog;
import androidx.fragment.app.Fragment;

import com.example.mytasker.R;
import com.example.mytasker.util.ChipAdapter;
import com.google.android.material.dialog.MaterialAlertDialogBuilder;

import org.jetbrains.annotations.NotNull;

import java.util.ArrayList;
import java.util.Objects;

import static com.example.mytasker.util.Contracts.dpToPx;

public class PostTaskCat extends Fragment {


    public PostTaskCat() {
        // Required empty public constructor
    }

    private String category = "";
    private TextView task_cat;
    private ChipAdapter taskTags, taskMustHaves;
    private int which = 0;

    public String getTaskCat() {
        return category;
    }

    public ArrayList<String> getTaskTags() {
        return taskTags.getList();
    }

    public ArrayList<String> getTaskMustHaves() {
        return taskMustHaves.getList();
    }

    @Override
    public View onCreateView(@NotNull LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.post_task_cat, container, false);
        task_cat = view.findViewById(R.id.task_cat);
        task_cat.setOnClickListener(this::click);
        taskTags = new ChipAdapter(title -> {
        }, view.findViewById(R.id.task_tag_chips), new ArrayList<>());
        taskMustHaves = new ChipAdapter(title -> {
        }, view.findViewById(R.id.task_must_chips), new ArrayList<>());
        view.findViewById(R.id.task_tag).setOnClickListener(this::click);
        view.findViewById(R.id.task_must_haves).setOnClickListener(this::click);
        return view;
    }

    private void click(View v) {
        EditText input = new EditText(getContext());
        input.setInputType(InputType.TYPE_CLASS_TEXT);
        int pad = dpToPx(24);
        int p = dpToPx(16);
        input.setPadding(pad, pad, pad, p);
        int id = v.getId();
        switch (id) {
            case R.id.task_cat:
                new MaterialAlertDialogBuilder(Objects.requireNonNull(getContext()), R.style.AlertDialogTheme)
                        .setSingleChoiceItems(R.array.categ_names, which, (dialog, which) -> {
                        }).setTitle("Select Category")
                        .setPositiveButton("OK", (dialog, which) -> {
                            ListView lw = ((AlertDialog) dialog).getListView();
                            PostTaskCat.this.which = lw.getCheckedItemPosition();
                            category = (String) lw.getAdapter().getItem(PostTaskCat.this.which);
                            task_cat.setText(category);
                        }).show();
                break;
            case R.id.task_tag:
                new MaterialAlertDialogBuilder(Objects.requireNonNull(getContext()), R.style.AlertDialogTheme).setTitle("ADD TAG").setView(input)
                        .setPositiveButton("ADD", (dialog, which) -> {
                            if (taskTags.isSafe(input.getText().toString()))
                                taskTags.addChild(input.getText().toString());
                        })
                        .show();
                input.requestFocus();
                break;
            case R.id.task_must_haves:
                input.setPadding(pad, pad, pad, p);
                input.setPadding(pad, pad, pad, p);
                new MaterialAlertDialogBuilder(Objects.requireNonNull(getContext()), R.style.AlertDialogTheme).setTitle("ADD MUST HAVES").setView(input)
                        .setPositiveButton("ADD", (dialog, which) -> {
                            if (taskMustHaves.isSafe(input.getText().toString()))
                                taskMustHaves.addChild(input.getText().toString());
                        })
                        .show();
                input.requestFocus();
        }
    }

}