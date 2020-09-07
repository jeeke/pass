package com.esselion.pass.fragments

import android.content.Intent
import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.ImageView
import androidx.fragment.app.Fragment
import com.bumptech.glide.Glide
import com.bumptech.glide.request.RequestOptions
import com.esselion.pass.R
import com.esselion.pass.activities.NotificationActivity
import com.esselion.pass.chat.MessagesActivity
import com.esselion.pass.chat.model.Dialog
import com.esselion.pass.chat.model.DialogHelper
import com.esselion.pass.chat.model.DialogSelectionAdapter
import com.esselion.pass.util.Cache
import com.esselion.pass.util.Tools
import com.google.firebase.database.*
import com.stfalcon.chatkit.commons.ImageLoader
import com.stfalcon.chatkit.dialogs.DialogsListAdapter
import kotlinx.android.synthetic.main.empty.*
import kotlinx.android.synthetic.main.fragment_chats.*
import java.util.*
import kotlin.collections.HashSet

class ChatContent : Fragment(), DialogsListAdapter.OnDialogViewClickListener<Dialog>,
        DialogsListAdapter.OnDialogViewLongClickListener<Dialog> {

    var imageLoader: ImageLoader? = null
    var dialogsAdapter: DialogsListAdapter<Dialog>? = null
    var selectedDialogs: HashSet<Dialog> = HashSet()
    var items: ArrayList<Dialog>? = null
    //    private lateinit var menu: Menu
    private var mConvDatabase: DatabaseReference? = null

//    override fun onCreateOptionsMenu(menu: Menu, inflater: MenuInflater) {
//        super.onCreateOptionsMenu(menu, inflater)
//        inflater.inflate(R.menu.delete_menu, menu)
//        this.menu = menu
//        super.onCreateOptionsMenu(menu, inflater)
//    }

    private fun initToolbar() {
        toolbar.setOnMenuItemClickListener {
            when (it.itemId) {
                R.id.action_notification -> {
                    Tools.launchActivity(activity, NotificationActivity::class.java)
                    requireActivity().invalidateOptionsMenu()
                    true
                }
                else -> false
            }
        }
    }
//    override fun onOptionsItemSelected(item: MenuItem): Boolean {
//        val id = item.itemId
//        val activity: Activity? = activity
//        if (activity != null) { //            SharedPrefAdapter sp = SharedPrefAdapter.getInstance();
//            if (id == R.id.action_notification) {
//            }
//            if (item.itemId == R.id.action_delete) {
//                deleteSelectedItems()
//                return true
//            }
//            return false
//        }
//        return super.onOptionsItemSelected(item)
//    }

    override fun onStart() {
        super.onStart()
        val uid = Cache.getUser().uid
        mConvDatabase = FirebaseDatabase.getInstance().reference.child("Chats").child(uid)
        queryFireBase()
    }

    override fun onCreateView(inflater: LayoutInflater, container: ViewGroup?,
                              savedInstanceState: Bundle?): View? {
        // Inflate the layout for this fragment
        return inflater.inflate(R.layout.fragment_chats, container, false)
    }

    private fun queryFireBase() {
        try {
            val conversationQuery = mConvDatabase!!.orderByChild("lastActivity")
            conversationQuery.addValueEventListener(
                    object : ValueEventListener {
                        override fun onDataChange(dataSnapshot: DataSnapshot) {
                            val dialogs = ArrayList<Dialog>()
                            for (data in dataSnapshot.children) {
                                val helper = data.getValue(DialogHelper::class.java)
                                if (helper != null) {
                                    dialogs.add(helper.toDialog())
                                }
                            }
                            if (dialogs.isEmpty()) {
                                dialogsList?.visibility = View.GONE
                                anim?.visibility = View.VISIBLE
                            } else {
                                dialogsList?.visibility = View.VISIBLE
                                items = dialogs
                                initAdapter()
                            }
                        }

                        override fun onCancelled(databaseError: DatabaseError) {}
                    }
            )
        } catch (e: java.lang.Exception) {
            e.printStackTrace()
        }
    }


    override fun onActivityCreated(savedInstanceState: Bundle?) {
        super.onActivityCreated(savedInstanceState)
        anim?.visibility = View.GONE
        initToolbar()
        imageLoader = ImageLoader { imageView: ImageView, url: String?, _: Any? -> Glide.with(this).load(url).apply(RequestOptions.circleCropTransform()).into(imageView) }
        dialogsAdapter = DialogSelectionAdapter(imageLoader)
    }

//    fun onSelectionChanged() {
//        menu.findItem(R.id.action_delete).isVisible = selectedDialogs.size > 0
//    }

//    fun onBackPressed() {
//        if (selectedDialogs.isEmpty()) {
//           super.onBackPressed()
//        } else {
//            unSelectAllItems()
//            onSelectionChanged()
//        }
//    }


//    private fun deleteSelectedItems() {
//        try {
//            if (selectedDialogs.size > 0) {
//                val u: MutableMap<String, Any?> = HashMap()
//                val rootRef1 = "Chats/" + Cache.getUser().uid + "/"
//                val rootRef2 = "Messages/" + Cache.getUser().uid + "/"
//                for (d in selectedDialogs) {
//                    u[rootRef1 + d.id] = null
//                    u[rootRef2 + d.id] = null
//                }
////                showProgressBar(true)
//                callFireBase(u)
//                unSelectAllItems()
//                onSelectionChanged()
//            }
//        } catch (e: Exception) {
//            e.printStackTrace()
//        }
//    }

//    private fun callFireBase(map: Map<String, Any?>) {
//        try {
//            Cache.getDatabase().updateChildren(map).addOnCompleteListener { task: Task<Void?> ->
//                //                showProgressBar(false)
//                if (task.isSuccessful) Tools.showSnackBar(activity, "Chats Deleted Successfully") else Tools.showSnackBar(activity, "Couldn't be deleted", OnRetryListener { callFireBase(map) })
//            }
//        } catch (e: Exception) {
//            e.printStackTrace()
//        }
//    }

//    private fun unSelectAllItems() {
//        try {
//            for (d in selectedDialogs) d.unSelect()
//            dialogsAdapter = DialogSelectionAdapter(imageLoader)
//            initAdapter()
//            selectedDialogs.clear()
//        } catch (e: Exception) {
//            e.printStackTrace()
//        }
//    }


    fun initAdapter() {
        dialogsAdapter?.setItems(items)
        dialogsAdapter?.setOnDialogViewClickListener(this)
        dialogsAdapter?.onDialogViewLongClickListener = this
        dialogsList!!.setAdapter(dialogsAdapter)
    }

    override fun onDialogViewLongClick(view: View, dialog: Dialog) {
//        view.setBackgroundColor(resources.getColor(R.color.blue_50))
//        dialog.select()
//        selectedDialogs.add(dialog)
//        onSelectionChanged()
    }

    override fun onDialogViewClick(view: View, dialog: Dialog) {
//        if (selectedDialogs.size == 0) {
        val intent = Intent(activity, MessagesActivity::class.java)
        intent.putExtra("id", dialog.id)
        intent.putExtra("name", dialog.dialogName)
        intent.putExtra("avatar", dialog.dialogPhoto)
        Tools.launchActivity(activity, intent)
//        } else {
//            if (selectedDialogs.contains(dialog)) {
//                view.setBackgroundColor(Color.parseColor("#ffffff"))
//                dialog.unSelect()
//                selectedDialogs.remove(dialog)
//            } else {
//                view.setBackgroundColor(resources.getColor(R.color.blue_50))
//                dialog.select()
//                selectedDialogs.add(dialog)
//            }
//            onSelectionChanged()
//        }
    }
}