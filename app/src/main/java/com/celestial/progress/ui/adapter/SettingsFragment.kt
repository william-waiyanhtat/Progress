package com.celestial.progress.ui.adapter

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.activity.viewModels
import androidx.fragment.app.viewModels
import androidx.lifecycle.Observer
import androidx.lifecycle.ViewModelProvider
import androidx.preference.Preference
import androidx.preference.PreferenceFragmentCompat
import androidx.preference.SwitchPreference
import androidx.preference.SwitchPreferenceCompat
import com.celestial.progress.MainActivity
import com.celestial.progress.R
import com.celestial.progress.others.NotificationHelper
import com.celestial.progress.ui.CounterViewModel
import com.celestial.progress.widget.WidgetConfigViewModel

class SettingsFragment : PreferenceFragmentCompat() {

    private lateinit var notiOnOff: SwitchPreferenceCompat
    private lateinit var defaultNotiOnOff: SwitchPreferenceCompat

    lateinit var counterViewModel: CounterViewModel

    override fun onCreateView(inflater: LayoutInflater, container: ViewGroup?, savedInstanceState: Bundle?): View? {
        counterViewModel = ViewModelProvider(requireActivity())[CounterViewModel::class.java]

        return super.onCreateView(inflater, container, savedInstanceState)
    }

    override fun onCreatePreferences(savedInstanceState: Bundle?, rootKey: String?) {
        val activity = (activity as MainActivity)
        activity.showHideToolbar(true)
        activity.setTitle("Setting")
        activity.toolbar.navigationIcon = activity.getDrawable(R.drawable.ic_back)
        setPreferencesFromResource(R.xml.root_preferences, rootKey)

        listenNotificationOnOff()
        listenDefaultNotificationOnOff()
    }

    private fun listenDefaultNotificationOnOff() {
        defaultNotiOnOff = findPreference<SwitchPreferenceCompat>(getString(R.string.pf_key_noti_style))!!
        defaultNotiOnOff.setOnPreferenceClickListener { it ->
            revokeAllIssueNotification()
            return@setOnPreferenceClickListener true

        }
    }

    private fun listenNotificationOnOff() {
        notiOnOff = findPreference<SwitchPreferenceCompat>(getString(R.string.pf_key_overallnoti))!!
        notiOnOff.setOnPreferenceChangeListener { preference: Preference, any: Any ->
            revokeAllIssueNotification()
            return@setOnPreferenceChangeListener true

        }

    }

    private fun revokeAllIssueNotification() {
        counterViewModel?.fetchNotificationOnCounterList().observe(viewLifecycleOwner, Observer {
            if (notiOnOff.isChecked) {
                for (n in it) {
                    if (NotificationHelper.checkNotification(requireContext(), n)) {
                        NotificationHelper.cancelNotification(requireContext(), n)
                    }
                    NotificationHelper.createNotification(requireContext(), n)

                }
            } else {
                for (n in it) {
                    if (NotificationHelper.checkNotification(requireContext(), n)) {
                        NotificationHelper.cancelNotification(requireContext(), n)
                    }


                }


            }

        })
    }

}