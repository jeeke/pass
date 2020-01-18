package com.esselion.pass.fragments

import android.app.Activity
import android.os.Bundle
import android.view.*
import androidx.appcompat.app.AppCompatActivity
import androidx.fragment.app.Fragment
import androidx.fragment.app.FragmentPagerAdapter
import com.esselion.pass.R
import com.esselion.pass.activities.NotificationActivity
import com.esselion.pass.adapters.TabAdapter
import com.esselion.pass.adapters.TabAdapter.Companion.TYPE_HISTORY
import com.esselion.pass.util.Tools
import kotlinx.android.synthetic.main.fragment_dash_container.*

class HistoryContainer : Fragment() {

    override fun onCreateView(inflater: LayoutInflater, container: ViewGroup?,
                              savedInstanceState: Bundle?): View? {
        return inflater.inflate(R.layout.fragment_dash_container, container, false)
    }

    override fun onActivityCreated(savedInstanceState: Bundle?) {
        super.onActivityCreated(savedInstanceState)
        val sectionsPagerAdapter = TabAdapter(TYPE_HISTORY, childFragmentManager, FragmentPagerAdapter.BEHAVIOR_RESUME_ONLY_CURRENT_FRAGMENT)
        view_pager.adapter = sectionsPagerAdapter
        tab_layout.setupWithViewPager(view_pager)
        initToolbar()
    }

    override fun onCreateOptionsMenu(menu: Menu, inflater: MenuInflater) {
        super.onCreateOptionsMenu(menu, inflater)
        inflater.inflate(R.menu.menu_home, menu)
        super.onCreateOptionsMenu(menu, inflater)
    }

    override fun onOptionsItemSelected(item: MenuItem): Boolean {
        val id = item.itemId
        val activity: Activity? = activity
        if (activity != null) { //            SharedPrefAdapter sp = SharedPrefAdapter.getInstance();
            if (id == R.id.action_notification) {
                Tools.launchActivity(activity, NotificationActivity::class.java)
                activity.invalidateOptionsMenu()
            }
            return false
        }
        return super.onOptionsItemSelected(item)
    }

    private fun initToolbar() {
        setHasOptionsMenu(true)
        val a = activity as? AppCompatActivity
        a?.setSupportActionBar(toolbar)
        a?.supportActionBar?.title = "History"
    }
}