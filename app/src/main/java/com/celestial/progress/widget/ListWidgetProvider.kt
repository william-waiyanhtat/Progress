package com.celestial.progress.widget


import android.app.PendingIntent
import android.appwidget.AppWidgetManager
import android.appwidget.AppWidgetProvider
import android.content.Context
import android.content.Intent
import android.widget.RemoteViews
import com.celestial.progress.R
import com.celestial.progress.data.CounterRepository

import java.util.*
import javax.inject.Inject


class ListWidgetProvider @Inject constructor(val repo: CounterRepository): AppWidgetProvider() {


    override fun onUpdate(
        context: Context?,
        appWidgetManager: AppWidgetManager?,
        appWidgetIds: IntArray?
    ) {
        super.onUpdate(context, appWidgetManager, appWidgetIds)

        val count: Int = appWidgetIds?.size ?: 0
        for (i in 0 until count) {
            val widgetId = appWidgetIds!![i]
            val number = String.format("%03d", Random().nextInt(900) + 100)
            val remoteViews = RemoteViews(
                context!!.packageName,
                R.layout.listview_item
            )
            remoteViews.setTextViewText(R.id.tv_note_widget_list, number)
//            val intent = Intent(context, ListWidgetProvider::class.java)
//            intent.action = AppWidgetManager.ACTION_APPWIDGET_UPDATE
//            intent.putExtra(AppWidgetManager.EXTRA_APPWIDGET_IDS, appWidgetIds)
//            val pendingIntent = PendingIntent.getBroadcast(
//                context,
//                0, intent, PendingIntent.FLAG_UPDATE_CURRENT
//            )
           // remoteViews.setOnClickPendingIntent(R.id.actionButton, pendingIntent)
            appWidgetManager!!.updateAppWidget(widgetId, remoteViews)
        }
    }
}