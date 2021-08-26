package com.celestial.progress.data

import androidx.room.Database
import androidx.room.RoomDatabase
import com.celestial.progress.data.model.Counter

@Database(
    entities = [Counter::class],
    version = 1
)
abstract class CounterDatabase: RoomDatabase() {

    abstract fun counterDao(): CounterDao
}