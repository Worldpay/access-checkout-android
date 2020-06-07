package com.worldpay.access.checkout.validation.validators

import com.worldpay.access.checkout.api.configuration.DefaultCardRules
import com.worldpay.access.checkout.api.configuration.DefaultCardRules.MONTH_DEFAULTS
import com.worldpay.access.checkout.api.configuration.DefaultCardRules.YEAR_DEFAULTS
import org.junit.Test
import java.util.*
import java.util.Calendar.*
import kotlin.test.assertEquals
import kotlin.test.assertFalse
import kotlin.test.assertTrue

class NewDateValidatorTest {
    private val dateValidator = NewDateValidator()

    @Test
    fun `should be invalid if month and year are empty`() {
        assertFalse(dateValidator.validate( "", ""))
    }

    @Test
    fun `should be invalid if month is empty`() {
        assertFalse(dateValidator.validate( "", "99"))
    }

    @Test
    fun `should be invalid if month is null`() {
        assertFalse(dateValidator.validate( null, "99"))
    }

    @Test
    fun `should be invalid if year is empty`() {
        assertFalse(dateValidator.validate( "12", ""))
    }

    @Test
    fun `should be invalid if year is null`() {
        assertFalse(dateValidator.validate( "12", null))
    }

    @Test
    fun `should be invalid if month is invalid`() {
        assertFalse(dateValidator.validate( "aa", "99"))
    }

    @Test
    fun `should be invalid if month is too short`() {
        assertFalse(dateValidator.validate( "1", "99"))
    }

    @Test
    fun `should be invalid if year is invalid`() {
        assertFalse(dateValidator.validate( "12", "aa"))
    }

    @Test
    fun `should be invalid if year is too short`() {
        assertFalse(dateValidator.validate( "12", "1"))
    }

    @Test
    fun `should be invalid if year is in the past`() {
        val now = setDate(10, 20)
        val dateValidator = NewDateValidator(now)

        assertFalse(dateValidator.validate("12", "18"))
    }

    @Test
    fun `should be invalid if month is in the past of current year`() {
        val now = setDate(10, 20)
        val dateValidator = NewDateValidator(now)

        assertFalse(dateValidator.validate("09", "20"))
    }

    @Test
    fun `should be valid if last day of current month of current year`() {
        val now = setDate(10, 20)
        val dateValidator = NewDateValidator(now)

        assertTrue(dateValidator.validate("10", "20"))
    }

    @Test
    fun `should be valid if year is in the future`() {
        val now = setDate(10, 20)
        val dateValidator = NewDateValidator(now)

        assertTrue(dateValidator.validate("10", "21"))
    }

    @Test
    fun `should return a pair containing default month and year rules`() {
        val rules = dateValidator.getValidationRule()

        assertEquals(YEAR_DEFAULTS, rules.second)
        assertEquals(MONTH_DEFAULTS, rules.first)
    }

    private fun setDate(month: Int, year: Int): Calendar {
        val date = getInstance()
        date.set(YEAR, 2000 + year)
        date.set(MONTH, month)
        date.set(Calendar.DAY_OF_MONTH, 30)
        date.set(Calendar.HOUR_OF_DAY, 23)
        date.set(Calendar.MINUTE, 59)
        date.set(Calendar.SECOND, 59)
        date.set(Calendar.MILLISECOND, 999)

        return date
    }
}
