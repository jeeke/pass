package com.example.mytasker.adapters;

import android.content.Context;
import android.graphics.drawable.Drawable;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import com.example.mytasker.R;
import com.example.mytasker.models.Question;
import com.example.mytasker.util.Contracts;

import java.util.ArrayList;
import java.util.List;

public class QuestionAdapter extends RecyclerView.Adapter<QuestionAdapter.holder> {

    private ArrayList<Question> list;
    private RecyclerViewClickListener mListener;
    private boolean type;
    private Context context;

    public interface RecyclerViewClickListener {
        void onClick(View view, int position);
    }

    public QuestionAdapter(Context context,RecyclerViewClickListener listener, ArrayList<Question> list, boolean type) {
        this.list = list;
        this.mListener =  listener;
        this.context = context;
        this.type = type;
    }

    @NonNull
    @Override
    public holder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(parent.getContext()).inflate(R.layout.card_question, parent, false);
        return new holder(view);
    }


    public void update(List<Question> list) {
        this.list.clear();
        if(list!=null)
            this.list.addAll(list);
        notifyDataSetChanged();
    }

    private void setStage(View v,int stage){
        View dot = v.findViewById(R.id.dot);
        TextView dotText = v.findViewById(R.id.dotText);
        dotText.setVisibility(View.VISIBLE);
        dot.setVisibility(View.VISIBLE);
        Drawable drawable = (context.getDrawable(R.drawable.notification_dot_indicator));
        drawable.setTint(context.getResources().getColor(Contracts.QUES_STAGE_COLORS[stage]));
        dot.setBackground(drawable);
//        dot.setBackgroundColor(((Context)mListener).getResources().getColor(Contracts.QUES_STAGE_COLORS[stage]));
        dotText.setText(Contracts.QUES_STAGE_TEXT[stage]);
    }

    @Override
    public void onBindViewHolder(@NonNull holder holder, int position) {
        View mView = holder.itemView;
        View v = mView.findViewById(R.id.lyt_parent);
        switch (position % 4) {
            case 0:
                v.setBackground(context.getDrawable(R.drawable.bg_soft_orange));
                break;
            case 1:
                v.setBackground(context.getDrawable(R.drawable.bg_orange));
                break;
            case 2:
                v.setBackground(context.getDrawable(R.drawable.bg_green));
                break;
            case 3:
                v.setBackground(context.getDrawable(R.drawable.bg_blue));
        }
        Question current = list.get(position);
        ((TextView) mView.findViewById(R.id.question)).setText(current.getQues());
        ((TextView) mView.findViewById(R.id.poster_id)).setText(current.getPosterName());
        if(type) setStage(mView,current.getStage());
    }

    @Override
    public int getItemCount() {
        return list.size();
    }

    public class holder extends RecyclerView.ViewHolder implements View.OnClickListener {
        View itemView;

        holder(@NonNull View itemView) {
            super(itemView);
            this.itemView = itemView;
            itemView.setOnClickListener(this);
        }

        @Override
        public void onClick(View v) {
            mListener.onClick(v, getAdapterPosition());
        }
    }
}


