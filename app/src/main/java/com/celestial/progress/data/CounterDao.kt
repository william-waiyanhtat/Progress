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

    @Query("SELECT * FROM counter")
    fun observeAllShoppingItems(): LiveData<List<Counter>>
}
