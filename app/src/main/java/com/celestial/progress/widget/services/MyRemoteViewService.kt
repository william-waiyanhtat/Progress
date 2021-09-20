package com.celestial.progress.widget.services

import android.content.Intent
import android.widget.RemoteViewsService
import com.celestial.progress.data.CounterRepository
import com.celestial.progress.widget.ProgressWidgetDataProvider
import dagger.hilt.android.AndroidEntryPoint
import javax.inject.Inject

@AndroidEntryPoint
class MyRemoteViewService: RemoteViewsService() {

    @Inject
    lateinit var repository: CounterRepository

    override fun onGetViewFactory(intent: Intent): RemoteViewsFactory {
        return ProgressWidgetDataProvider(this.applicationContext, intent, repository)
    }
}