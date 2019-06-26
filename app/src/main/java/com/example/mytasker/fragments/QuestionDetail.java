package com.example.mytasker.fragments;

import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.EditText;

import androidx.fragment.app.Fragment;

import com.example.mytasker.R;

public class QuestionDetail extends Fragment {
    private EditText question;

    public QuestionDetail() {
    }

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        View v = inflater.inflate(R.layout.fragment_question_detail, container, false);
        question = v.findViewById(R.id.question);
        return v;
    }

    public String getQuestion() {
        return String.valueOf(question.getText());
    }
}
