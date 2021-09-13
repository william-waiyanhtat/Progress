package com.celestial.progress.ui

import androidx.lifecycle.LiveData
import com.celestial.progress.data.model.Counter
import com.celestial.progress.others.Resource

interface ViewModelUseCase {

    fun createCounter(counter: Counter): LiveData<Resource<Long>>

    fun readCounterDetail(): Counter

    fun readAllCounters(): LiveData<List<Counter>>

    fun updateCounter(counter: Counter)
}