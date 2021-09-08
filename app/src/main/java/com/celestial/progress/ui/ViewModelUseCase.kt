package com.celestial.progress.ui

import com.celestial.progress.data.model.Counter

interface ViewModelUseCase {

    fun createCounter()

    fun readCounterDetail(): Counter

    fun readAllCounters()

}