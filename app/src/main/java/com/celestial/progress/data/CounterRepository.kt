package com.celestial.progress.data

import androidx.lifecycle.LiveData
import com.celestial.progress.data.model.Counter
import javax.inject.Inject

class CounterRepository @Inject constructor(
    private val counterDao: CounterDao): DefaultRepository {
    override suspend fun insertCounterItem(counterItem: Counter) {
        counterDao.insertCounter(counterItem)
    }

    override suspend fun deleteCounterItem(counterItem: Counter) {
        counterDao.deleteCounter(counterItem)
    }

    override fun observeAllCounterItem(): LiveData<List<Counter>> {
       return counterDao.observeAllShoppingItems()
    }

    suspend fun insert100Records(){
        for(i in 1..100) {
            val counter = Counter(
                "Counter No. ${i}",
                "2021-05-11",
                "2021-08-03",
                true,
                11,
                "Note For Counter"
            )
            counterDao.insertCounter(counter)
        }
    }

}