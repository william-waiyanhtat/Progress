package com.celestial.progress.data

import androidx.lifecycle.LiveData
import androidx.room.*
import com.celestial.progress.data.model.Counter

@Dao
interface CounterDao {
    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun insertCounter(counter: Counter)

    @Delete
    suspend fun deleteShoppingItem(counter: Counter)

    @Query("SELECT * FROM counter")
    fun observeAllShoppingItems(): LiveData<List<Counter>>
}
