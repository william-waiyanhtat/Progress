package com.celestial.progress.data

import androidx.lifecycle.LiveData
import androidx.room.*
import com.celestial.progress.data.model.Counter

@Dao
interface CounterDao {
    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun insertCounter(counter: Counter): Long

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun insertAll(counters: List<Counter>)

    @Delete
    suspend fun deleteCounter(counter: Counter)

    @Query("SELECT * FROM counter where isArchived = :isArchived ORDER BY `order` ASC")
    fun observeAllValidCounters(isArchived: Boolean = false): LiveData<List<Counter>>

    @Update
    suspend fun updateCounter(counter: Counter)

    @Query("SELECT * FROM counter where isArchived = :isArchived ORDER BY `order` ASC")
    fun readAllValidCounters(isArchived: Boolean = false): List<Counter>

    @Query("SELECT * FROM counter where isArchived = 0 and id = :id")
    suspend fun getCounterById(id: Int): Counter

    @Query("update counter set requiredNotification = :isNotify where id = :id")
    suspend fun updateCounterForNotificationById(id: Int, isNotify: Boolean)

    @Query("select * from counter where isArchived = 0 and requiredNotification = 1")
     fun fetchCounterWhichRequiredNotification(): LiveData<List<Counter>>

}
