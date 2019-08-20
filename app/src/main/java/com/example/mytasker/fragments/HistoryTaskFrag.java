package com.example.mytasker.fragments;

import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import androidx.annotation.NonNull;
import androidx.fragment.app.Fragment;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.example.mytasker.R;
import com.example.mytasker.adapters.TaskListAdapter;
import com.example.mytasker.models.Task;

import java.util.ArrayList;


/**
 * A placeholder fragment containing a simple view.
 */
public class HistoryTaskFrag extends Fragment {
    private ArrayList<Task> list;
    private TaskListAdapter.RecyclerViewClickListener listener;


    public HistoryTaskFrag(TaskListAdapter.RecyclerViewClickListener listener,ArrayList<Task> list) {
        this.list = list;
        this.listener = listener;
    }

    @Override
    public View onCreateView(
            @NonNull LayoutInflater inflater, ViewGroup container,
            Bundle savedInstanceState) {
        RecyclerView recyclerView = (RecyclerView) inflater.inflate(R.layout.frag_his_task, container, false);
//        RecyclerView recyclerView = root.findViewById(R.id.recyclerView);
        TaskListAdapter adapter = new TaskListAdapter(getContext(),listener,list,true);
        RecyclerView.LayoutManager layoutManager = new LinearLayoutManager(getContext());
        recyclerView.setLayoutManager(layoutManager);
        recyclerView.setAdapter(adapter);
        return recyclerView;
    }

}