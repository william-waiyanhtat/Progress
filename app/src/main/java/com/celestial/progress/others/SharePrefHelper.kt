package com.celestial.progress.others

import android.content.Context
import androidx.preference.PreferenceManager
import com.celestial.progress.R

object SharePrefHelper {

    fun getSharedPreferences(context: Context) =
        PreferenceManager.getDefaultSharedPreferences(context)

    fun getSharedPreferencesEditor(context: Context) =
        PreferenceManager.getDefaultSharedPreferences(context).edit()

    fun isAllNotificationOff(context: Context): Boolean {
        return getSharedPreferences(context).getBoolean(
            context.getString(R.string.pf_key_overallnoti),
            true
        )
    }

    fun isDefaultNotification(context: Context): Boolean {
        return getSharedPreferences(context).getBoolean(
            context.getString(R.string.pf_key_noti_style),
            false
        )
    }

    fun isGlimmerAnimationOff(context: Context): Boolean {
        return getSharedPreferences(context).getBoolean(
            context.getString(R.string.pf_key_pbar_animation),
            false
        )
    }


    fun isGuideShown(context: Context): Boolean {
        return getSharedPreferences(context).getBoolean(Constants.isGuideShown, false)
    }

    fun setGuideShown(context: Context) {
        getSharedPreferencesEditor(context).putBoolean(Constants.isGuideShown, true).apply()
    }

    fun isOnboardingShown(context: Context): Boolean {
        return getSharedPreferences(context).getBoolean(Constants.isOnboardShownKey, false)
    }

    fun setOnBoardingShow(context: Context) {
        getSharedPreferencesEditor(context).putBoolean(Constants.isOnboardShownKey, true).apply()
    }

}