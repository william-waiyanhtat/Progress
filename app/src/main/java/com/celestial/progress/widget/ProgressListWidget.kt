package com.celestial.progress.widget

import android.app.AlarmManager
import android.app.PendingIntent
import android.app.PendingIntent.FLAG_UPDATE_CURRENT
import android.appwidget.AppWidgetManager
import android.appwidget.AppWidgetProvider
import android.content.ComponentName
import android.content.Context
import android.content.Intent
import android.os.Build
import android.os.SystemClock
import android.util.Log
import android.widget.RemoteViews
import android.widget.Toast
import com.celestial.progress.MainActivity
import com.celestial.progress.R
import com.celestial.progress.widget.services.MyRemoteViewService
import java.util.*


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
        for (appWidgetId in appWidgetIds) {
            updateAppWidget(context, appWidgetManager, appWidgetId)
        }
    }

    override fun onReceive(context: Context?, intent: Intent?) {
        super.onReceive(context, intent)
        val action = intent?.getAction()

        if (action.equals(Intent.ACTION_TIME_CHANGED) ||
                action.equals(Intent.ACTION_TIMEZONE_CHANGED)) {
            getRefreshIntent(context!!).send()
            Log.d(TAG,"On Date or Time Changed")
        }else{
            Log.d(TAG,"On AppWidgt Intent")
            setAlarmToUpdateAtDayChanged(context!!)
        }
    }

    override fun onEnabled(context: Context) {
        // Enter relevant functionality for when the first widget is created
    }

    override fun onDisabled(context: Context) {
        // Enter relevant functionality for when the last widget is disabled
        cancelUpdatePendingIntentAlarmManager(context)

    }

    override fun onDeleted(context: Context?, appWidgetIds: IntArray?) {
        super.onDeleted(context, appWidgetIds)

    }

    companion object{
        val TAG = ProgressListWidget::class.simpleName

         private fun getPendingIntent(context: Context, value: Int): PendingIntent {
            //1
            val intent = Intent(context, MainActivity::class.java)
            return PendingIntent.getActivity(context, value, intent, 0)
        }

         private fun getRefreshIntent(context: Context): PendingIntent{
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
            return PendingIntent.getBroadcast(context, 2, intent, FLAG_UPDATE_CURRENT)
        }

        fun setAlarmToUpdateAtDayChanged(context: Context){
            val pendingIntent = getRefreshIntent(context)

            val alarmManager = context.getSystemService(Context.ALARM_SERVICE) as AlarmManager


            val calendar = Calendar.getInstance().apply {
                set(Calendar.HOUR_OF_DAY, 0)
                set(Calendar.MINUTE, 0)
                set(Calendar.SECOND, 0)
                set(Calendar.MILLISECOND, 0)
            }

            calendar.add(Calendar.DAY_OF_YEAR,1)
            alarmManager.setExact(AlarmManager.RTC_WAKEUP,calendar.timeInMillis,pendingIntent)
        }

        internal fun cancelUpdatePendingIntentAlarmManager(context: Context){
            val pendingIntent = getRefreshIntent(context)
            val alarmManager = context.getSystemService(Context.ALARM_SERVICE) as AlarmManager
            pendingIntent.cancel()
            alarmManager.cancel(pendingIntent)
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
            views.setOnClickPendingIntent(R.id.add_appWidget, ProgressListWidget.getPendingIntent(context, 1))
            views.setOnClickPendingIntent(R.id.btn_refresh, ProgressListWidget.getRefreshIntent(context))
            // Instruct the widget manager to update the widget
            appWidgetManager.updateAppWidget(appWidgetId, views)
        }
    }

}

