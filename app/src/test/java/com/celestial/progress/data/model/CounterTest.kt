package com.celestial.progress.data.model

import android.graphics.Color
import com.google.common.truth.Truth.assertThat

import org.junit.Test

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


}