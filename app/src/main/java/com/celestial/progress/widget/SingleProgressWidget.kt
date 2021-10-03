package com.celestial.progress.widget


import android.app.AlarmManager
import android.app.PendingIntent
import android.appwidget.AppWidgetManager
import android.appwidget.AppWidgetProvider
import android.content.ComponentName
import android.content.Context
import android.content.Intent
import android.graphics.*
import android.opengl.Visibility
import android.os.Build
import android.os.Looper
import android.util.Log
import android.view.View
import android.widget.*
import com.celestial.progress.MainActivity
import com.celestial.progress.R
import com.celestial.progress.data.CounterRepository
import com.celestial.progress.data.model.Counter
import com.celestial.progress.ui.component.DeviceUtils
import com.celestial.progress.ui.component.DeviceUtils.getSecondaryColor
import dagger.hilt.android.AndroidEntryPoint
import kotlinx.coroutines.GlobalScope
import kotlinx.coroutines.launch
import java.util.*
import javax.inject.Inject

/**
 * Implementation of App Widget functionality.
 * App Widget Configuration implemented in [SingleProgressWidgetConfigureActivity]
 */
@AndroidEntryPoint
class SingleProgressWidget : AppWidgetProvider() {

    val TAG = SingleProgressWidget::class.java.name

    @Inject
    lateinit var counterRepository: CounterRepository

    override fun onUpdate(
            context: Context,
            appWidgetManager: AppWidgetManager,
            appWidgetIds: IntArray
    ) {
        for (appWidgetId in appWidgetIds) {
            GlobalScope.launch {
                try {

                    val id = loadTitlePref(context, appWidgetId)

                    Log.d(TAG, "onUpdate: ID: ${id}")
                    val counter = counterRepository.getCounterById(id.toInt())

                    if (counter != null) {
                        updateAppWidget(context, appWidgetManager, appWidgetId, counter)
                    }
                    //Todo add not available counter handling widget here
                    // updateAppWidget(context,appWidgetIds)

                } catch (e: Exception) {

                }

            }
            val id = loadTitlePref(context, appWidgetId)

        }

    }


    override fun onDeleted(context: Context, appWidgetIds: IntArray) {
        // When the user deletes the widget, delete the preference associated with it.
        for (appWidgetId in appWidgetIds) {
            deleteTitlePref(context, appWidgetId)
        }
    }

    override fun onEnabled(context: Context) {
        // Enter relevant functionality for when the first widget is created
    }

    override fun onDisabled(context: Context) {
        // Enter relevant functionality for when the last widget is disabled
        cancelUpdatePendingIntentAlarmManager(context)
    }

    companion object {
        internal fun updateAppWidget(
                context: Context,
                appWidgetManager: AppWidgetManager,
                appWidgetId: Int,
                counter: Counter?
        ) {
            // Construct the RemoteViews object
            val views = RemoteViews(context.packageName, R.layout.single_progress_widget)
            views.setTextViewText(R.id.appwidget_text, counter?.title)
            views.setOnClickPendingIntent(R.id.parent_single_widget, getPendingIntent(context,1))

            views.setViewVisibility(R.id.tv_single_widget_detail,View.VISIBLE)
            views.setTextViewText(R.id.tv_single_widget_detail, counter?.getInitial()+"\n"+counter?.getDetail(!counter?.isElapsed()))

            views.setOnClickPendingIntent(R.id.btn_swidget_update, getRefreshIntent(context))
            views.setImageViewBitmap(R.id.imgv_single_widget, getProgressView(context, counter))
            if(counter?.isElapsed()!!) {
                views.setViewVisibility(R.id.ly_single_progress_widget_percent, View.GONE)
            }else{
                views.setViewVisibility(R.id.ly_single_progress_widget_percent, View.VISIBLE)
                views.setTextViewText(R.id.tv_single_widget_percent,counter.getPercent().toString()+"%")
            }
            // Instruct the widget manager to update the widget
            appWidgetManager.updateAppWidget(appWidgetId, views)
        }

        private fun getPendingIntent(context: Context, value: Int): PendingIntent {
            //1
            val intent = Intent(context, MainActivity::class.java)
            return PendingIntent.getActivity(context, value, intent, 0)
        }



        fun getRefreshIntent(context: Context): PendingIntent {
            val intent = Intent(context.applicationContext, SingleProgressWidget::class.java)
            intent.action = AppWidgetManager.ACTION_APPWIDGET_UPDATE
            // Use an array and EXTRA_APPWIDGET_IDS instead of AppWidgetManager.EXTRA_APPWIDGET_ID,
            // since it seems the onUpdate() is only fired on that:
            val widgetManager = AppWidgetManager.getInstance(context)
            val ids = widgetManager.getAppWidgetIds(ComponentName(context, SingleProgressWidget::class.java))
            if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.HONEYCOMB) {
                widgetManager.notifyAppWidgetViewDataChanged(ids, R.layout.single_progress_widget)
            }
            intent.putExtra(AppWidgetManager.EXTRA_APPWIDGET_IDS, ids)

            return PendingIntent.getBroadcast(context, 2, intent, PendingIntent.FLAG_UPDATE_CURRENT)

        }

        private fun makeProgressBitmap(context: Context, progress: Int, color1: Int, color2: Int, sizeInDP: Float, stroke: Float, isElapsed: Boolean): Bitmap? {
            val TAG = SingleProgressWidget::class.java.name
            val paint = Paint()
            paint.isAntiAlias = true

            paint.style = Paint.Style.STROKE
            paint.strokeWidth = stroke
            val size = DeviceUtils.convertDpToPixel(sizeInDP, context)

            val bitmap = Bitmap.createBitmap(size.toInt(), size.toInt(), Bitmap.Config.ARGB_8888)
            val canvas = Canvas(bitmap)

            val rectF = RectF(stroke, stroke, size - stroke, size - stroke)
            paint.color = Color.LTGRAY
            canvas.drawArc(rectF, 270f, 360f, false, paint)

            paint.shader = createSweepGradient(size / 2, size / 2, color1, color2).apply {
                val matrix = Matrix()
                matrix.preRotate(-90f, size / 2 - stroke, size / 2 - stroke)
                setLocalMatrix(matrix)
            }
            val sweepAngle = progress.toFloat() / 100 * 360f
            canvas.drawArc(rectF, 270f, sweepAngle, false, paint)
            return bitmap

        }

        private fun getProgressView(context: Context, counter: Counter?): Bitmap? {
            val color1 = counter?.color
            val color2 = color1?.getSecondaryColor()

            val p = counter?.getPercent()!!
            return makeProgressBitmap(context, p.toInt(), color1!!, color2!!, 50f, 10f, counter.isElapsed())

        }


        private fun createSweepGradient(cX: Float, xY: Float, color1: Int, color2: Int): SweepGradient {
            return SweepGradient(cX, xY, color1, color2)
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




}

