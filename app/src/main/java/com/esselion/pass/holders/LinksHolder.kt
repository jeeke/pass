package com.esselion.pass.holders

import android.view.View
import androidx.recyclerview.widget.RecyclerView
import com.esselion.pass.adapters.Link
import kotlinx.android.synthetic.main.holder_link.view.*

class LinksHolder(itemView: View) : RecyclerView.ViewHolder(itemView) {
    fun setItem(current: Link) {
        itemView.link_image.setImageResource(current.imageResId)
        itemView.link_title.text = current.name
    }
}