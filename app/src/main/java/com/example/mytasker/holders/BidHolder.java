package com.example.mytasker.holders;

import android.view.View;
import android.widget.ImageView;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import com.example.mytasker.R;
import com.example.mytasker.models.Bid;
import com.squareup.picasso.Picasso;

import static com.example.mytasker.util.Tools.elapsedTime;

public class BidHolder extends RecyclerView.ViewHolder implements View.OnClickListener {
    private ImageView avatar;
    private TextView desc, name, c_date, contact, price;
    private Listener mListener;
    private String tasker_id;
    //TODO confirm assign and resign button
//    private static final int BUTTON_ASSIGN = R.id.assign;

    public BidHolder(@NonNull View itemView, Listener listener) {
        super(itemView);
        mListener = listener;
        name = itemView.findViewById(R.id.name);
        desc = itemView.findViewById(R.id.desc);
        c_date = itemView.findViewById(R.id.c_date);
        contact = itemView.findViewById(R.id.second);
        price = itemView.findViewById(R.id.price);
        avatar = itemView.findViewById(R.id.avatar);
    }

    public void setItem(Bid bid) {
        itemView.findViewById(R.id.assign).setOnClickListener(this);
        name.setText(bid.getName());
        desc.setText(bid.getDes() + "");
        c_date.setText(elapsedTime(Long.parseLong(bid.getC_date())));
        price.setText("$" + bid.getPrice());
//        Toast.makeText(itemView.getContext(),  Toast.LENGTH_SHORT).show();
        this.tasker_id = bid.getId();
        if (bid.getAvatar() != null)
            Picasso.with(avatar.getContext()).load(bid.getAvatar()).into(avatar);
    }

    @Override
    public void onClick(View v) {
        mListener.onClick(v, tasker_id);
    }

    public interface Listener {
        void onClick(View v, String tasker_id);
    }
}