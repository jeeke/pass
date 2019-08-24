package com.example.mytasker.holders;

import android.view.View;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import com.example.mytasker.R;
import com.example.mytasker.models.Answer;

public class AnswerHolder extends RecyclerView.ViewHolder {
    //    private AnswerHolder.RecyclerViewClickListener mListener;
    private TextView name, ans;
//    private View dot;
//    private ImageView image;
//    private Answer answer;

//    public Answer getAnswer() {
//        return answer;
//    }

//    public interface RecyclerViewClickListener {
//        void onClick(View view, Answer answer);
//    }

    public AnswerHolder(@NonNull View itemView) {
        super(itemView);
//        this.mListener = listener;
//        itemView.setOnClickListener(this);
//        image = itemView.findViewById(R.id.poster_image);
        name = itemView.findViewById(R.id.name);
        ans = itemView.findViewById(R.id.answer);
//        ques = itemView.findViewById(R.id.answer);
//        bg = itemView.findViewById(R.id.lyt_parent);
//        dotText = itemView.findViewById(R.id.dotText);
//        dot = itemView.findViewById(R.id.dot);
    }

//    @Override
//    public void onClick(View v) {
//        mListener.onClick(v, answer);
//    }

    public void setItem(Answer current) {
//        bg.setBackground(drawable);
//        answer = current;
//        name.setText(current.getQues());
//        ques.setText(current.getQues());
//        noAns.setText("");
//        if (type) setStage(current.getStage(), drawable2, c);
//        else dotText.setVisibility(View.GONE);
//        answer = current;
        name.setText("by " + current.getName());
        ans.setText(current.getAnswer());
    }

//    private void setStage(int stage, Drawable drawable, int c) {
//        dotText.setVisibility(View.VISIBLE);
//        dot.setVisibility(View.VISIBLE);
//        drawable.setTint(c);
//        dot.setBackground(drawable);
//        dotText.setText(Contracts.TASK_STAGE_TEXT[stage]);
//    }
}


