package com.celestial.progress.ui.adapter

import android.os.Bundle
import androidx.preference.PreferenceFragmentCompat
import com.celestial.progress.R

class SettingsFragment : PreferenceFragmentCompat() {

    override fun onCreatePreferences(savedInstanceState: Bundle?, rootKey: String?) {
        setPreferencesFromResource(R.xml.root_preferences, rootKey)
    }
}