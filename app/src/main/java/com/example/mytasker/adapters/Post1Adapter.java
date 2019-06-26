package com.example.mytasker.adapters;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.constraintlayout.widget.ConstraintLayout;
import androidx.viewpager.widget.PagerAdapter;

import com.example.mytasker.R;
import com.example.mytasker.models.ModelPost;

import java.util.List;

public class Post1Adapter extends PagerAdapter {
    private List<ModelPost> models;
    private LayoutInflater layoutInflater;
    private Context context;

    public Post1Adapter(List<ModelPost> models, Context context) {
        this.models = models;
        this.context = context;
    }
    @Override
    public Object instantiateItem(@NonNull ViewGroup container, int position) {
        layoutInflater = LayoutInflater.from(context);
        View view = layoutInflater.inflate(R.layout.post1item, container, false);

        TextView title;
        ImageView person;
        ConstraintLayout constraintLayout;

        title = view.findViewById(R.id.taskername);
        person =  view.findViewById(R.id.taskerimage);

        title.setText(models.get(position).getTitle());
        person.setImageResource(models.get(position).getPerson());

        container.addView(view, 0);
        return view;
    }

    @Override
    public void destroyItem(ViewGroup container, int position, Object object) {
        container.removeView((View)object);
    }

    @Override
    public int getCount() {
        return models.size();

    }

    @Override
    public boolean isViewFromObject(View view, Object object) {
        return (view == object);
    }
}
