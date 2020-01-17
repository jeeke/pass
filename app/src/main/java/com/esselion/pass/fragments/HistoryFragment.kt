package com.esselion.pass.fragments

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.fragment.app.Fragment
import com.esselion.pass.R
import com.esselion.pass.adapters.HistoryTaskAdapter
import kotlinx.android.synthetic.main.activity_history_task.*

class HistoryFragment : Fragment() {

    override fun onCreateView(inflater: LayoutInflater, container: ViewGroup?,
                              savedInstanceState: Bundle?): View? {
        return inflater.inflate(R.layout.activity_history_task, container, false)
    }

    override fun onActivityCreated(savedInstanceState: Bundle?) {
        super.onActivityCreated(savedInstanceState)
        val sectionsPagerAdapter = HistoryTaskAdapter(childFragmentManager)
        view_pager.adapter = sectionsPagerAdapter
        tabs.setupWithViewPager(view_pager)
    }

    //    private fun initToolbar() {
//        val toolbar: Toolbar = findViewById<Toolbar>(R.id.toolbar)
//        setSupportActionBar(toolbar)
//        getSupportActionBar().setTitle("My Tasks")
//        getSupportActionBar().setDisplayHomeAsUpEnabled(true)
//    }
}