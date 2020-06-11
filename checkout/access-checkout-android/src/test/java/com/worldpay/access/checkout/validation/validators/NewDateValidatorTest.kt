package com.worldpay.access.checkout.validation.validators

import org.junit.Test
import java.util.*
import java.util.Calendar.*
import kotlin.test.assertFalse
import kotlin.test.assertTrue

class NewDateValidatorTest {

    private val dateValidator = NewDateValidator()

    @Test
    fun `should be invalid if month and year are empty`() {
        assertFalse(dateValidator.validate( ""))
    }

    @Test
    fun `should be invalid if month is empty`() {
        assertFalse(dateValidator.validate( "99"))
    }

    @Test
    fun `should be invalid if month is null`() {
        assertFalse(dateValidator.validate( "99"))
    }

    @Test
    fun `should be invalid if year is empty`() {
        assertFalse(dateValidator.validate( "12"))
    }

    @Test
    fun `should be invalid if year is null`() {
        assertFalse(dateValidator.validate( "12"))
    }

    @Test
    fun `should be invalid if month is invalid`() {
        assertFalse(dateValidator.validate( "aa/99"))
    }

    @Test
    fun `should be invalid if month is too short`() {
        assertFalse(dateValidator.validate( "1/99"))
    }

    @Test
    fun `should be invalid if year is invalid`() {
        assertFalse(dateValidator.validate( "12/aa"))
    }

    @Test
    fun `should be invalid if year is too short`() {
        assertFalse(dateValidator.validate( "12/1"))
    }

    @Test
    fun `should be invalid if year is in the past`() {
        assertFalse(dateValidator.validate("12/18"))
    }

    @Test
    fun `should be invalid if month is in the past of current year`() {
        val now = setDate(10, 20)
        val dateValidator = NewDateValidator(now)

        assertFalse(dateValidator.validate("09/20"))
    }

    @Test
    fun `should be valid if last day of current month of current year`() {
        val now = setDate(10, 20)
        val dateValidator = NewDateValidator(now)

        assertTrue(dateValidator.validate("10/20"))
    }

    @Test
    fun `should be valid if year is in the future`() {
        assertTrue(dateValidator.validate("10/30"))
    }

    private fun setDate(month: Int, year: Int): Calendar {
        val date = getInstance()
        date.set(YEAR, 2000 + year)
        date.set(MONTH, month - 1)
        date.set(DAY_OF_MONTH, date.getActualMaximum(DAY_OF_MONTH))
        date.set(HOUR_OF_DAY, date.getActualMaximum(HOUR_OF_DAY))
        date.set(MINUTE, date.getActualMaximum(MINUTE))
        date.set(SECOND, date.getActualMaximum(SECOND))
        date.set(MILLISECOND, date.getActualMaximum(MILLISECOND))

        return date
    }
}
