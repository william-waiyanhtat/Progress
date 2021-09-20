package com.celestial.progress.data

import androidx.lifecycle.LiveData
import com.celestial.progress.data.model.Counter
import com.celestial.progress.others.RepoStatus
import javax.inject.Inject

class CounterRepository @Inject constructor(

        private val counterDao: CounterDao) : DefaultRepository {

    override suspend fun insertCounterItem(counterItem: Counter, status: RepoStatus) {
        val result = counterDao.insertCounter(counterItem)

        if (result >= 0)
            status.success(result, "New Counter Added Successfully!")
        else
            status.equals("Error inserting new counter!")

    }

    override suspend fun deleteCounterItem(counterItem: Counter) {
        counterDao.deleteCounter(counterItem)
    }

    override fun observeAllCounterItem(): LiveData<List<Counter>> {
        return counterDao.observeAllValidCounters()
    }

    override suspend fun updateCounter(counter: Counter) {
        counterDao.updateCounter(counter)
    }

    override fun observeAllArchiveCounterItem(): LiveData<List<Counter>> {
        return counterDao.observeAllValidCounters(isArchived = true)
    }

    override fun readAllValidCounters(): List<Counter> {
        return counterDao.readAllValidCounters(isArchived = false)
    }

    override suspend fun getCounterById(id: Int): Counter {
        return counterDao.readCounterById(id)
    }

    suspend fun insert100Records() {
        for (i in 1..100) {
            val counter = Counter(
                    "Counter No. ${i}",
                    "2021-05-11",
                    "2021-08-03",
                    11,
                    "Note For Counter"
            )
            counterDao.insertCounter(counter)
        }
    }

}