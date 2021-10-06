package com.celestial.progress.others

import android.app.AlarmManager
import android.app.PendingIntent
import android.appwidget.AppWidgetManager
import android.content.*
import android.os.Build
import android.widget.Toast
import androidx.annotation.CallSuper
import com.celestial.progress.R
import com.celestial.progress.data.CounterRepository
import com.celestial.progress.others.Constants.notificationUpdateAction
import com.celestial.progress.widget.ProgressListWidget
import dagger.hilt.android.AndroidEntryPoint
import java.util.*
import javax.inject.Inject

@AndroidEntryPoint
class NotificationReceiver: HiltBroadcastReceiver() {

    @Inject
    lateinit var counterRepository: CounterRepository

    override fun onReceive(context: Context?, intent: Intent?) {
        super.onReceive(context, intent)
        NotificationController.fetchAndUpdateNotifiedCounterList(counterRepository,context!!)
        setAlarmToUpdateAtDayChanged(context)
    }

    private fun setAlarmToUpdateAtDayChanged(context: Context){
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

    private fun getRefreshIntent(context: Context): PendingIntent {
        val intent = Intent(context.applicationContext, NotificationReceiver::class.java)
        intent.action = notificationUpdateAction
        return PendingIntent.getBroadcast(context, 202, intent, PendingIntent.FLAG_UPDATE_CURRENT)
    }

}

abstract class HiltBroadcastReceiver: BroadcastReceiver() {
    @CallSuper
    override fun onReceive(context: Context?, intent: Intent?) {

    }

}