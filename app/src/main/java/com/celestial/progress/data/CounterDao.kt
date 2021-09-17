package com.celestial.progress.data

import androidx.lifecycle.LiveData
import androidx.room.*
import com.celestial.progress.data.model.Counter

@Dao
interface CounterDao {
    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun insertCounter(counter: Counter): Long

    @Delete
    suspend fun deleteCounter(counter: Counter)

    @Query("SELECT * FROM counter where isArchived = :isArchived ORDER BY `order` ASC")
    fun observeAllShoppingItems(isArchived: Boolean = false): LiveData<List<Counter>>

    @Update
    suspend fun updateCounter(counter: Counter)

    @Query("SELECT * FROM counter where isArchived = :isArchived ORDER BY `order` ASC")
    fun readAllValidCounters(isArchived: Boolean = false): List<Counter>
}
