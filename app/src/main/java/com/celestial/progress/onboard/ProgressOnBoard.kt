package com.celestial.progress.onboard

import android.content.Intent
import android.graphics.Color
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import androidx.fragment.app.Fragment
import com.celestial.progress.MainActivity
import com.celestial.progress.R
import com.limerse.onboard.OnboardAdvanced
import com.limerse.onboard.OnboardFragment

class ProgressOnBoard : OnboardAdvanced() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

        addSlide(
            OnboardFragment.newInstance(
                title = getString(R.string.slide1_title),
                description = getString(R.string.slide1_description),
                resourceId = R.drawable.slider1_content, //or R.raw.your_json for LottieAnimationView
                backgroundDrawable = R.drawable.slider1_background,
                titleColor = Color.BLACK,
                descriptionColor = Color.GRAY,
                backgroundColor = Color.BLUE,
                // titleTypefaceFontRes = R.font.opensans_regular,
                //    descriptionTypefaceFontRes = R.font.opensans_regular,
                //    isLottie = true //To hide the imageView and enable the LottieAnimationView
            )
        )

        addSlide(
            OnboardFragment.newInstance(
                title = getString(R.string.slide2_title),
                description = getString(R.string.slide2_description),
                resourceId = R.drawable.slider2_content, //or R.raw.your_json for LottieAnimationView
                backgroundDrawable = R.drawable.slider2_background,
                titleColor = Color.BLACK,
                descriptionColor = Color.GRAY,
                backgroundColor = Color.BLUE,
                // titleTypefaceFontRes = R.font.opensans_regular,
                //    descriptionTypefaceFontRes = R.font.opensans_regular,
                //    isLottie = true //To hide the imageView and enable the LottieAnimationView
            )
        )

        addSlide(
            OnboardFragment.newInstance(
                title = getString(R.string.slide3_title),
                description = getString(R.string.slide3_description),
                resourceId = R.drawable.slider3_content, //or R.raw.your_json for LottieAnimationView
                backgroundDrawable = R.drawable.slider3_background,
                titleColor = Color.BLACK,
                descriptionColor = Color.GRAY,
                backgroundColor = Color.BLUE,
                 //titleTypefaceFontRes = R.font.opensans_regular,
               //    descriptionTypefaceFontRes = R.font.opensans_regular,
                //    isLottie = true //To hide the imageView and enable the LottieAnimationView
            )
        )

        addSlide(
            OnboardFragment.newInstance(
                title = getString(R.string.slide4_title),
                description = getString(R.string.slide4_description),
                resourceId = R.drawable.slider4_content, //or R.raw.your_json for LottieAnimationView
                backgroundDrawable = R.drawable.slider4_background,
                titleColor = Color.BLACK,
                descriptionColor = Color.GRAY,
                backgroundColor = Color.BLUE,
                // titleTypefaceFontRes = R.font.opensans_regular,
                //    descriptionTypefaceFontRes = R.font.opensans_regular,
                //    isLottie = true //To hide the imageView and enable the LottieAnimationView
            )
        )

        addSlide(
            OnboardFragment.newInstance(
                title = getString(R.string.slide5_title),
                description = getString(R.string.slide5_description),
                     resourceId = R.drawable.slider5_content, //or R.raw.your_json for LottieAnimationView
                backgroundDrawable = R.drawable.slider5_background,
                titleColor = Color.BLACK,
                descriptionColor = Color.GRAY,
                backgroundColor = Color.BLUE,
                // titleTypefaceFontRes = R.font.opensans_regular,
                //    descriptionTypefaceFontRes = R.font.opensans_regular,
                //    isLottie = true //To hide the imageView and enable the LottieAnimationView
            )
        )


    }

    override fun onSkipPressed(currentFragment: Fragment?) {
        super.onSkipPressed(currentFragment)
        finish()
    }

    override fun onDonePressed(currentFragment: Fragment?) {
        super.onDonePressed(currentFragment)
        startActivity(Intent(this,MainActivity::class.java))
        finish()
    }
}