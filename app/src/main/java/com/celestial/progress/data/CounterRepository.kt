package com.celestial.progress.data

import androidx.lifecycle.LiveData
import com.celestial.progress.data.model.Counter
import com.celestial.progress.data.model.DefaultRepository
import javax.inject.Inject

class CounterRepository @Inject constructor(
    private val counterDao: CounterDao
): DefaultRepository {
    override suspend fun insertCounterItem(counterItem: Counter) {
        TODO("Not yet implemented")
    }

    override suspend fun deleteCounterItem(counterItem: Counter) {
        TODO("Not yet implemented")
    }

    override fun observeAllCounterItem(): LiveData<List<Counter>> {
        TODO("Not yet implemented")
    }

    override fun observeTotalPrice(): LiveData<Float> {
        TODO("Not yet implemented")
    }


}