package com.example.mytasker.holders;

import android.view.View;
import android.widget.ImageView;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import com.example.mytasker.R;
import com.example.mytasker.models.Bid;
import com.squareup.picasso.Picasso;

public class BidHolder extends RecyclerView.ViewHolder {
    ImageView avatar;
    private TextView desc, name, c_date, contact, price;

    public BidHolder(@NonNull View itemView) {
        super(itemView);
        name = itemView.findViewById(R.id.name);
        desc = itemView.findViewById(R.id.desc);
        c_date = itemView.findViewById(R.id.c_date);
        contact = itemView.findViewById(R.id.contact);
        price = itemView.findViewById(R.id.price);
        avatar = itemView.findViewById(R.id.avatar);
    }

    public void setItem(Bid bid) {
        name.setText(bid.getName());
        desc.setText(bid.getDes() + "");
        c_date.setText(elapsedTime(bid.getC_date()));
        date.setText(new Date(bid.getC_date()).toString());
        if (bid.getImage() != null)
            Picasso.with(image.getContext()).load(bid.getImage()).into(image);
        if (bid.getPoster_avatar() != null)
            Picasso.with(avatar.getContext()).load(bid.getImage()).into(avatar);
    }
}