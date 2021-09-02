package com.celestial.progress.data.model

import androidx.room.Entity
import androidx.room.PrimaryKey
import org.joda.time.DurationFieldType
import org.joda.time.LocalDate
import org.joda.time.Period
import org.joda.time.PeriodType
import java.text.ParseException
import java.text.SimpleDateFormat
import java.util.*
import java.util.concurrent.TimeUnit

@Entity
class Counter(
        val title: String,
        val startDate: String,
        val endDate: String?,
        val isElapsed: Boolean,
        val color: Int,
        val note: String,

        @PrimaryKey
        val id: Int? = null
) {

    fun dayDifferenceBetweenTwoDates(): Long?{
        val d1 = this.startDate.getDate()
        val d2 = this.endDate?.getDate()

        d2?.let{

            val calendar1 = Calendar.getInstance().resetToMidnight(d1)
            val calendar2 = Calendar.getInstance().resetToMidnight(it)
            println("Calendar1 ${calendar1.toString()}")
            println("Calendar2 ${calendar2.toString()}")
            return TimeUnit.MILLISECONDS.toDays(Math.abs(calendar1.timeInMillis - calendar2.timeInMillis))

        }
            return null
    }



    fun getDetail(): Period{
        val start = LocalDate.parse(startDate)
        val end = LocalDate.parse(endDate)

        val fields = PeriodType.forFields(arrayOf(DurationFieldType.years(),
                DurationFieldType.months(), DurationFieldType.days()
        ))
        val period = Period(start, end) // normalize to months and days
                .normalizedStandard(fields)
        println("Year: ${period.years}, Months: ${period.months}, Days: ${period.days}")
        return period
    }

}

fun Calendar.resetToMidnight(date: Date): Calendar{
    this.time = date
    this.set(Calendar.HOUR_OF_DAY, 0)
    this.set(Calendar.MINUTE, 0)
    this.set(Calendar.MILLISECOND, 0)
    return this
}

fun String.getDate(): Date{
    var date: Date? = null
    try {
        date = SimpleDateFormat("yyyy-MM-dd").parse(this)

    } catch (e: ParseException) {
        e.printStackTrace()
        return Calendar.getInstance().time
    }
    return date
}
