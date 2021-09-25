package com.celestial.progress.others

import android.app.NotificationChannel
import android.app.NotificationManager
import android.app.PendingIntent
import android.content.Context
import android.content.Intent
import android.os.Build
import android.provider.Settings
import android.service.notification.StatusBarNotification
import androidx.core.app.NotificationCompat
import androidx.core.content.ContextCompat.getSystemService
import com.celestial.progress.MainActivity
import com.celestial.progress.R
import com.celestial.progress.data.model.Counter


const val NOTIFICATION_CHANNEL_ID = "com.celestial.progress"
const val NOTIFICATION_CHANNEL_NAME= "casualChannel"

object NotificationHelper {

//    private var mContext: Context? = null
//    private var mNotificationManager: NotificationManager? = null
//    private var mBuilder: NotificationCompat.Builder? = null
    /**
     * Create and push the notification
     */
    fun createNotification(mContext: Context, counter: Counter) {
        var mNotificationManager: NotificationManager? = null
        var mBuilder: NotificationCompat.Builder? = null
        /**Creates an explicit intent for an Activity in your app */
        val resultIntent = Intent(mContext, MainActivity::class.java)
        resultIntent.addFlags(Intent.FLAG_ACTIVITY_NEW_TASK)
        val resultPendingIntent = PendingIntent.getActivity(
                mContext,
                0 /* Request code */, resultIntent,
                PendingIntent.FLAG_UPDATE_CURRENT
        )
        mBuilder = NotificationCompat.Builder(mContext!!)
                .setSmallIcon(R.drawable.ic_only_ic)
        mBuilder!!.setContentTitle(counter.title)
                .setContentText(counter.getDetail())
                .setAutoCancel(false)
                .setOngoing(true)
                .setColorized(true)
                .setColor(counter.color!!)
                .setSound(Settings.System.DEFAULT_NOTIFICATION_URI)
                .setContentIntent(resultPendingIntent)
        mNotificationManager =
                mContext!!.getSystemService(Context.NOTIFICATION_SERVICE) as NotificationManager
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O) {
            val importance = NotificationManager.IMPORTANCE_DEFAULT
            val notificationChannel = NotificationChannel(
                    NOTIFICATION_CHANNEL_ID,
                    NOTIFICATION_CHANNEL_NAME,
                    importance
            )
            notificationChannel.enableLights(false)
            assert(mNotificationManager != null)
            mBuilder!!.setChannelId(NOTIFICATION_CHANNEL_ID)
            mNotificationManager!!.createNotificationChannel(notificationChannel)
        }
        assert(mNotificationManager != null)
        mNotificationManager!!.notify(counter.id!! /* Request Code */, mBuilder!!.build())
    }

    fun cancelNotification(mContext: Context, counter: Counter){
       val notificationManager =  mContext.getSystemService(Context.NOTIFICATION_SERVICE) as NotificationManager
        notificationManager.cancel(counter.id!!)
    }

    fun checkNotification(mContext: Context, counter: Counter): Boolean{
        val mNotificationManager: NotificationManager = mContext.getSystemService(Context.NOTIFICATION_SERVICE) as NotificationManager
        val notifications: Array<StatusBarNotification> = mNotificationManager.activeNotifications

        if(notifications.isNotEmpty())
        {
            //you don't have notifications
            return false
        }
        else
        {
            for(n in notifications){
                if(n.id == counter.id){
                    return true
                }
            }
            return false
        }
    }
}