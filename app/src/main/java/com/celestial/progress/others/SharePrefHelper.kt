package com.celestial.progress.others

import android.content.Context
import androidx.preference.PreferenceManager
import com.celestial.progress.R

object SharePrefHelper {

    fun getSharedPreferences(context: Context) = PreferenceManager.getDefaultSharedPreferences(context)

    fun getSharedPreferencesEditor(context: Context) = PreferenceManager.getDefaultSharedPreferences(context).edit()

    fun isAllNotificationOff(context: Context): Boolean {
        return getSharedPreferences(context).getBoolean(context.getString(R.string.pf_key_overallnoti), false)
    }

    fun isDefaultNotification(context: Context): Boolean {
        return getSharedPreferences(context).getBoolean(context.getString(R.string.pf_key_noti_style), false)
    }

    fun isGlimmerAnimationOff(context: Context): Boolean {
        return getSharedPreferences(context).getBoolean(context.getString(R.string.pf_key_pbar_animation), false)
    }
}