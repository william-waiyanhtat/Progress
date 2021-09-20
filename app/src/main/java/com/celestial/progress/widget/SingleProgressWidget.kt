package com.celestial.progress.widget


import android.app.PendingIntent
import android.appwidget.AppWidgetManager
import android.appwidget.AppWidgetProvider
import android.content.ComponentName
import android.content.Context
import android.content.Intent
import android.graphics.*
import android.os.Build
import android.os.Looper
import android.util.Log
import android.widget.*
import com.celestial.progress.R
import com.celestial.progress.data.CounterRepository
import com.celestial.progress.data.model.Counter
import com.celestial.progress.ui.component.DeviceUtils
import com.celestial.progress.ui.component.DeviceUtils.getSecondaryColor
import dagger.hilt.android.AndroidEntryPoint
import kotlinx.coroutines.GlobalScope
import kotlinx.coroutines.launch
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
        Log.d("SingleProgressWidget", "Thread -" + Looper.getMainLooper().isCurrentThread + "counterRepo: ${counterRepository.toString()}")

//        val intent = Intent(context.applicationContext,SingleViewWidgetService::class.java)
//        intent.putExtra(AppWidgetManager.EXTRA_APPWIDGET_IDS,appWidgetIds)
//        context.startService(intent)

//        // There may be multiple widgets active, so update all of them
        for (appWidgetId in appWidgetIds) {
            GlobalScope.launch {
                try {
                    val list = counterRepository.readAllValidCounters()

                    val id = loadTitlePref(context, appWidgetId)


                    Log.d(TAG, "onUpdate: ID: ${id}")
                    val counter = counterRepository.getCounterById(id.toInt())

                    Log.d(TAG, "On Update List Size: ${list.size}" + "Current Thread :${Looper.getMainLooper().isCurrentThread}")
                    // updateAppWidget(context,appWidgetIds)
                    updateAppWidget(context, appWidgetManager, appWidgetId, counter)
                } catch (e: Exception) {

                }

            }
            val id = loadTitlePref(context, appWidgetId)
            Toast.makeText(context, "Widget Update -" + appWidgetId, Toast.LENGTH_SHORT).show()
            // appWidgetManager.notifyAppWidgetViewDataChanged(appWidgetId,R.layout.single_progress_widget)
            Log.d(TAG, "Widget Update : ${id}")
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
    }

    companion object {
        internal fun updateAppWidget(
                context: Context,
                appWidgetManager: AppWidgetManager,
                appWidgetId: Int,
                counter: Counter?
        ) {
            // val widgetText = loadTitlePref(context, appWidgetId)

            // Construct the RemoteViews object
            val views = RemoteViews(context.packageName, R.layout.single_progress_widget)
            views.setTextViewText(R.id.appwidget_text, counter?.title)
            views.setTextViewText(R.id.tv_single_widget_detail, counter?.getDetail())
            //    views.setProgressBar(R.id.pbar_single_widget,100, counter?.getPercent()?.toInt()!!, counter?.isElapsed())
            views.setOnClickPendingIntent(R.id.btn_swidget_update, getRefreshIntent(context))

            views.setImageViewBitmap(R.id.imgv_single_widget, getProgressView(context,counter))


         //   getProgressView(counter)

            // Instruct the widget manager to update the widget
            appWidgetManager.updateAppWidget(appWidgetId, views)
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

        private fun makeProgressBitmap(context: Context, progress: Int, color1: Int, color2: Int, sizeInDP: Float, stroke: Float): Bitmap? {
            val paint = Paint()
            paint.isAntiAlias = true
            paint.color = color1
            paint.style = Paint.Style.STROKE
            paint.strokeWidth = stroke
            val size = DeviceUtils.convertDpToPixel(sizeInDP, context)

            val bitmap = Bitmap.createBitmap(size.toInt(), size.toInt(), Bitmap.Config.ARGB_8888)
            val canvas = Canvas(bitmap)

            val rectF = RectF(stroke, stroke, size - stroke, size - stroke)
            paint.color = Color.GRAY
            canvas.drawArc(rectF, 270f, 360f, false, paint)

            paint.shader = createSweepGradient(size / 2, size / 2, color1, color2).apply {

                val matrix = Matrix()
                matrix.preRotate(-90f, size / 2 - stroke, size / 2 - stroke)
                setLocalMatrix(matrix)

            }
            val TAG = SingleProgressWidget::class.java.name

            val sweepAngle = progress.toFloat() / 100 * 360f

            Log.d(TAG, "Sweep Angle $sweepAngle Progress: $progress")



            canvas.drawArc(rectF, 270f, sweepAngle, false, paint)
            return bitmap

        }

        private fun getProgressView(context: Context, counter: Counter?): Bitmap? {
            val color1 = counter?.color
            val color2 = color1?.getSecondaryColor()

            val progress = counter?.getPercent()?.toInt()

            return makeProgressBitmap(context, progress!!,color1!!,color2!!, 50f,10f)

        }


        private fun createSweepGradient(cX: Float, xY: Float, color1: Int, color2: Int): SweepGradient {
            return SweepGradient(cX, xY, color1, color2)
        }

    }


}

