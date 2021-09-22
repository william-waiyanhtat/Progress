package com.celestial.progress.data


import android.content.Context
import android.graphics.Color
import androidx.arch.core.executor.testing.InstantTaskExecutorRule
import androidx.room.Room
import androidx.test.core.app.ApplicationProvider
import androidx.test.ext.junit.runners.AndroidJUnit4
import androidx.test.filters.SmallTest
import com.celestial.progress.data.model.Counter
import com.celestial.progress.getOrAwaitValue
import kotlinx.coroutines.ExperimentalCoroutinesApi
import kotlinx.coroutines.runBlocking
import kotlinx.coroutines.test.runBlockingTest
import org.junit.After
import org.junit.Before
import org.junit.Rule

import org.junit.Test
import org.junit.runner.RunWith
import java.io.IOException
import java.text.SimpleDateFormat
import java.util.*

@ExperimentalCoroutinesApi
@RunWith(AndroidJUnit4::class)
@SmallTest
class SimpleDatabaseTest {

    private lateinit var counterDao: CounterDao
    private lateinit var db: CounterDatabase

    @get: Rule
    var instantTaskExecutorRule = InstantTaskExecutorRule()

    @Before
    fun createDb(){
        val context = ApplicationProvider.getApplicationContext<Context>()
        db = Room.inMemoryDatabaseBuilder(context,CounterDatabase::class.java).build()
        counterDao = db.counterDao()
    }

    @Test
    fun readWriteTest()= runBlockingTest{

        val d1: Date = Calendar.getInstance().time
        val d2: Date = Calendar.getInstance().time

        val smpf: SimpleDateFormat = SimpleDateFormat("YYYY-MM-DD'T'HH-mm-ss")
        val startDate = smpf.format(d1)
        val endDate = smpf.format(d2)

        val counter = Counter(
            "ABC",
            startDate,
            endDate,
            Color.RED,
            "Test Note"
        )

        counterDao.insertCounter(counter)

        val c = counterDao.observeAllValidCounters().getOrAwaitValue()

        val a = Counter(
            "AAA",
            startDate,
            endDate,
            Color.BLUE,
            ""
        )

        assert(c.size>0)

    }

    @Test
    fun gettingUnavailableID(){
        val d1: Date = Calendar.getInstance().time
        val d2: Date = Calendar.getInstance().time

        val smpf: SimpleDateFormat = SimpleDateFormat("YYYY-MM-DD'T'HH-mm-ss")
        val startDate = smpf.format(d1)
        val endDate = smpf.format(d2)

        val counter = Counter(
                "ABC",
                startDate,
                endDate,
                Color.RED,
                "Test Note"
        )

        val c = runBlocking { counterDao.getCounterById(8) }

        println(c)

        assert(c==null)

    }



    @After
    @Throws(IOException::class)
    fun closeDb() {
        db.close()
    }


}