package com.celestial.progress.widget

import android.appwidget.AppWidgetManager
import android.content.ComponentName
import android.content.Context
import android.content.Intent
import android.os.Looper
import android.util.Log
import android.view.View
import android.widget.RemoteViews
import android.widget.RemoteViewsService
import com.celestial.progress.R
import com.celestial.progress.data.CounterRepository
import com.celestial.progress.data.model.Counter
import com.celestial.progress.others.Utils
import com.celestial.progress.ui.component.DeviceUtils
import kotlinx.coroutines.GlobalScope
import kotlinx.coroutines.launch


class ProgressWidgetDataProvider(val ctx: Context, val intent: Intent, val repository: CounterRepository) : RemoteViewsService.RemoteViewsFactory {
    var progressList = ArrayList<Counter>()


    val TAG = ProgressWidgetDataProvider::class.java.name

    lateinit var counterList: List<Counter>

    override fun onCreate() {

    }

    override fun onDataSetChanged() {
        counterList = repository.readAllValidCounters()
        Log.d(TAG, "onDataSetChanged: - Main Thread :${Looper.myLooper() == Looper.getMainLooper()}")
    }

    override fun onDestroy() {

    }

    override fun getCount(): Int {
        return counterList.size
    }

    override fun getViewAt(position: Int): RemoteViews {
        val rv = RemoteViews(ctx.packageName, R.layout.listview_item)
        val model = counterList[position]

        rv.setTextViewText(R.id.tv_title_widget_list, model.title)
        rv.setTextViewText(R.id.tv_detail_widget_list, model.getInitial() +model.getDetail(!model?.isElapsed()))
        if (model.isElapsed()) {
            rv.setViewVisibility(R.id.progress_widget_list, View.GONE)
            rv.setViewVisibility(R.id.progress_widget_list_imgv, View.GONE)
            rv.setViewVisibility(R.id.tv_percent_widget_list,View.GONE)
        } else {
            rv.setViewVisibility(R.id.tv_percent_widget_list,View.VISIBLE)
            rv.setTextViewText(R.id.tv_percent_widget_list, model.getPercent().toString() + "%")
            rv.setViewVisibility(R.id.progress_widget_list_imgv, View.VISIBLE)
            val pBitmap = Utils.generateProgressBitmap(ctx,DeviceUtils.convertDpToPixel(280f,ctx).toInt(), model.getPercent()!!.toInt(),model.color!!)
            rv.setImageViewBitmap(R.id.progress_widget_list_imgv, pBitmap)
        }



        rv.setProgressBar(R.id.progress_widget_list, 0, 0, model.isElapsed())
        if (!model.isElapsed()) {
            rv.setProgressBar(R.id.progress_widget_list, 100,
                    model.getPercent()?.toInt()!!, false)

        }
        return rv
    }

    override fun getLoadingView(): RemoteViews? {
        return null
    }

    override fun getViewTypeCount(): Int {
        return 1
    }

    override fun getItemId(position: Int): Long {
        return Math.random().toLong()
    }

    override fun hasStableIds(): Boolean {
        return true
    }


}