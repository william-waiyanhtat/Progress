package com.celestial.progress.data.model

import android.graphics.Color
import com.google.common.truth.Truth.assertThat

import org.junit.Test
import java.util.*

class CounterTest {

    @Test
    fun deatailOnDayTest() {
        val counter = Counter(
            "1day",
            "2020-08-10",
            "2021-09-10",
            false,
            Color.RED,
            "Empty",
            false,
            DisplayFormat.DAY
        )

        print("Counter ${counter.getDetail()}")
        assertThat(counter.getDetail()).isEqualTo("396 Day(s)")
    }

    @Test
    fun detailOnWeekDayTest() {
        val counter = Counter(
            "1day",
            "2020-09-01",
            "2021-09-09",
            false,
            Color.RED,
            "Empty",
            false,
            DisplayFormat.WEEK_DAY
        )

        print("Counter ${counter.getDetail()}")
        assertThat(counter.getDetail()).isEqualTo("53 Week(s), 2 Day(s)")
    }

    @Test
    fun detailOnMonthWeekDayTest() {
        val counter = Counter(
            "1day",
            "2021-06-01",
            "2021-09-10",
            false,
            Color.RED,
            "Empty",
            false,
            DisplayFormat.MONTH_WEEK_DAY
        )

        print("Counter ${counter.getDetail()}")
        assertThat(counter.getDetail()).isEqualTo("3 Month(s), 1 Week(s), 2 Day(s)")
    }

    @Test
    fun detailOnYearMonthWeekDayTest() {
        val counter = Counter(
            "1day",
            "2020-08-01",
            "2021-09-10",
            false,
            Color.RED,
            "Empty",
            false,
            DisplayFormat.YEAR_MONTH_WEEK_DAY
        )

        print("Counter ${counter.getDetail()}")
        assertThat(counter.getDetail()).isEqualTo("1 Year(s), 1 Month(s), 1 Week(s), 2 Day(s)")
    }

    @Test
    fun detailOnYearMonthDayTest() {
        val counter = Counter(
            "1day",
            "2020-08-01",
            "2021-09-10",
            false,
            Color.RED,
            "Empty",
            false,
            DisplayFormat.YEAR_MONTH_DAY
        )

        print("Counter ${counter.getDetail()}")
        assertThat(counter.getDetail()).isEqualTo("1 Year(s), 1 Month(s), 9 Day(s)")
    }

    @Test
    fun oneDayBefore(){
        val counter = Counter(
            "1Day",
            "2021-09-12",
            "2021-09-13",
            isElapsed = true,
            Color.RED,
            "Empty",
            false,
            DisplayFormat.DAY
        )

        val result = counter.getDetail()
        println(result+ "***")
        assertThat(result).isEqualTo("1 Day(s)")
    }

    @Test
    fun dateStringFromCalendarInstance(){
        val cal = Calendar.getInstance()
        val st = cal.getCurrentDateString()

        println(st)

        assertThat(st).isEqualTo("2021-09-13")
    }

    @Test
    fun dayCountPercentTest(){
        val counter = Counter(
        "50 Percent Test",
        "2021-09-16",
        "2021-09-18",
        true,
        Color.RED,
        "Empty",
        false,
        DisplayFormat.DAY
        )

        val result = counter.getPercent()

        print("Percent: $result")
        assertThat(result).isEqualTo(50L)
    }

}