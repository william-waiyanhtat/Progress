package com.celestial.progress.others

import android.content.Context
import com.celestial.progress.data.CounterRepository
import dagger.hilt.android.AndroidEntryPoint
import kotlinx.coroutines.GlobalScope
import kotlinx.coroutines.launch
import javax.inject.Inject


object NotificationController {


    fun fetchAndUpdateNotifiedCounterList(counterRepository: CounterRepository, context: Context) {
        GlobalScope.launch {
            val list = counterRepository.fetchAllCountersWhichRequiredNotification()

            for (i in list) {
                NotificationHelper.createNotification(context, i)
            }
        }
    }
}