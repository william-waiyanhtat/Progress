package com.celestial.progress.services

import android.app.Service
import android.appwidget.AppWidgetManager
import android.content.Intent
import android.os.IBinder
import com.celestial.progress.widget.ProgressListWidget

class ProgressWidgetService : Service() {

    override fun onBind(intent: Intent): IBinder? {
        TODO("Return the communication channel to the service.")
        return null
    }

    override fun onStartCommand(intent: Intent?, flags: Int, startId: Int): Int {

        val appWidgetManager = AppWidgetManager.getInstance(this)
        val allWidgetIds = intent?.getIntArrayExtra(AppWidgetManager.EXTRA_APPWIDGET_ID)
        if(allWidgetIds!=null){
            for(appWidgetId in allWidgetIds){
                ProgressListWidget.updateAppWidget(this,appWidgetManager,appWidgetId)
            }
        }

        return super.onStartCommand(intent, flags, startId)
    }
}