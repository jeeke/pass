package com.esselion.pass.fragments

import android.content.Context
import android.content.Intent
import android.graphics.drawable.Animatable
import android.graphics.drawable.Drawable
import android.os.Bundle
import android.util.Log
import android.view.*
import androidx.appcompat.app.AppCompatActivity
import androidx.fragment.app.Fragment
import androidx.fragment.app.FragmentPagerAdapter
import androidx.vectordrawable.graphics.drawable.Animatable2Compat
import androidx.vectordrawable.graphics.drawable.AnimatedVectorDrawableCompat
import androidx.viewpager.widget.ViewPager
import com.bumptech.glide.Glide
import com.bumptech.glide.request.RequestOptions
import com.esselion.pass.FBMsgService
import com.esselion.pass.R
import com.esselion.pass.activities.NotificationActivity
import com.esselion.pass.activities.SettingActivity
import com.esselion.pass.adapters.ProfileTabAdapter
import com.esselion.pass.models.Profile
import com.esselion.pass.util.Cache
import com.esselion.pass.util.Contracts
import com.esselion.pass.util.SharedPrefAdapter
import com.esselion.pass.util.Tools
import com.google.android.material.tabs.TabLayout
import com.google.firebase.database.DataSnapshot
import com.google.firebase.database.DatabaseError
import com.google.firebase.database.DatabaseReference
import com.google.firebase.database.ValueEventListener
import kotlinx.android.synthetic.main.frag_profile_new.*
import kotlinx.android.synthetic.main.profile_main.*

class ProfileFragment : Fragment() {
    interface ActivityListener {
        val mine: Boolean
        val uId: String
        val uName: String
        val imageUrl: String
    }

    fun visibilityValue(visibility: Boolean): Int {
        return if (visibility) View.VISIBLE else View.GONE
    }

    val vMap = arrayOf(View.VISIBLE, View.GONE, View.INVISIBLE)

    private var mListener: ActivityListener? = null
    private var mDatabase: DatabaseReference? = null

    override fun onCreateView(inflater: LayoutInflater, container: ViewGroup?,
                              savedInstanceState: Bundle?): View? {
        mDatabase = Cache.getDatabase()
        val v = inflater.inflate(R.layout.frag_profile_new, container, false)
        val tabs: TabLayout = v.findViewById(R.id.tab_layout)
        val viewPager: ViewPager = v.findViewById(R.id.view_pager)
        viewPager.adapter = ProfileTabAdapter(childFragmentManager, FragmentPagerAdapter.BEHAVIOR_RESUME_ONLY_CURRENT_FRAGMENT)
        tabs.setupWithViewPager(v.findViewById(R.id.view_pager))

        val mine = mListener?.mine == true
        setHasOptionsMenu(mine)
        updateProfileImage(mine)
        edit_details.visibility = visibilityValue(mine)
        profile_action_btns.visibility = visibilityValue(!mine)
        return v
    }

    private fun callFirebase() {
        val ref = mDatabase?.child("Profiles${mListener?.uId}")
        ref?.addListenerForSingleValueEvent(object : ValueEventListener {
            override fun onCancelled(p0: DatabaseError) {
                Log.e("error", p0.toString())
            }

            override fun onDataChange(p0: DataSnapshot) {
                val profile: Profile? = p0.getValue(Profile::class.java)
                about.text = profile?.about
            }

        })
    }

    private fun updateProfileImage(mine: Boolean) {
        if (mine) {
            Cache.mUser = null
            Cache.getUser().photoUrl
            Glide.with(profile_image.context).load(mListener?.imageUrl)
                    .apply(RequestOptions.circleCropTransform()).into(profile_image)
        } else {
            Glide.with(profile_image.context).load(mListener?.imageUrl)
                    .apply(RequestOptions.circleCropTransform()).into(profile_image)
        }
    }


    override fun onActivityCreated(savedInstanceState: Bundle?) {
        super.onActivityCreated(savedInstanceState)
        (activity as? AppCompatActivity)?.setSupportActionBar(toolbar)
        callFirebase()
    }

    override fun onStop() {
        super.onStop()
        FBMsgService.unregisterNotificationListener()
    }

    override fun onResume() {
        super.onResume()
        activity?.invalidateOptionsMenu()
        FBMsgService.registerNotificationListener { activity?.invalidateOptionsMenu() }
    }

    override fun onPrepareOptionsMenu(menu: Menu) {
        super.onPrepareOptionsMenu(menu)
        if (SharedPrefAdapter.getInstance().hasUnseenNotification()) {
            menu.getItem(0).setIcon(R.drawable.ic_bell_avd)
            val menuItem = menu.getItem(0).icon
            val animatable = menuItem as Animatable
            animatable.start()
            AnimatedVectorDrawableCompat.registerAnimationCallback(menuItem, object : Animatable2Compat.AnimationCallback() {
                override fun onAnimationEnd(drawable: Drawable) {
                    super.onAnimationEnd(drawable)
                    animatable.start()
                }
            })
        }
    }

    override fun onCreateOptionsMenu(menu: Menu, inflater: MenuInflater) {
        inflater.inflate(R.menu.profile_menu, menu)
        super.onCreateOptionsMenu(menu, inflater)
    }

    override fun onOptionsItemSelected(item: MenuItem): Boolean {
        if (item.itemId == R.id.notification) {
            Tools.launchActivityForResult(activity, Intent(context, NotificationActivity::class.java), Contracts.CODE_NOTIFICATION_ACTIVITY)
            SharedPrefAdapter.getInstance().setHasNotification()
            activity?.invalidateOptionsMenu()
        } else if (item.itemId == R.id.setting) {
            Tools.launchActivityForResult(activity, Intent(context, SettingActivity::class.java), Contracts.CODE_SETTINGS_ACTIVITY)
            return true
        }
        return false
    }

    override fun onAttach(context: Context) {
        super.onAttach(context)
        mListener = activity as ActivityListener?
    }

    override fun onDetach() {
        super.onDetach()
        mListener = null
    }
}