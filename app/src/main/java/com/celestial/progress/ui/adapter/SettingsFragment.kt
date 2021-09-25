package com.celestial.progress.ui.adapter

import android.os.Bundle
import androidx.preference.PreferenceFragmentCompat
import com.celestial.progress.MainActivity
import com.celestial.progress.R

class SettingsFragment : PreferenceFragmentCompat() {

    override fun onCreatePreferences(savedInstanceState: Bundle?, rootKey: String?) {
        val activity = (activity as MainActivity)
        activity.showHideToolbar(true)
        activity.setTitle("Setting")
        activity.toolbar.navigationIcon = activity.getDrawable(R.drawable.ic_back)
        setPreferencesFromResource(R.xml.root_preferences, rootKey)
    }
}