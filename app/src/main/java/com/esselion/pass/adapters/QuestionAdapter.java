package com.esselion.pass.adapters;

import android.app.Activity;
import android.content.Intent;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import androidx.annotation.NonNull;
import androidx.fragment.app.FragmentActivity;
import androidx.recyclerview.widget.RecyclerView;

import com.esselion.pass.R;
import com.esselion.pass.activities.ProfileActivity;
import com.esselion.pass.activities.QuestionDetailActivity;
import com.esselion.pass.holders.QuestionHolder;
import com.esselion.pass.models.Question;

import java.util.ArrayList;
import java.util.List;

import static com.esselion.pass.util.Tools.launchActivity;

public class QuestionAdapter extends RecyclerView.Adapter<QuestionHolder> implements QuestionHolder.RecyclerViewClickListener {

    private ArrayList<Question> list;
    private Activity mActivity;

    public QuestionAdapter(FragmentActivity activity, ArrayList<Question> list) {
        this.list = list;
        mActivity = activity;
    }

    @NonNull
    @Override
    public QuestionHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        return new QuestionHolder(LayoutInflater.from(parent.getContext()).inflate(R.layout.card_question, parent, false), this);
    }


    public void update(List<Question> list) {
        this.list.clear();
        if(list!=null)
            this.list.addAll(list);
        notifyDataSetChanged();
    }

    @Override
    public void onBindViewHolder(@NonNull QuestionHolder holder, int position) {
        Question model = list.get(position);
        holder.setItem(model, false, null, null, 0);
    }

    @Override
    public int getItemCount() {
        return list.size();
    }

    @Override
    public void onClick(View view, Question question) {
        if (view.getId() == R.id.action_profile) {
            Intent intent = new Intent(mActivity, ProfileActivity.class);
            intent.putExtra("id", question.getPoster_id());
            intent.putExtra("name", question.getPosterName());
            intent.putExtra("avatar", question.getPoster_image());
            launchActivity(mActivity, intent);
        } else {
            Intent intent = new Intent(mActivity, QuestionDetailActivity.class);
            intent.putExtra("ques", question);
            launchActivity(mActivity, intent);
        }
    }
}


