package com.celestial.progress.widget

import android.appwidget.AppWidgetManager
import android.content.ComponentName
import android.content.Context
import android.content.Intent
import android.os.Looper
import android.util.Log
import android.widget.RemoteViews
import android.widget.RemoteViewsService
import com.celestial.progress.R
import com.celestial.progress.data.CounterRepository
import com.celestial.progress.data.model.Counter
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
        rv.setTextViewText(R.id.tv_title_widget_list, counterList[position].title)
        rv.setTextViewText(R.id.tv_note_widget_list, counterList[position].note)
        rv.setTextViewText(R.id.tv_detail_widget_list, counterList[position].getDetail())
        rv.setProgressBar(R.id.progress_widget_list,0,0,counterList[position].isElapsed!!)
        if(!counterList[position].isElapsed!!){
            rv.setProgressBar(R.id.progress_widget_list,100,
                counterList[position].getPercent()?.toInt()!!,false)
            rv.setTextViewText(R.id.tv_percent_widget_list,counterList[position].getPercent().toString()+"%")
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