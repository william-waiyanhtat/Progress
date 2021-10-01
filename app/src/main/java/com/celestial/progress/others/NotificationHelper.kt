package com.celestial.progress.others

import android.app.NotificationChannel
import android.app.NotificationManager
import android.app.PendingIntent
import android.content.Context
import android.content.Intent
import android.content.res.Resources
import android.graphics.*
import android.os.Build
import android.provider.Settings
import android.service.notification.StatusBarNotification
import android.util.Log
import android.view.View
import android.widget.RemoteViews
import androidx.core.app.NotificationCompat
import androidx.preference.PreferenceManager
import com.celestial.progress.MainActivity
import com.celestial.progress.R
import com.celestial.progress.data.model.Counter
import com.celestial.progress.ui.component.DeviceUtils


const val NOTIFICATION_CHANNEL_ID = "com.celestial.progress"
const val NOTIFICATION_CHANNEL_NAME = "casualChannel"

object NotificationHelper {

//    private var mContext: Context? = null
//    private var mNotificationManager: NotificationManager? = null
//    private var mBuilder: NotificationCompat.Builder? = null
    /**
     * Create and push the notification
     */
    fun createNotification(mContext: Context, counter: Counter) {

        val notifStrKey = mContext.getString(R.string.pf_key_noti_style)

        val isDefaultNotification = SharePrefHelper.isDefaultNotification(mContext)

        Log.d("NOTI", "isDefautlNoti: ${isDefaultNotification}")

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
                .setContentText(counter.getInitial()+ counter.getDetail())
                .setAutoCancel(false)
                .setOngoing(true)
                .setColorized(true)
                .setColor(counter.color!!)
                .setSound(Settings.System.DEFAULT_NOTIFICATION_URI)
                .setContentIntent(resultPendingIntent)

        if (!isDefaultNotification) {
             mBuilder.setCustomContentView(createAndGetCustomNotification(mContext ,counter))
        }
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

    fun cancelNotification(mContext: Context, counter: Counter) {
        val notificationManager = mContext.getSystemService(Context.NOTIFICATION_SERVICE) as NotificationManager
        notificationManager.cancel(counter.id!!)
    }

    fun checkNotification(mContext: Context, counter: Counter): Boolean {
        val mNotificationManager: NotificationManager = mContext.getSystemService(Context.NOTIFICATION_SERVICE) as NotificationManager
        val notifications: Array<StatusBarNotification> = mNotificationManager.activeNotifications

        if (notifications.isEmpty()) {
            //you don't have notifications
            return false
        } else {
            for (n in notifications) {
                if (n.id == counter.id) {
                    return true
                }
            }
            return false
        }
    }

    private fun createAndGetCustomNotification(context: Context, counter: Counter): RemoteViews {
        return RemoteViews(context.packageName, R.layout.custom_notification_layout).apply {
            setTextViewText(R.id.tv_notification_title, counter.title)

            val bitmap = context.getDrawable(R.drawable.ic_only_ic)?.let { Utils.drawableToBitmap(it) }

            val tImage = tintImage(bitmap!!,counter.color!!)

            setImageViewBitmap(R.id.imgv_customnoti_icon,tImage)

            val initial  = if(counter.isElapsed()) "Elapsed : " else "Remaining : "
            setTextViewText(R.id.tv_notification_detail, initial+counter.getDetail())
            if(counter.note!!.isEmpty()){
                setViewVisibility(R.id.tv_notification_note,View.GONE)
            }else{
                setViewVisibility(R.id.tv_notification_note,View.VISIBLE)
                setTextViewText(R.id.tv_notification_note, counter.note)
            }

            if(counter.isElapsed()){
                setViewVisibility(R.id.imgv_notification_progress, View.GONE)
            }else{
                setViewVisibility(R.id.imgv_notification_progress, View.VISIBLE)
                setImageViewBitmap(R.id.imgv_notification_progress, generateProgressBitmap(context, 30, counter.color!!))
            }

        }
    }

    private fun generateProgressBitmap(context: Context, progress: Int, color: Int): Bitmap {

        val displayMetrics = Resources.getSystem().displayMetrics

        val width = displayMetrics.widthPixels

        val padding = DeviceUtils.convertDpToPixel(8f, context)

        val stroke = 4f

        val viewWidth = width - 2 * padding

        val viewHeight = DeviceUtils.convertDpToPixel(12f, context)


        val paint = Paint()
        paint.isAntiAlias = true

        paint.style = Paint.Style.FILL
        paint.strokeWidth = stroke
        paint.color = Color.LTGRAY

        val size = DeviceUtils.convertDpToPixel(10f, context)

        val bitmap = Bitmap.createBitmap(viewWidth.toInt(), viewHeight.toInt(), Bitmap.Config.ARGB_8888)

        val canvas = Canvas(bitmap)

        val rectF = RectF(0f + stroke, 0f + stroke, viewWidth - stroke, viewHeight - stroke)
        canvas.drawRoundRect(rectF, 20f, 20f, paint)

        paint.style  = Paint.Style.FILL

        val r =  (viewWidth - stroke)* (progress.toFloat()/100.toFloat())
        val progressRectF = RectF(0f + stroke, 0f + stroke, r, viewHeight - stroke)
        paint.color = color

        canvas.drawRoundRect(progressRectF, 20f, 20f, paint)
        // canvas.drawArc(rectF, 270f, 360f, false, paint)

        return bitmap

    }

    fun tintImage(bitmap: Bitmap, color: Int): Bitmap? {
        val paint = Paint()
        paint.colorFilter = PorterDuffColorFilter(color, PorterDuff.Mode.SRC_IN)
        val bitmapResult = Bitmap.createBitmap(bitmap.width, bitmap.height, Bitmap.Config.ARGB_8888)
        val canvas = Canvas(bitmapResult)
        canvas.drawBitmap(bitmap, 0f, 0f, paint)
        return bitmapResult
    }
}


