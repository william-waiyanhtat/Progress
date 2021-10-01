package com.celestial.progress.others

import com.celestial.progress.data.model.getDate
import java.util.regex.Pattern

object Validator {


    val errorStringEmpty = "Counter name can't be empty"

    val errorMinCharCount = "Counter name can't be less than ${Constants.counterNameCharMinimum}"

    val errorMaxCharCount = "Counter name can't be more than ${Constants.counterNameCharCountMax}"

    val errorSpecailChar = "Counter name can't contain special character"

    val p = Pattern.compile("[^a-z0-9]", Pattern.CASE_INSENSITIVE)

    val emptyStartDateError = "Start date can't be empty!"

    val startDateEarlierError = "Start date can't be earlier than End date"

    val startDateEqualError = "Start date can't be same as End date"

    val endDateMustBeAdd = "End date can't be empty for Countdown Progress"

    /*
    input empty
    input more than counter character count exceed certain limit
    input contain special character
     */
    fun verifyCounterName(input: String): Resource<Boolean> {
        if (input.isEmpty())
            return Resource.error(errorStringEmpty, false)

        if (input.length > Constants.counterNameCharCountMax)
            return Resource.error(errorMaxCharCount, false)

        if (p.matcher(input).find())
            return Resource.error(errorSpecailChar, false)

        if (input.length < Constants.counterNameCharMinimum)
            return Resource.error(errorMinCharCount, false)

        return Resource.success(true,null)
    }

    fun verifyInputDateString(startDate: String, endDate: String?, isCountDown: Boolean): Resource<Int> {
        val date1 = startDate.getDate()
        val date2 = endDate?.getDate()


        if(startDate.isNotEmpty() && endDate?.isEmpty()!! && isCountDown){
            return Resource.error(endDateMustBeAdd,-1)
        }


        if(startDate.isNotEmpty() && endDate?.isEmpty()!!){
            return Resource.success(1,null)
        }


        if (startDate.isEmpty()) {
            return Resource.error(emptyStartDateError, 0)
        }

        date2?.let {
            if(it<date1){
                return Resource.error(startDateEarlierError,0)
            }

            if(it==date1){
                return Resource.error(startDateEqualError,0)
            }

        }

        return Resource.success(1,null)
    }



}