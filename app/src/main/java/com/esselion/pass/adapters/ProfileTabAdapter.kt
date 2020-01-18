package com.esselion.pass.adapters

import androidx.fragment.app.Fragment
import androidx.fragment.app.FragmentManager
import androidx.fragment.app.FragmentPagerAdapter
import com.esselion.pass.InfoFragment
import com.esselion.pass.fragments.WallFragment

class ProfileTabAdapter(fm: FragmentManager, behavior: Int) :
        FragmentPagerAdapter(fm, behavior) {

    companion object {
        private val TABS =
                arrayOf("Info", "Wall")
    }

    override fun getItem(position: Int): Fragment {
        return if (position == 0) InfoFragment() else WallFragment()
    }

    override fun getPageTitle(position: Int): CharSequence? {
        return TABS[position]
    }

    override fun getCount() = 2
}