package com.esselion.pass

import android.content.Context
import android.content.DialogInterface
import android.os.Bundle
import android.text.InputType
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.EditText
import androidx.fragment.app.Fragment
import com.esselion.pass.activities.BaseActivity
import com.esselion.pass.fragments.ProfileFragment
import com.esselion.pass.models.Profile
import com.esselion.pass.models.Rating
import com.esselion.pass.util.ChipAdapter
import com.esselion.pass.util.ChipAdapter.OnChipRemovedListener
import com.esselion.pass.util.Contracts
import com.esselion.pass.util.Tools
import com.google.android.material.dialog.MaterialAlertDialogBuilder
import com.google.firebase.database.DataSnapshot
import com.google.firebase.database.DatabaseError
import com.google.firebase.database.DatabaseReference
import com.google.firebase.database.ValueEventListener
import kotlinx.android.synthetic.main.activity_location.*
import kotlinx.android.synthetic.main.profile_person_detail.*
import kotlinx.android.synthetic.main.profile_skills.*
import kotlinx.android.synthetic.main.profile_stats.*
import java.util.*


class InfoFragment : Fragment() {
    private var adapter: ChipAdapter? = null
    private var mListener: ProfileFragment.ActivityListener? = null
    private val mDatabase: DatabaseReference? = null


    override fun onCreateView(inflater: LayoutInflater, container: ViewGroup?,
                              savedInstanceState: Bundle?): View? {
        // Inflate the layout for this fragment
        return inflater.inflate(R.layout.fragment_info, container, false)
    }

    override fun onActivityCreated(savedInstanceState: Bundle?) {
        super.onActivityCreated(savedInstanceState)
        initViews()
        callFirebase()
        forMe(mListener?.mine)
    }

    private fun getServer(): Server? {
        val activity = activity as BaseActivity?
        return activity?.server
    }

    private fun initViews() {
        adapter = ChipAdapter(OnChipRemovedListener { title: String? ->
            val server: Server? = getServer()
            server?.removeSkill(title, mListener?.uId)
        }, skillschip, ArrayList())
    }

    override fun onAttach(context: Context) {
        super.onAttach(context)
        mListener = activity as? ProfileFragment.ActivityListener
    }


    override fun onDetach() {
        super.onDetach()
        mListener = null
    }

    private fun setupSkills(skills: ArrayList<String>) {
        adapter = ChipAdapter(OnChipRemovedListener { title: String? ->
            val server: Server? = getServer()
            server?.removeSkill(title, mListener?.uId)
        }, skillschip, skills)
    }

    private fun setupStats(p: Profile) {
        val rating = p.posterRating
        val rating2 = p.taskerRating
        if (rating + rating2 == 0f) {
            layoutstats?.visibility = View.GONE
            lottie_anim?.visibility = View.VISIBLE
            layoutrating?.visibility = View.GONE
            divider11?.visibility = View.GONE
        } else {
            layoutrating?.visibility = View.VISIBLE
            lottie_anim?.visibility = View.GONE
            posterating?.rating = rating
            taskerrating?.rating = rating2
            val r1 = p.r1
            val r2 = p.r2
            val r3 = p.r3
            if (r1 + r2 + r3 != 0) {
                divider?.visibility = View.VISIBLE
                layoutstats?.visibility = View.VISIBLE
                progressontime?.progress = r1
                textViewontime?.text = "$r1%"
                progressquality?.progress = r2
                textViewquality?.text = "$r2%"
                progressbehaviour?.progress = r3
                textViewbehaviour?.text = "$r3%"
            }
        }
    }

    private fun forMe(mine: Boolean?) {
        if (mine == true) {
            addskill?.setOnClickListener {
                val input = EditText(context)
                input.inputType = InputType.TYPE_CLASS_TEXT
                val pad = Contracts.dpToPx(24)
                val p = Contracts.dpToPx(16)
                input.setPadding(pad, pad, pad, p)
                MaterialAlertDialogBuilder(Objects.requireNonNull(context), R.style.AlertDialogTheme).setTitle("ADD TAG").setView(input)
                        .setPositiveButton("ADD") { _: DialogInterface?, _: Int ->
                            val skill = input.text.toString()
                            if (adapter?.isSafe(skill) == false) {
                                Tools.showSnackBar(getActivity(), "Skill Already Exist or Empty")
                                return@setPositiveButton
                            }
                            val server: Server? = getServer()
                            server?.addSkill(skill, mListener?.uId)
                        }
                        .show()
                input.requestFocus()
            }
        } else {
            addskill?.visibility = View.GONE
        }
    }

    private fun callFirebase() {
        val r = mDatabase?.child("Profiles/${mListener?.uId}")
        r?.addValueEventListener(object : ValueEventListener {

            override fun onCancelled(snapshot: DatabaseError) {
                r.removeEventListener(this)
            }

            override fun onDataChange(snapshot: DataSnapshot) {
                val profile = snapshot.getValue(Profile::class.java)
                if (profile != null) {
                    profile.setByTasker(snapshot.child("Ratings/ByTasker").getValue(Rating::class.java))
                    profile.setByPoster(snapshot.child("Ratings/ByPoster").getValue(Rating::class.java))
                    skillschip?.removeAllViews()
                    for (skill in snapshot.child("Skills").children) {
                        profile.addSkill(skill.key)
                    }
                    setupStats(profile)
                    setupSkills(profile.skills)
                    settupdetail(profile)
                }
            }
        })
    }

    private fun settupdetail(p: Profile) {
        taskdone?.text = p.t_done.toString() + ""
        bucksearned?.text = "₹" + p.bucks
        taskposted?.text = p.t_posted.toString() + ""
    }
}
