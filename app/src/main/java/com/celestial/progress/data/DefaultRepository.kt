package com.celestial.progress.data

import androidx.lifecycle.LiveData
import com.celestial.progress.data.model.Counter
import com.celestial.progress.others.RepoStatus
import com.celestial.progress.others.Resource

interface DefaultRepository {
    suspend fun insertCounterItem(counterItem: Counter, status: RepoStatus)

    suspend fun insertAllCounters(counters: List<Counter>)

    suspend fun deleteCounterItem(counterItem: Counter)

    fun observeAllCounterItem(): LiveData<List<Counter>>

    suspend fun updateCounter(counter: Counter)

    fun observeAllArchiveCounterItem(): LiveData<List<Counter>>

    fun readAllValidCounters(): List<Counter>

    suspend fun getCounterById(id: Int): Counter
}