package com.celestial.progress.data

import androidx.lifecycle.LiveData
import com.celestial.progress.data.model.Counter

interface DefaultRepository {
    suspend fun insertCounterItem(counterItem: Counter)

    suspend fun deleteCounterItem(counterItem: Counter)

    fun observeAllCounterItem(): LiveData<List<Counter>>

    fun observeTotalPrice(): LiveData<Float>

}