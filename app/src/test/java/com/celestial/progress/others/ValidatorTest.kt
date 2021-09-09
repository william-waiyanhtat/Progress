package com.celestial.progress.others

import com.celestial.progress.others.Validator.emptyStartDateError
import com.celestial.progress.others.Validator.errorMaxCharCount
import com.celestial.progress.others.Validator.errorMinCharCount
import com.celestial.progress.others.Validator.errorSpecailChar
import com.celestial.progress.others.Validator.errorStringEmpty
import com.celestial.progress.others.Validator.startDateEarlierError
import com.celestial.progress.others.Validator.startDateEqualError
import com.celestial.progress.others.Validator.verifyCounterName
import com.celestial.progress.others.Validator.verifyInputDateString
import com.google.common.truth.Truth.assertThat

import org.junit.Test
import java.lang.StringBuilder

class ValidatorTest {

    @Test
    fun checkEmptyInputCounter() {
        val result = verifyCounterName("")
        assertThat(result).isEqualTo(Resource.error(errorStringEmpty, false))
    }

    @Test
    fun inputExceedCharacterCount() {
        val st = StringBuilder()
        for (i in 1..Constants.counterNameCharCountMax + 1) {
            st.append(1)
        }
        val input = st.toString()
        val result = verifyCounterName(input)

        assertThat(result).isEqualTo(Resource.error(errorMaxCharCount, false))
    }

    @Test
    fun inputSpecailCharTest() {
        val result = verifyCounterName("#ai")
        assertThat(result).isEqualTo(Resource.error(errorSpecailChar, false))
    }

    @Test
    fun inputLessThanCounterNameLength() {
        val result = verifyCounterName("22")
        assertThat(result).isEqualTo(Resource.error(errorMinCharCount, false))
    }

    @Test
    fun startDateEmptyTest() {
        val result = verifyInputDateString("", "")
        assertThat(result).isEqualTo(Resource.error(emptyStartDateError, false))
    }

    @Test
    fun startDateGreaterThanEndDate() {
        val result = verifyInputDateString("2021-08-11", "2021-08-10")
        assertThat(result).isEqualTo(Resource.error(startDateEarlierError, false))
    }

    @Test
    fun startDateEqualToEndDate() {
        val result = verifyInputDateString("2021-08-11", "2021-08-11")
        assertThat(result).isEqualTo(Resource.error(startDateEqualError, false))
    }
}