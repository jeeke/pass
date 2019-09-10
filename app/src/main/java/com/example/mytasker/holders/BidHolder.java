package com.example.mytasker.holders;

import android.view.View;
import android.widget.ImageView;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import com.bumptech.glide.Glide;
import com.bumptech.glide.request.RequestOptions;
import com.example.mytasker.R;
import com.example.mytasker.models.Bid;

import static com.example.mytasker.util.Tools.elapsedTime;

public class BidHolder extends RecyclerView.ViewHolder implements View.OnClickListener {
    private ImageView avatar;
    private TextView desc, name, c_date, contact, price;
    private Listener mListener;
    private String tasker_id, tasker_name, tasker_avatar;
    //TODO confirm assign and resign button
//    private static pass_splash int BUTTON_ASSIGN = R.id.assign;

    public BidHolder(@NonNull View itemView, Listener listener) {
        super(itemView);
        mListener = listener;
        name = itemView.findViewById(R.id.poster_name);
        desc = itemView.findViewById(R.id.desc);
        c_date = itemView.findViewById(R.id.c_date);
        contact = itemView.findViewById(R.id.second);
        price = itemView.findViewById(R.id.price);
        avatar = itemView.findViewById(R.id.poster_image);
    }

    public void setItem(Bid bid) {
        itemView.findViewById(R.id.assign).setOnClickListener(this);
        itemView.setOnClickListener(this);
        tasker_name = bid.getName();
        tasker_avatar = bid.getAvatar();
        name.setText(bid.getName());
        desc.setText(bid.getDes() + "");
        c_date.setText(elapsedTime(Long.parseLong(bid.getC_date())));
        price.setText("₹" + bid.getPrice());
//        Toast.makeText(itemView.getContext(),  Toast.LENGTH_SHORT).show();
        this.tasker_id = bid.getId();
        if (bid.getAvatar() != null)
            Glide.with(avatar.getContext()).load(bid.getAvatar()).apply(RequestOptions.circleCropTransform()).into(avatar);
    }

    @Override
    public void onClick(View v) {
        mListener.onClick(v, tasker_id, tasker_name, tasker_avatar);
    }

    public interface Listener {
        void onClick(View v, String tasker_id, String tasker_name, String tasker_avatar);
    }
}