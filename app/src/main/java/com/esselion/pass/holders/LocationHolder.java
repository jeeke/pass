package com.esselion.pass.holders;

import android.view.View;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import com.esselion.pass.R;
import com.esselion.pass.models.Location;

public class LocationHolder extends RecyclerView.ViewHolder implements View.OnClickListener {
    private RecyclerViewClickListener mListener;
    private TextView tag, desc;
    private Location location;

    public LocationHolder(@NonNull View itemView, RecyclerViewClickListener listener) {
        super(itemView);
        this.mListener = listener;
        itemView.setOnClickListener(this);
        itemView.findViewById(R.id.action_delete).setOnClickListener(this);
        tag = itemView.findViewById(R.id.tag);
        desc = itemView.findViewById(R.id.desc);
    }

    public Location getLocation() {
        return location;
    }

    @Override
    public void onClick(View v) {
        mListener.onClick(v, location);
    }

    public void setItem(Location current) {
        tag.setText(current.tag);
        if (!current.desc.equals("")) desc.setText(current.desc);
        this.location = current;
    }

    public interface RecyclerViewClickListener {
        void onClick(View view, Location location);
    }
}