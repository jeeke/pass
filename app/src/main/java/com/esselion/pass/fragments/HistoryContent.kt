package com.esselion.pass.fragments

import android.content.Intent
import android.graphics.drawable.Drawable
import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.fragment.app.Fragment
import androidx.recyclerview.widget.LinearLayoutManager
import com.esselion.pass.R
import com.esselion.pass.activities.TaskDetailActivity
import com.esselion.pass.holders.TaskHolder
import com.esselion.pass.models.Task
import com.esselion.pass.util.Cache
import com.esselion.pass.util.Contracts
import com.esselion.pass.util.Tools
import com.firebase.ui.database.FirebaseRecyclerAdapter
import com.firebase.ui.database.FirebaseRecyclerOptions
import com.google.firebase.database.Query
import kotlinx.android.synthetic.main.frag_his_task.*

class HistoryContent(var type: Int = 0) : Fragment(), TaskHolder.RecyclerViewClickListener {
    private var mAdapter: FirebaseRecyclerAdapter<*, *>? = null

    override fun onCreateView(inflater: LayoutInflater, container: ViewGroup?,
                              savedInstanceState: Bundle?): View? {
        return inflater.inflate(R.layout.frag_his_task, container, false)
    }

    override fun onActivityCreated(savedInstanceState: Bundle?) {
        super.onActivityCreated(savedInstanceState)
        callFireBase()
    }


    private fun callFireBase() {
        recyclerView?.setHasFixedSize(true)
        val mManager = LinearLayoutManager(context)
        recyclerView?.layoutManager = mManager
        //Initialize Database
        val c: String = if (type == 2) "AsTasker" else "AsPoster"
        //TODO remodel table to increase efficiency
        val mQuery: Query = Cache.getDatabase().child("PrevTasks").child(Cache.getUser().uid).child(c)
        //Initialize FirebasePagingOptions
        val options = FirebaseRecyclerOptions.Builder<Task>()
                .setQuery(mQuery, Task::class.java).build()
        //Initialize Adapter
        mAdapter = object : FirebaseRecyclerAdapter<Task, TaskHolder>(options) {
            override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): TaskHolder {
                return TaskHolder(LayoutInflater.from(parent.context).inflate(R.layout.card_task, parent, false), this@HistoryContent)
            }

            override fun onBindViewHolder(taskHolder: TaskHolder, position: Int, model: Task) {
                val drawable: Drawable = when (position % 4) {
                    0 -> context!!.getDrawable(R.drawable.bg_soft_orange)
                    1 -> context!!.getDrawable(R.drawable.bg_orange)
                    2 -> context!!.getDrawable(R.drawable.bg_green)
                    else -> context!!.getDrawable(R.drawable.bg_blue)
                }
                val drawable2 = context!!.getDrawable(R.drawable.notification_dot_indicator)
                val c = context!!.resources.getColor(Contracts.TASK_STAGE_COLORS[model.stage])
                taskHolder.setItem(model, true, drawable, drawable2, c)
            }
        }
        recyclerView.adapter = mAdapter
    }


    override fun onStart() {
        super.onStart()
        mAdapter?.startListening()
    }

    override fun onStop() {
        super.onStop()
        mAdapter?.stopListening()
    }

    override fun onClick(view: View?, task: Task?) {
        val intent = Intent(context, TaskDetailActivity::class.java)
        intent.putExtra("from", type)
        intent.putExtra("task", task)
        Tools.launchActivity(activity, intent)
    }
}