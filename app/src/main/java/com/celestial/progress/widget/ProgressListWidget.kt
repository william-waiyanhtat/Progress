package com.celestial.progress.widget

import android.app.PendingIntent
import android.app.PendingIntent.FLAG_UPDATE_CURRENT
import android.appwidget.AppWidgetManager
import android.appwidget.AppWidgetProvider
import android.content.ComponentName
import android.content.Context
import android.content.Intent
import android.os.Build
import android.util.Log
import android.widget.RemoteViews
import android.widget.Toast
import com.celestial.progress.MainActivity
import com.celestial.progress.R
import com.celestial.progress.widget.services.MyRemoteViewService


/**
 * Implementation of App Widget functionality.
 */
class ProgressListWidget : AppWidgetProvider() {


    val TAG = ProgressListWidget::class.java.name
    override fun onUpdate(
            context: Context,
            appWidgetManager: AppWidgetManager,
            appWidgetIds: IntArray
    ) {


        Toast.makeText(context, "Widget Update get called...", Toast.LENGTH_LONG).show()

        for (appWidgetId in appWidgetIds) {

            updateAppWidget(context, appWidgetManager, appWidgetId)
         //   appWidgetManager.notifyAppWidgetViewDataChanged(appWidgetId,R.layout.listview_item)
        }
    }

    override fun onReceive(context: Context?, intent: Intent?) {
        super.onReceive(context, intent)

        Log.d(TAG,"On Receive: ${intent.toString()}")
    }

    override fun onEnabled(context: Context) {
        // Enter relevant functionality for when the first widget is created
    }

    override fun onDisabled(context: Context) {
        // Enter relevant functionality for when the last widget is disabled
    }

    companion object{
         fun getPendingIntent(context: Context, value: Int): PendingIntent {
            //1
            val intent = Intent(context, MainActivity::class.java)
            //2
           // intent.action = Constants.ADD_COFFEE_INTENT
            //3
            //intent.putExtra(Constants.GRAMS_EXTRA, value)
            //4
            return PendingIntent.getActivity(context, value, intent, 0)
        }

        fun getRefreshIntent(context: Context): PendingIntent{
            val intent = Intent(context.applicationContext, ProgressListWidget::class.java)
            intent.action = AppWidgetManager.ACTION_APPWIDGET_UPDATE
            // Use an array and EXTRA_APPWIDGET_IDS instead of AppWidgetManager.EXTRA_APPWIDGET_ID,
            // since it seems the onUpdate() is only fired on that:
            val widgetManager = AppWidgetManager.getInstance(context)
            val ids = widgetManager.getAppWidgetIds(ComponentName(context, ProgressListWidget::class.java))
            if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.HONEYCOMB) {
                widgetManager.notifyAppWidgetViewDataChanged(ids, R.id.widget_list_view)
            }
            intent.putExtra(AppWidgetManager.EXTRA_APPWIDGET_IDS, ids)

            return PendingIntent.getBroadcast(context, 2, intent,FLAG_UPDATE_CURRENT )
           // context.sendBroadcast(intent)

        }

        internal fun updateAppWidget(
                context: Context,
                appWidgetManager: AppWidgetManager,
                appWidgetId: Int
        ) {
            // Construct the RemoteViews object
            val views = RemoteViews(context.packageName, R.layout.progress_list_widget)

            val intent = Intent(context, MyRemoteViewService::class.java)

            views.setRemoteAdapter(R.id.widget_list_view, intent)
            // views.setTextViewText(R.id.appwidget_text, widgetText)
            views.setOnClickPendingIntent(R.id.add_appWidget, ProgressListWidget.getPendingIntent(context, 1))
            views.setOnClickPendingIntent(R.id.btn_refresh, ProgressListWidget.getRefreshIntent(context))
            // Instruct the widget manager to update the widget
            appWidgetManager.updateAppWidget(appWidgetId, views)
        }
    }

}

