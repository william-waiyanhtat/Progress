package com.celestial.progress.data.model

import androidx.room.Entity
import androidx.room.PrimaryKey
import org.joda.time.*
import java.text.ParseException
import java.text.SimpleDateFormat
import java.util.*
import java.util.concurrent.TimeUnit

@Entity
class Counter(
    val title: String,
    val startDate: String,
    val endDate: String?,
    val isElapsed: Boolean? = true,
    val color: Int?,
    val note: String?,
    val requiredNotification: Boolean = false,
    val displayFormat: DisplayFormat = DisplayFormat.DAY,

    @PrimaryKey
    val id: Int? = null
) {

    fun dayDifferenceBetweenTwoDates(): Long? {
        val d1 = this.startDate.getDate()
        val d2 = this.endDate?.getDate()

        d2?.let {
            val calendar1 = Calendar.getInstance().resetToMidnight(d1)
            val calendar2 = Calendar.getInstance().resetToMidnight(it)
            println("Calendar1 ${calendar1.toString()}")
            println("Calendar2 ${calendar2.toString()}")
            return TimeUnit.MILLISECONDS.toDays(Math.abs(calendar1.timeInMillis - calendar2.timeInMillis))
        }
        return null
    }

    fun getDetail(): String {
        val start = LocalDate.parse(startDate)
        val end = LocalDate.parse(endDate)

        print(Days.daysBetween(start, end).days)

        if (displayFormat == DisplayFormat.DAY || displayFormat == DisplayFormat.WEEK_DAY) {
            val days = Days.daysBetween(start, end).days
            return when (displayFormat) {
                DisplayFormat.DAY -> "$days Day(s)"
                else -> {
                    val wk = days / 7
                    val day = days % 7

                    "$wk Week(s), $day Day(s)"
                }
            }

        } else {
            var field = when (displayFormat) {
                (DisplayFormat.WEEK_DAY) -> PeriodType.forFields(
                    arrayOf(
                        DurationFieldType.weeks(),
                        DurationFieldType.days()
                    )
                )
                (DisplayFormat.MONTH_WEEK_DAY) -> PeriodType.forFields(
                    arrayOf(
                        DurationFieldType.months(),
                        DurationFieldType.weeks(), DurationFieldType.days()
                    )
                )
                (DisplayFormat.YEAR_MONTH_DAY) -> PeriodType.forFields(
                    arrayOf(
                        DurationFieldType.years(),
                        DurationFieldType.months(), DurationFieldType.days()
                    )
                )
                (DisplayFormat.YEAR_MONTH_WEEK_DAY) -> PeriodType.forFields(
                    arrayOf(
                        DurationFieldType.years(),
                        DurationFieldType.months(),
                        DurationFieldType.weeks(),
                        DurationFieldType.days()
                    )
                )
                else -> PeriodType.forFields(arrayOf(DurationFieldType.days()))
            }

            val period = Period(start, end) // normalize to months and days
                .normalizedStandard(field)

            return when (displayFormat) {
                DisplayFormat.YEAR_MONTH_WEEK_DAY -> "${period.years} Year(s), ${period.months} Month(s), ${period.weeks} Week(s), ${period.days} Day(s)"
                DisplayFormat.YEAR_MONTH_DAY -> "${period.years} Year(s), ${period.months} Month(s), ${period.days} Day(s)"
                DisplayFormat.MONTH_WEEK_DAY -> "${period.months} Month(s), ${period.weeks} Week(s), ${period.days} Day(s)"
                DisplayFormat.WEEK_DAY -> "${period.weeks} Week(s), ${period.days} Day(s)"
                else -> "${period.days} Day(s)"
            }
        }
    }


}

fun Calendar.resetToMidnight(date: Date): Calendar {
    this.time = date
    this.set(Calendar.HOUR_OF_DAY, 0)
    this.set(Calendar.MINUTE, 0)
    this.set(Calendar.MILLISECOND, 0)
    return this
}

fun String.getDate(): Date {
    var date: Date? = null
    try {
        date = SimpleDateFormat("yyyy-MM-dd").parse(this)
    } catch (e: ParseException) {
        e.printStackTrace()
        return Calendar.getInstance().time
    }
    return date
}

enum class DisplayFormat() {
    DAY,
    WEEK_DAY,
    MONTH_WEEK_DAY,
    YEAR_MONTH_WEEK_DAY,
    YEAR_MONTH_DAY;

}