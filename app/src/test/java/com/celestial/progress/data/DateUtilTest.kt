package com.celestial.progress.data


import com.celestial.progress.data.model.Counter
import org.junit.Test
import java.text.SimpleDateFormat
import java.util.*

class DateUtilTest {

    @Test
    fun testDateString(){
        val currentDate = Calendar.getInstance().time

        var newCal = Calendar.getInstance()

        newCal.apply {
            this.add(Calendar.DAY_OF_YEAR,-36000)

        }
        val newDate = newCal.time

        val smp  = SimpleDateFormat("yyyy-MM-dd")

        val st = smp.format(newDate)

        println("**result**")
        println(st)
        println("----")
        assert(st=="2021-08-23")
    }

    @Test
    fun testDayDifference(){
        val c = Counter(
                "5day Diff",
                "1991-12-04",
                "2021-08-27",
                false,
                    1,
                "Nothing"
        )
        println("Day Difference between two dates: ${c.dayDifferenceBetweenTwoDates()}")

        c.getDetail()

        assert(c.dayDifferenceBetweenTwoDates() == 5L)


    }


}