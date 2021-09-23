package com.celestial.progress.ui

import androidx.lifecycle.LiveData
import com.celestial.progress.data.model.Counter
import com.celestial.progress.others.Resource

interface ViewModelUseCase {

    fun createCounter(counter: Counter): LiveData<Resource<Long>>

    fun insertAll(counters: List<Counter>)

    fun readCounterDetail(): Counter

    fun readAllCounters(): LiveData<List<Counter>>

    fun updateCounter(counter: Counter)

    fun readArchiveCounters():LiveData<List<Counter>>

    fun deleteCounter(counter: Counter)
}