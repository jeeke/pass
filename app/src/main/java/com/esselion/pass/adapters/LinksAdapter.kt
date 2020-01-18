package com.esselion.pass.adapters

import android.content.Context
import android.content.Intent
import android.net.Uri
import android.view.LayoutInflater
import android.view.ViewGroup
import androidx.recyclerview.widget.RecyclerView
import com.esselion.pass.R
import com.esselion.pass.holders.LinksHolder


data class Link(var name: String, var imageResId: Int, var url: String)

class LinksAdapter(private val context: Context?, private val list: ArrayList<Link>) : RecyclerView.Adapter<LinksHolder>() {
    init {
        list.add(0, Link("Add Link", R.drawable.ic_add, "https://google.com"))
    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): LinksHolder {
        val view = LayoutInflater.from(parent.context).inflate(R.layout.holder_link, parent, false)
        return LinksHolder(view)
    }

    override fun onBindViewHolder(holder: LinksHolder, position: Int) {
        holder.setItem(list[position])
        holder.itemView.setOnClickListener { onClick(position) }
    }

    override fun getItemCount(): Int {
        return list.size
    }

    interface RecyclerViewClickListener {
        fun onClick(position: Int)
    }

    fun onClick(position: Int) {
        var url = list[position].url
        if (!url.startsWith("http://") && !url.startsWith("https://"))
            url = "http://$url";
        val browserIntent = Intent(Intent.ACTION_VIEW, Uri.parse(url))
        context?.startActivity(browserIntent)
    }
}
