package com.esselion.pass.activities

import android.animation.Animator
import android.content.Intent
import android.os.Bundle
import android.util.Log
import androidx.appcompat.app.AppCompatActivity
import com.airbnb.lottie.LottieAnimationView
import com.esselion.pass.R

class SplashActivity : AppCompatActivity() {

    val TAG: String = SplashActivity::class.java.simpleName

    public override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_splash)

        val lottieAnimationView = findViewById<LottieAnimationView>(R.id.logo)
        lottieAnimationView.addAnimatorListener(object : Animator.AnimatorListener {
            override fun onAnimationStart(animation: Animator) {
                Log.i(TAG, "Animation start")
            }

            override fun onAnimationEnd(animation: Animator) {
                Log.i(TAG, "Animation end")
                startActivity(Intent(this@SplashActivity, MainActivity::class.java))
                finish()
            }

            override fun onAnimationCancel(animation: Animator) {
                Log.i(TAG, "Animation cancel")
            }

            override fun onAnimationRepeat(animation: Animator) {
                Log.i(TAG, "Animation repeat")
            }
        })
    }

    override fun finish() {
        super.finish()
        overridePendingTransition(R.anim.slide_from_right, R.anim.slide_to_left)
    }

}
