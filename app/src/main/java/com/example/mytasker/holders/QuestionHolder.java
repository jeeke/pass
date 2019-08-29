package com.example.mytasker.holders;

import android.graphics.drawable.Drawable;
import android.view.View;
import android.widget.ImageView;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import com.bumptech.glide.Glide;
import com.bumptech.glide.request.RequestOptions;
import com.example.mytasker.R;
import com.example.mytasker.models.Question;
import com.example.mytasker.util.Contracts;

public class QuestionHolder extends RecyclerView.ViewHolder implements View.OnClickListener {
    private RecyclerViewClickListener mListener;
    private TextView name, ques, noAns, dotText;
    private View bg, dot;
    private ImageView image;
    private Question question;

    public QuestionHolder(@NonNull View itemView, RecyclerViewClickListener listener) {
        super(itemView);
        this.mListener = listener;
        itemView.setOnClickListener(this);
        image = itemView.findViewById(R.id.poster_image);
        name = itemView.findViewById(R.id.poster_name);
        noAns = itemView.findViewById(R.id.noAnswers);
        ques = itemView.findViewById(R.id.question);
        bg = itemView.findViewById(R.id.lyt_parent);
        dotText = itemView.findViewById(R.id.dotText);
        dot = itemView.findViewById(R.id.dot);
    }

    public Question getQuestion() {
        return question;
    }

    @Override
    public void onClick(View v) {
        mListener.onClick(v, question);
    }

    //TODO implement no of answers

    public void setItem(Question current, boolean type, Drawable drawable, Drawable drawable2, int c) {
        bg.setBackground(drawable);
        question = current;
        name.setText(current.getQues());
        ques.setText(current.getQues());
        noAns.setText("");
        Glide.with(image.getContext()).load(current.getPoster_image()).apply(new RequestOptions().placeholder(R.drawable.person)).into(image);
        if (type) setStage(current.getStage(), drawable2, c);
        else dotText.setVisibility(View.GONE);
    }

    private void setStage(int stage, Drawable drawable, int c) {
        dotText.setVisibility(View.VISIBLE);
        dot.setVisibility(View.VISIBLE);
        drawable.setTint(c);
        dot.setBackground(drawable);
        dotText.setText(Contracts.TASK_STAGE_TEXT[stage]);
    }

    public interface RecyclerViewClickListener {
        void onClick(View view, Question question);
    }
}