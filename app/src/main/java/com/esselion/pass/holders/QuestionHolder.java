package com.esselion.pass.holders;

import android.graphics.drawable.Drawable;
import android.view.View;
import android.widget.ImageView;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import com.bumptech.glide.Glide;
import com.bumptech.glide.request.RequestOptions;
import com.esselion.pass.R;
import com.esselion.pass.models.Question;
import com.esselion.pass.util.Contracts;

import static com.esselion.pass.util.Tools.elapsedTime;

public class QuestionHolder extends RecyclerView.ViewHolder implements View.OnClickListener {
    private RecyclerViewClickListener mListener;
    private TextView name, ques, dotText, createdAt;
    private View dot;
    private ImageView image;
    private Question question;

    public QuestionHolder(@NonNull View itemView, RecyclerViewClickListener listener) {
        super(itemView);
        this.mListener = listener;
        itemView.setOnClickListener(this);
        image = itemView.findViewById(R.id.poster_image);
        name = itemView.findViewById(R.id.poster_name);
        createdAt = itemView.findViewById(R.id.createdAt);
//        noAns = itemView.findViewById(R.id.noAnswers);
        ques = itemView.findViewById(R.id.question);
//        bg = itemView.findViewById(R.id.lyt_parent);
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
        question = current;
        TextView elapsed = itemView.findViewById(R.id.elapsed_time);
        View profile = itemView.findViewById(R.id.action_profile);
        ques.setText(current.getQues());
        if (type) {
            profile.setVisibility(View.INVISIBLE);
            setStage(current.getStage(), drawable2, c);
            elapsed.setText(elapsedTime(current.getC_date()));
        } else {
            Glide.with(image.getContext())
                    .load(current.getPoster_image())
                    .apply(RequestOptions.circleCropTransform())
                    .into(image);
            createdAt.setText(elapsedTime(current.getC_date()));
            profile.setOnClickListener(this);
            elapsed.setVisibility(View.GONE);
            name.setText(current.getPosterName());
            dotText.setVisibility(View.GONE);
        }
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