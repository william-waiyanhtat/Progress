package com.celestial.progress.others

import android.app.NotificationChannel
import android.app.NotificationManager
import android.app.PendingIntent
import android.content.Context
import android.content.Intent
import android.content.res.Resources
import android.graphics.Bitmap
import android.graphics.Canvas
import android.graphics.Paint
import android.graphics.RectF
import android.graphics.drawable.BitmapDrawable
import android.graphics.drawable.Drawable
import android.media.RingtoneManager
import android.os.Build
import android.util.Log
import androidx.annotation.RequiresApi
import androidx.core.app.NotificationCompat
import com.celestial.progress.R
import com.celestial.progress.ui.component.DeviceUtils


object Utils {

    val TAG = Utils::class.java.name

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

    @RequiresApi(Build.VERSION_CODES.O)
    fun showNotificationOreo(notiID: Int, channelId: String, title: String, context: Context){
            val notiHelper = NotiUtil(context)
            val notificationBuilder = notiHelper.getNotification(title, "This is text notification body message", channelId)
            if(notificationBuilder != null){
                notiHelper.notify(notiID, notificationBuilder)
                Log.d(TAG, " NOti OReo Get Called")
            }

    }


    fun drawableToBitmap(drawable: Drawable): Bitmap? {
        var bitmap: Bitmap? = null
        if (drawable is BitmapDrawable) {
            val bitmapDrawable = drawable
            if (bitmapDrawable.bitmap != null) {
                return bitmapDrawable.bitmap
            }
        }
        bitmap = if (drawable.intrinsicWidth <= 0 || drawable.intrinsicHeight <= 0) {
            Bitmap.createBitmap(1, 1, Bitmap.Config.ARGB_8888) // Single color bitmap will be created of 1x1 pixel
        } else {
            Bitmap.createBitmap(drawable.intrinsicWidth, drawable.intrinsicHeight, Bitmap.Config.ARGB_8888)
        }
        val canvas = Canvas(bitmap)
        drawable.setBounds(0, 0, canvas.getWidth(), canvas.getHeight())
        drawable.draw(canvas)
        return bitmap
    }

     fun generateProgressBitmap(context: Context,width: Int, progress: Int, color: Int): Bitmap {

     //   val displayMetrics = Resources.getSystem().displayMetrics

       // val width = displayMetrics.widthPixels

        val padding = DeviceUtils.convertDpToPixel(8f, context)

        val stroke = 4f

        val viewWidth = width - 2 * padding

        val viewHeight = DeviceUtils.convertDpToPixel(12f, context)


        val paint = Paint()
        paint.isAntiAlias = true

        paint.style = Paint.Style.STROKE
        paint.strokeWidth = stroke
        paint.color = color

        val size = DeviceUtils.convertDpToPixel(10f, context)

        val bitmap = Bitmap.createBitmap(viewWidth.toInt(), viewHeight.toInt(), Bitmap.Config.ARGB_8888)

        val canvas = Canvas(bitmap)

        val rectF = RectF(0f + stroke, 0f + stroke, viewWidth - stroke, viewHeight - stroke)
        canvas.drawRoundRect(rectF, 20f, 20f, paint)

        paint.style  = Paint.Style.FILL

        val r =  (viewWidth - stroke)* (progress.toFloat()/100.toFloat())
        val progressRectF = RectF(0f + stroke, 0f + stroke, r, viewHeight - stroke)
        paint.color = color

        canvas.drawRoundRect(progressRectF, 50f, 50f, paint)
        // canvas.drawArc(rectF, 270f, 360f, false, paint)
        return bitmap

    }

}