package com.celestial.progress

import android.animation.Animator
import android.animation.AnimatorListenerAdapter
import android.animation.ValueAnimator
import android.content.Intent
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.os.Handler
import android.os.Looper
import com.celestial.progress.databinding.ActivitySplashBinding
import com.celestial.progress.onboard.ProgressOnBoard
import com.celestial.progress.others.Utils
import com.celestial.progress.ui.component.BigProgressBar

class SplashActivity : AppCompatActivity() {

    private var valueAnimator: ValueAnimator? = null
    private var binding: ActivitySplashBinding? = null
    private lateinit var progressBar: BigProgressBar

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)


        binding = ActivitySplashBinding.inflate(layoutInflater)

        progressBar = binding?.pbActivitysplash!!
        progressBar.progress = 0
        progressBar.duration = 1000
        progressBar.color1 = getColor(R.color.teal_200)
        progressBar.indeterminate = true


        binding?.tvAppNameSplash?.text = Utils.getApplicationName(this)

        val view = binding?.root
        setContentView(view)

        Handler(Looper.myLooper()!!).postDelayed({
            startActivity(Intent(this@SplashActivity, MainActivity::class.java))
                    finish()

        },1000)

//        valueAnimator = ValueAnimator.ofInt(0, 100)
//        valueAnimator.apply {
//            this?.duration = 1000L
//            this?.addUpdateListener {
//                progressBar.progress = it.animatedValue as Int
//            }
//            this?.addListener(object : AnimatorListenerAdapter() {
//                override fun onAnimationEnd(animation: Animator?) {
//                    startActivity(Intent(this@SplashActivity, MainActivity::class.java))
//                    finish()
//                }
//            })
//
//            this?.start()
//        }
    }

    override fun onDestroy() {
        valueAnimator = null
        binding = null

        super.onDestroy()
    }

}