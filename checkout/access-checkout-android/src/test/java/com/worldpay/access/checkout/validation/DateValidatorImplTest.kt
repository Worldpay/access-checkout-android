package com.worldpay.access.checkout.validation

import com.worldpay.access.checkout.model.CardConfiguration
import com.worldpay.access.checkout.model.CardDefaults
import com.worldpay.access.checkout.model.CardValidationRule
import org.junit.Before
import org.junit.Test
import java.util.*
import kotlin.test.assertEquals
import kotlin.test.assertFalse
import kotlin.test.assertTrue

class DateValidatorImplTest {

    lateinit var validator: DateValidator
    lateinit var now: Calendar

    @Before
    fun setup() {
        now = Calendar.getInstance()
        val monthRule = CardValidationRule("^0[1-9]{0,1}$|^1[0-2]{0,1}$", 2, 2, null)
        val yearRule = CardValidationRule("^\\d{0,2}$", 2, 2, null)
        validator = DateValidatorImpl(now, CardConfiguration(null, CardDefaults(null, null, monthRule, yearRule)))
    }

    @Test
    fun `given no default rules then should be completely and partially valid`() {
        val emptyConfigValidator = DateValidatorImpl(now, CardConfiguration())

        assertEquals(ValidationResult(partial = true, complete = true), emptyConfigValidator.validate("", ""))
    }

    @Test
    fun `given no month and year rule then should be completely and partially valid`() {
        val emptyConfigValidator = DateValidatorImpl(now, CardConfiguration(null, CardDefaults(null, null, null, null)))

        assertEquals(ValidationResult(partial = true, complete = true), emptyConfigValidator.validate("", ""))
    }

    @Test
    fun `given no month rule only then should be completely and partially valid`() {
        val emptyConfigValidator = DateValidatorImpl(now, CardConfiguration(null, CardDefaults(null, null, null, CardValidationRule(null, null, null, 2))))

        assertEquals(ValidationResult(partial = true, complete = true), emptyConfigValidator.validate("", ""))
    }

    @Test
    fun `given no year rule only then should be completely and partially valid`() {
        val emptyConfigValidator = DateValidatorImpl(now, CardConfiguration(null, CardDefaults(null, null, CardValidationRule(null, null, null, 2), null)))

        assertEquals(ValidationResult(partial = true, complete = true), emptyConfigValidator.validate("", ""))
    }

    @Test
    fun `given month and year rule without matcher then will validate against only length check`() {
        setCurrentDate(2019, 5 /* May */, 7)

        val monthRule = CardValidationRule(null, null, null, 2)
        val yearRule = CardValidationRule(null, null, null, 2)
        val emptyMatcherConfigValidator = DateValidatorImpl(now, CardConfiguration(null, CardDefaults(null, null, monthRule, yearRule)))

        assertEquals(ValidationResult(partial = false, complete = true), emptyMatcherConfigValidator.validate("12", "20"))
    }

    @Test
    fun `given an invalid date format then should be completely and partially invalid`() {
        assertEquals(ValidationResult(partial = false, complete = false), validator.validate("-1", "-1"))
    }

    @Test
    fun `given partially valid single digit month and a completely valid year then should be partially or completely invalid`() {
        setCurrentDate(2019, 5 /* May */, 7)

        assertEquals(ValidationResult(partial = false, complete = false), validator.validate("1", "20"))
    }

    @Test
    fun `given invalid single digit month and a completely valid year then should be completely invalid`() {
        setCurrentDate(2019, 5 /* May */, 7)

        assertEquals(ValidationResult(partial = false, complete = false), validator.validate("2", "20"))
    }

    @Test
    fun `given future date for next year then should be completely valid`() {
        setCurrentDate(2019, 5 /* May */, 7)

        assertEquals(ValidationResult(partial = false, complete = true), validator.validate("11", "20"))
    }

    @Test
    fun `given future date in the current year then should be completely valid`() {
        setCurrentDate(2019, 5 /* May */, 7)

        assertEquals(ValidationResult(partial = false, complete = true), validator.validate("11", "19"))
    }

    @Test
    fun `given past date in the current year then should be completely invalid`() {
        setCurrentDate(2019, 5 /* May */, 7)

        assertEquals(ValidationResult(partial = false, complete = false), validator.validate("01", "19"))
    }

    @Test
    fun `given past date for last year then should be completely invalid`() {
        setCurrentDate(2019, 5 /* May */, 7)

        assertEquals(ValidationResult(partial = false, complete = false), validator.validate("01", "18"))
    }

    @Test
    fun `given last day of a month then should be completely valid`() {
        setCurrentDate(2019, 2 /* March */, 31)
        now.set(Calendar.HOUR_OF_DAY, 23)
        now.set(Calendar.MINUTE, 59)
        now.set(Calendar.SECOND, 59)
        now.set(Calendar.MILLISECOND, 999)

        assertEquals(ValidationResult(partial = false, complete = true), validator.validate("03", "19"))
    }

    @Test
    fun `given invalid two digit month and a completely valid year then should be completely invalid`() {
        setCurrentDate(2019, 5 /* May */, 7)

        assertEquals(ValidationResult(partial = false, complete = false), validator.validate("13", "20"))
    }

    @Test
    fun `given partially valid month and a completely invalid year then should be completely invalid`() {
        assertEquals(ValidationResult(partial = false, complete = false), validator.validate("1", "-1"))
    }

    @Test
    fun `given partially invalid month and a completely invalid year then should be completely invalid`() {
        assertEquals(ValidationResult(partial = false, complete = false), validator.validate("2", "-1"))
    }

    @Test
    fun `given completely valid month and a completely invalid year then should be completely invalid`() {
        assertEquals(ValidationResult(partial = false, complete = false), validator.validate("11", "-1"))
    }

    @Test
    fun `given completely invalid month and a completely invalid year then should be completely invalid`() {
        assertEquals(ValidationResult(partial = false, complete = false), validator.validate("13", "-1"))
    }

    @Test
    fun `given minus month and a completely valid year then should be completely invalid`() {
        setCurrentDate(2019, 5 /* May */, 7)

        assertEquals(ValidationResult(partial = false, complete = false), validator.validate("-1", "20"))
    }

    @Test
    fun `given alpha month and a completely valid year then should be completely invalid`() {
        setCurrentDate(2019, 5 /* May */, 7)

        assertEquals(ValidationResult(partial = false, complete = false), validator.validate("A", "20"))
    }

    @Test
    fun `given completely valid month and alpha year then should be completely invalid`() {
        setCurrentDate(2019, 5 /* May */, 7)

        assertEquals(ValidationResult(partial = false, complete = false), validator.validate("12", "A"))
    }

    @Test
    fun `given valid months and no year then should be completely valid`() {
        assertEquals(ValidationResult(partial = false, complete = true), validator.validate("01", null))
        assertEquals(ValidationResult(partial = false, complete = true), validator.validate("02", null))
        assertEquals(ValidationResult(partial = false, complete = true), validator.validate("03", null))
        assertEquals(ValidationResult(partial = false, complete = true), validator.validate("04", null))
        assertEquals(ValidationResult(partial = false, complete = true), validator.validate("05", null))
        assertEquals(ValidationResult(partial = false, complete = true), validator.validate("06", null))
        assertEquals(ValidationResult(partial = false, complete = true), validator.validate("07", null))
        assertEquals(ValidationResult(partial = false, complete = true), validator.validate("08", null))
        assertEquals(ValidationResult(partial = false, complete = true), validator.validate("09", null))
        assertEquals(ValidationResult(partial = false, complete = true), validator.validate("10", null))
        assertEquals(ValidationResult(partial = false, complete = true), validator.validate("11", null))
        assertEquals(ValidationResult(partial = false, complete = true), validator.validate("12", null))
    }

    @Test
    fun `given minus month and no year then should be completely invalid`() {
        assertEquals(ValidationResult(partial = false, complete = false), validator.validate("-1", null))
    }

    @Test
    fun `given alpha month and no year then should be completely invalid`() {
        assertEquals(ValidationResult(partial = false, complete = false), validator.validate("A", null))
    }

    @Test
    fun `given invalid month and no year then should be completely invalid`() {
        assertEquals(ValidationResult(partial = false, complete = false), validator.validate("13", null))
    }

    @Test
    fun `given zero as month and no year then should be partially valid`() {
        assertEquals(ValidationResult(partial = true, complete = false), validator.validate("0", null))
    }

    @Test
    fun `given one as month and no year then should be partially valid`() {
        assertEquals(ValidationResult(partial = true, complete = false), validator.validate("1", null))
    }

    @Test
    fun `given invalid partial month and no year then should be completely and partially invalid`() {
        assertEquals(ValidationResult(partial = false, complete = false), validator.validate("2", null))
    }

    @Test
    fun `given minus year and no month then should be completely invalid`() {
        assertEquals(ValidationResult(partial = false, complete = false), validator.validate(null, "-1"))
    }

    @Test
    fun `given alpha year and no month then should be completely invalid`() {
        assertEquals(ValidationResult(partial = false, complete = false), validator.validate(null, "A"))
    }

    @Test
    fun `given single digit year then should be partially valid`() {
        assertEquals(ValidationResult(partial = true, complete = false), validator.validate(null, "1"))
    }

    @Test
    fun `given past year only then should be completely and partially invalid`() {
        setCurrentDate(2019, 5 /* May */, 7)

        assertEquals(ValidationResult(partial = false, complete = false), validator.validate(null, "18"))
    }

    @Test
    fun `given current year only then should be completely valid`() {
        setCurrentDate(2019, 5 /* May */, 7)

        assertEquals(ValidationResult(partial = false, complete = true), validator.validate(null, "19"))
    }

    @Test
    fun `given future year only then should be completely valid`() {
        setCurrentDate(2019, 5 /* May */, 7)

        assertEquals(ValidationResult(partial = false, complete = true), validator.validate(null, "20"))
    }

    @Test
    fun `given empty configuration then dates can be updated`() {
        val emptyConfigValidator = DateValidatorImpl(now, CardConfiguration())

        assertTrue(emptyConfigValidator.canUpdate("", ""))
        assertTrue(emptyConfigValidator.canUpdate("1", "1"))
        assertTrue(emptyConfigValidator.canUpdate("A", "A"))
    }

    @Test
    fun `given empty year rule then dates can be updated`() {
        val emptyConfigValidator = DateValidatorImpl(now, CardConfiguration(null, CardDefaults(null, null, CardValidationRule(null, null, null, null), null)))

        assertTrue(emptyConfigValidator.canUpdate("", ""))
        assertTrue(emptyConfigValidator.canUpdate("1", "1"))
        assertTrue(emptyConfigValidator.canUpdate("A", "A"))
    }

    @Test
    fun `given empty month rule then dates can be updated`() {
        val emptyConfigValidator = DateValidatorImpl(now, CardConfiguration(null, CardDefaults(null, null, null, CardValidationRule(null, null, null, null))))

        assertTrue(emptyConfigValidator.canUpdate("", ""))
        assertTrue(emptyConfigValidator.canUpdate("1", "1"))
        assertTrue(emptyConfigValidator.canUpdate("A", "A"))
    }

    @Test
    fun `given empty year then can be updated`() {
        assertTrue(validator.canUpdate(null, ""))
    }

    @Test
    fun `given single digit year then then can be updated`() {
        assertTrue(validator.canUpdate(null, "2"))
    }

    @Test
    fun `given two digit year then can be updated`() {
        assertTrue(validator.canUpdate(null, "20"))
    }

    @Test
    fun `given three digit year then can be updated`() {
        assertTrue(validator.canUpdate(null, "201"))
    }

    @Test
    fun `given empty month then then can be updated`() {
        assertTrue(validator.canUpdate("", null))
    }

    @Test
    fun `given single digit month then can be updated`() {
        assertTrue(validator.canUpdate("1", null))
    }

    @Test
    fun `given two digit month then can be updated`() {
        assertTrue(validator.canUpdate("12", null))
    }

    @Test
    fun `given three digit month then can be updated`() {
        assertTrue(validator.canUpdate("123", null))
    }

    @Test
    fun `given empty month and year then dates can be updated`() {
        assertTrue(validator.canUpdate("", ""))
    }

    @Test
    fun `given single digit month and empty year then dates can be updated`() {
        assertTrue(validator.canUpdate("1", ""))
    }

    @Test
    fun `given two digit month and empty year then can be updated`() {
        assertTrue(validator.canUpdate("12", ""))
    }

    @Test
    fun `given two digit month and single digit year then can be updated`() {
        assertTrue(validator.canUpdate("12", "2"))
    }

    @Test
    fun `given two digit month and two digit year then cannot be updated`() {
        assertFalse(validator.canUpdate("12", "12"))
    }

    @Test
    fun `given three digit month and two digit year then can be updated`() {
        assertTrue(validator.canUpdate("123", "12"))
    }

    @Test
    fun `given three digit month and three digit year then can be updated`() {
        assertTrue(validator.canUpdate("123", "123"))
    }

    private fun setCurrentDate(year: Int, month: Int, day: Int) {
        now.set(Calendar.YEAR, year)
        now.set(Calendar.MONTH, month)
        now.set(Calendar.DAY_OF_MONTH, day)
    }
}