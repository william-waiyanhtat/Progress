package com.celestial.progress

import android.app.Application
import android.content.IntentFilter
import com.celestial.progress.others.Constants
import com.celestial.progress.others.NotificationReceiver
import dagger.hilt.android.HiltAndroidApp

@HiltAndroidApp
class ProgressApp: Application() {


    override fun onCreate() {
        super.onCreate()
        registerReceiver()
    }


    private fun registerReceiver(){
        val intentFilter: IntentFilter = IntentFilter().apply {
            addAction(Constants.notificationUpdateAction)
        }
        val notificationReceiver = NotificationReceiver()
        registerReceiver(notificationReceiver,intentFilter)
    }
}