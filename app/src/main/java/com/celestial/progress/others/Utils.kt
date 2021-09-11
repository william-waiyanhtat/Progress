package com.celestial.progress.others

import android.app.NotificationChannel
import android.app.NotificationManager
import android.app.PendingIntent
import android.content.Context
import android.content.Intent
import android.media.RingtoneManager
import android.os.Build
import androidx.core.app.NotificationCompat
import com.celestial.progress.R

object Utils {

     fun showNotification(context: Context, title: String, message: String) {
         val intent = Intent("OPEN_ACTIVITY_1")
         val notify_no = 1

        val pendingIntent =
            PendingIntent.getActivity(context, notify_no, intent, PendingIntent.FLAG_ONE_SHOT)
        val channel_id = "fcm_default_channel"
        val default_sound_uri = RingtoneManager.getDefaultUri(RingtoneManager.TYPE_NOTIFICATION)
        val notifBuilder: NotificationCompat.Builder = NotificationCompat.Builder(context, channel_id)
            .setSmallIcon(R.drawable.ic_app1)
            .setContentTitle(title)
            .setContentText(message).setAutoCancel(true)
            .setSound(default_sound_uri)
            .setContentIntent(pendingIntent)
        val notificationManager =context.
            getSystemService(Context.NOTIFICATION_SERVICE) as NotificationManager
        try {
            if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O) {
                val channel = NotificationChannel(
                        channel_id, "Default Channel", NotificationManager.IMPORTANCE_DEFAULT
                )
                channel.setShowBadge(true)
                notificationManager.createNotificationChannel(channel)
            }
            notificationManager.notify(notify_no, notifBuilder.build())
        } catch (e: NullPointerException) {
            e.printStackTrace()
        }
    }
}