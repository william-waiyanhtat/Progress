package com.celestial.progress.data.model

import androidx.lifecycle.LiveData

interface DefaultRepository {
    suspend fun insertCounterItem(counterItem: Counter)

    suspend fun deleteCounterItem(counterItem: Counter)

    fun observeAllCounterItem(): LiveData<List<Counter>>

    fun observeTotalPrice(): LiveData<Float>

}