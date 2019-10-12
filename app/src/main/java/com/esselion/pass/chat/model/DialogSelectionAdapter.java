package com.esselion.pass.chat.model;

import android.content.Context;
import android.graphics.Color;

import com.esselion.pass.R;
import com.stfalcon.chatkit.commons.ImageLoader;
import com.stfalcon.chatkit.dialogs.DialogsListAdapter;

public class DialogSelectionAdapter extends DialogsListAdapter<Dialog> {
    public DialogSelectionAdapter(ImageLoader imageLoader) {
        super(imageLoader);
    }

    @Override
    public void onBindViewHolder(BaseDialogViewHolder holder, int position) {
        super.onBindViewHolder(holder, position);
        if (items.get(position).getSelection()) {
            Context c = holder.itemView.getContext();
            holder.itemView.setBackgroundColor(c.getResources().getColor(R.color.blue_50));
        } else holder.itemView.setBackgroundColor(Color.parseColor("#ffffff"));
    }
}
