package com.celestial.progress.onboard

import android.graphics.Color
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import androidx.fragment.app.Fragment
import com.celestial.progress.R
import com.limerse.onboard.OnboardAdvanced
import com.limerse.onboard.OnboardFragment

class ProgressOnBoard : OnboardAdvanced() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

        addSlide(OnboardFragment.newInstance(
            title = "The title of your slide",
            description = "A description that will be shown on the bottom",
            resourceId = R.drawable.ic_only_ic, //or R.raw.your_json for LottieAnimationView
            backgroundDrawable = R.color.teal_700,
            titleColor = Color.YELLOW,
            descriptionColor = Color.RED,
            backgroundColor = Color.BLUE,
           // titleTypefaceFontRes = R.font.opensans_regular,
        //    descriptionTypefaceFontRes = R.font.opensans_regular,
        //    isLottie = true //To hide the imageView and enable the LottieAnimationView
        ))

        addSlide(OnboardFragment.newInstance(
            title = "The title of your slide",
            description = "A description that will be shown on the bottom",
            //     resourceId = R.drawable.ic_only_ic, //or R.raw.your_json for LottieAnimationView
            backgroundDrawable = R.drawable.ic_only_ic,
            titleColor = Color.YELLOW,
            descriptionColor = Color.RED,
            backgroundColor = Color.BLUE,
            // titleTypefaceFontRes = R.font.opensans_regular,
            //    descriptionTypefaceFontRes = R.font.opensans_regular,
            //    isLottie = true //To hide the imageView and enable the LottieAnimationView
        ))


    }

    override fun onSkipPressed(currentFragment: Fragment?) {
        super.onSkipPressed(currentFragment)
        finish()
    }

    override fun onDonePressed(currentFragment: Fragment?) {
        super.onDonePressed(currentFragment)
        finish()
    }
}