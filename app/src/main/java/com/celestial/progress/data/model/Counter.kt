package com.celestial.progress.data.model


import androidx.room.Entity
import androidx.room.Ignore
import androidx.room.PrimaryKey
import org.joda.time.*
import java.text.ParseException
import java.text.SimpleDateFormat
import java.util.*
import java.util.concurrent.TimeUnit

const val COMPLETE = -1L
const val OVER = -2L
const val INVALID = -3L

@Entity
class Counter(
        val title: String,
        val startDate: String,
        val endDate: String?,
        val color: Int?,
        val note: String?,
        var requiredNotification: Boolean = false,
        val displayFormat: DisplayFormat = DisplayFormat.DAY,
        var order: Int? = null,
        val createdDate: String = Calendar.getInstance().toString(),
        var isExpand: Boolean = false,
        var isArchived: Boolean = false,
        val repeating: Repeating = Repeating.ONCE,

        @PrimaryKey
        var id: Int? = null
) {
    @Ignore
    var isCheckedForWidget: Boolean = false


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

    fun isElapsed(): Boolean{
        if (endDate != null) {
            if(endDate.isEmpty()){
                return true
            }
        }
        return false
    }


    fun getDetail(isRemainingDate: Boolean = false): String {
        var suffix = ""
        //add start date is in the future
        val c = Calendar.getInstance().time
        val current = Calendar.getInstance().resetToMidnight(c)

        if(isComplete()){
            return "0 Day(s)"
        }

        val start =if(!isRemainingDate) LocalDate.parse(startDate)
        else if(isRemainingDate && startDate.getDate()>current.time){
            suffix = " from today"
            LocalDate.parse(current.getCurrentDateString())
        }else{
            LocalDate.parse(current.getCurrentDateString())
        }

        val end = if (!endDate?.isEmpty()!!) {
            LocalDate.parse(endDate)
        } else {
            LocalDate.parse(Calendar.getInstance().getCurrentDateString())
        }

        if (displayFormat == DisplayFormat.DAY || displayFormat == DisplayFormat.WEEK_DAY) {
            val days = Days.daysBetween(start, end).days
            return when (displayFormat) {
                DisplayFormat.DAY -> "$days Day(s)"
                else -> {
                    val wk = days / 7
                    val day = days % 7
                    "$wk Week(s), $day Day(s)"
                }
            }+suffix

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
            }+suffix
        }
    }

    fun isComplete(): Boolean {
        endDate?.let{
            var current: Calendar = Calendar.getInstance().apply {
                resetToMidnight(this.time)
            }
            if(current.time >=it.getDate()){
                return true
            }
            return false
        }
        return false
    }

    fun isStarted():Boolean{
        if(startDate.getDate()> Calendar.getInstance().time){
            return false
        }
        return true
    }

    fun getRemainingDayForStart(){

    }


    fun getPercent(): Long? {

        if(startDate.getDate()> Calendar.getInstance().time){
            return 0L
        }

        if(endDate!!.isEmpty()){
            return INVALID
        }
            endDate?.let {
                val currentDate = Calendar.getInstance().apply {
                    resetToMidnight(this.time)
                }

                if(currentDate.time > endDate.getDate()){
                    return OVER
                }

                if(currentDate.time == endDate.getDate()){
                    return COMPLETE
                }

                val dayReach = dayDifferenceBetweenTwoDates(startDate, currentDate.getCurrentDateString())

                val dayTotal = dayDifferenceBetweenTwoDates()

                println("DayReach: ${dayReach.toString()}")
                println("DayTotal: ${dayTotal.toString()}")

                val percent: Float? = (dayReach?.toFloat()?.div(dayTotal?.toFloat()!!))

                println("Result : ${percent.toString()}")

                return percent?.times(100)?.toLong()
            }

            return INVALID

    }

    fun getInitial(): String{
        return if(isElapsed())
            "Elapsed : "
        else
            "Remaining : "
    }
}

fun Calendar.resetToMidnight(date: Date): Calendar {
    this.time = date
    this.set(Calendar.HOUR_OF_DAY, 0)
    this.set(Calendar.MINUTE, 0)
    this.set(Calendar.MILLISECOND, 0)
    return this
}

fun dayDifferenceBetweenTwoDates(date1: String, date2: String): Long? {
    val d1 = date1.getDate()
    val d2 = date2.getDate()

    d2?.let {
        val calendar1 = Calendar.getInstance().resetToMidnight(d1)
        val calendar2 = Calendar.getInstance().resetToMidnight(it)
        println("Calendar1 ${calendar1.toString()}")
        println("Calendar2 ${calendar2.toString()}")
        return TimeUnit.MILLISECONDS.toDays(Math.abs(calendar1.timeInMillis - calendar2.timeInMillis))
    }
    return null
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
    YEAR_MONTH_DAY,
    YEAR_MONTH_WEEK_DAY;
}

enum class Repeating{
    ONCE ,MONTHLY, YEARLY
}

fun Calendar.getCurrentDateString(): String {
    val cal = Calendar.getInstance()
    val date = cal.time
    val sdf = SimpleDateFormat("yyyy-MM-dd")
    return sdf.format(date)
}
