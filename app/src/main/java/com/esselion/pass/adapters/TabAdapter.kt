package com.esselion.pass.adapters

import androidx.fragment.app.Fragment
import androidx.fragment.app.FragmentManager
import androidx.fragment.app.FragmentPagerAdapter
import com.esselion.pass.fragments.ChatContent
import com.esselion.pass.fragments.HistoryContent

class TabAdapter(private val type: Int, fm: FragmentManager, behavior: Int) : FragmentPagerAdapter(fm, behavior) {

    override fun getItem(position: Int): Fragment {
        return if (type == TYPE_CHAT) ChatContent() else HistoryContent(position + 1)
    }

    override fun getPageTitle(position: Int): CharSequence? {
        val a = if (type == TYPE_CHAT) CHAT_TITLES else HISTORY_TITLES
        return a[position]
    }

    override fun getCount(): Int {
        return 2
    }

    companion object {
        const val TYPE_CHAT = 0
        const val TYPE_HISTORY = 1
        val CHAT_TITLES = arrayOf("Active Chats", "Archive")
        private val HISTORY_TITLES = arrayOf("Tasks Posted", "Tasks Taken")
    }
}