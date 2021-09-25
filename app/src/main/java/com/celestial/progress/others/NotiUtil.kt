package com.celestial.progress.others


import android.app.Notification
import android.app.NotificationChannel
import android.app.NotificationManager
import android.app.PendingIntent
import android.content.Context
import android.content.ContextWrapper
import android.content.Intent
import android.graphics.Color
import android.os.Build
import android.provider.Settings
import androidx.annotation.RequiresApi
import androidx.core.app.NotificationCompat
import com.celestial.progress.MainActivity
import com.celestial.progress.R


class NotiUtil(context: Context): ContextWrapper(context) {

    fun createNotification(context: Context){
        if(Build.VERSION.SDK_INT>=Build.VERSION_CODES.O){
            createNotiOreoAndAbove(context)
        }else{
            createNoti(context)
        }

    }

    private fun createNoti(context: Context) {
        val builder: NotificationCompat.Builder = NotificationCompat.Builder(context)
            .setSmallIcon(R.drawable.ic_noti_ic) //set icon for notification
            .setContentTitle("Notifications Example") //set title of notification
            .setContentText("This is a notification message") //this is notification message
            .setAutoCancel(true) // makes auto cancel of notification
            .setPriority(NotificationCompat.PRIORITY_DEFAULT) //set priority of notification
            .setOngoing(true)


        val notificationIntent = Intent(context, MainActivity::class.java)
        notificationIntent.addFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP)
        //notification message will get at NotificationView
        //notification message will get at NotificationView
        notificationIntent.putExtra("message", "This is a notification message")

        val pendingIntent = PendingIntent.getActivity(
                context, 0, notificationIntent,
                PendingIntent.FLAG_UPDATE_CURRENT
        )
        builder.setContentIntent(pendingIntent)

        // Add as notification

        // Add as notification
        val manager = context.getSystemService(Context.NOTIFICATION_SERVICE) as NotificationManager?
        manager!!.notify(0, builder.build())
    }

    private fun createNotiOreoAndAbove(context: Context) {
      //  manager!!.notify(0, builder.build())
    }

    @RequiresApi(Build.VERSION_CODES.O)
    fun createChannel(channelID: String, channelName: String){
        val notificationChannel = NotificationChannel(channelID, channelName, NotificationManager.IMPORTANCE_HIGH)
        notificationChannel.enableLights(false)
        notificationChannel.lightColor = Color.CYAN
        notificationChannel.setShowBadge(false)
        notificationChannel.lockscreenVisibility = Notification.VISIBILITY_PRIVATE
        getNotificationManager().createNotificationChannel(notificationChannel)
    }

    @RequiresApi(Build.VERSION_CODES.O)
    fun getNotification(title: String, body: String, channelID: String): Notification.Builder{
        createChannel(channelID,"TEST CHANNEL")

        return Notification.Builder(applicationContext, channelID)
                .setContentTitle(title)
                .setContentText(body)
                .setSmallIcon(R.drawable.ic_only_ic)
                .setAutoCancel(false)
                .setOngoing(true)
                .setColorized(true)
                .setProgress(100,40,false)
                .setColor(Color.CYAN)

    }


    private fun getNotificationManager(): NotificationManager {
        return getSystemService(Context.NOTIFICATION_SERVICE) as NotificationManager
    }

    fun notify(id: Int, notification: Notification.Builder){
        getNotificationManager().notify(id, notification.build())
    }

    fun goToNotificationSettings(channel: String?) {
        val i = Intent(Settings.ACTION_CHANNEL_NOTIFICATION_SETTINGS)
        i.putExtra(Settings.EXTRA_APP_PACKAGE, packageName)
        i.putExtra(Settings.EXTRA_CHANNEL_ID, channel)

//Start the Activity with the intent//
        startActivity(i)
    }


}