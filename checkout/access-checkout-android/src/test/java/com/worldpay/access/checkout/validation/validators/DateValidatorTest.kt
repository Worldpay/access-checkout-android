package com.worldpay.access.checkout.validation.validators

import com.worldpay.access.checkout.testutils.CardConfigurationUtil.Configurations.CARD_CONFIG_BASIC
import com.worldpay.access.checkout.testutils.CardConfigurationUtil.Configurations.CARD_CONFIG_NO_BRAND
import com.worldpay.access.checkout.testutils.CardConfigurationUtil.Defaults.EXP_MONTH_RULE
import com.worldpay.access.checkout.testutils.CardConfigurationUtil.Defaults.EXP_YEAR_RULE
import com.worldpay.access.checkout.validation.ValidationResult
import org.junit.Test
import java.util.*
import kotlin.test.assertEquals
import kotlin.test.assertFalse
import kotlin.test.assertTrue

class DateValidatorTest {

    private val dateValidator =
        DateValidator()

    @Test
    fun `given no month and year rule and empty dates then should be partially valid`() {
        assertEquals(
            ValidationResult(
                partial = true,
                complete = false
            ), dateValidator.validate("", "", CARD_CONFIG_NO_BRAND))
    }

    @Test
    fun `given no month rule only and empty dates then should be partially valid`() {
        assertEquals(
            ValidationResult(
                partial = true,
                complete = false
            ), dateValidator.validate("", "", CARD_CONFIG_NO_BRAND))
    }

    @Test
    fun `given no year rule only and empty dates then should be partially valid`() {
        assertEquals(
            ValidationResult(
                partial = true,
                complete = false
            ), dateValidator.validate("", "", CARD_CONFIG_NO_BRAND))
    }

    @Test
    fun `given empty month and year then should be partially valid`() {
        assertEquals(
            ValidationResult(
                partial = true,
                complete = false
            ), dateValidator.validate("", "", CARD_CONFIG_NO_BRAND))
    }

    @Test
    fun `given empty month only then should be partially and completely invalid`() {
        assertEquals(
            ValidationResult(
                partial = false,
                complete = false
            ), dateValidator.validate("", "20", CARD_CONFIG_NO_BRAND))
    }

    @Test
    fun `given empty year only then should be partially and completely invalid`() {
        assertEquals(
            ValidationResult(
                partial = false,
                complete = false
            ), dateValidator.validate("12", "", CARD_CONFIG_NO_BRAND))
    }

    @Test
    fun `given month and year rule without matcher then will validate against only length check`() {
        val dateValidator = getValidatorWithDate(2019, 5, 7)

        assertEquals(
            ValidationResult(
                partial = false,
                complete = true
            ), dateValidator.validate("12", "20", CARD_CONFIG_NO_BRAND))
    }

    @Test
    fun `given an invalid date format then should be completely and partially invalid`() {
        assertEquals(
            ValidationResult(
                partial = false,
                complete = false
            ), dateValidator.validate("-1", "-1", CARD_CONFIG_NO_BRAND))
    }

    @Test
    fun `given partially valid single digit month and a completely valid year then should be partially  and completely invalid`() {
        val dateValidator = getValidatorWithDate(2019, 5, 7)

        assertEquals(
            ValidationResult(
                partial = false,
                complete = false
            ), dateValidator.validate("1", "20", CARD_CONFIG_NO_BRAND))
    }

    @Test
    fun `given invalid single digit month and a completely valid year then should be completely invalid`() {
        val dateValidator = getValidatorWithDate(2019, 5, 7)

        assertEquals(
            ValidationResult(
                partial = false,
                complete = false
            ), dateValidator.validate("2", "20", CARD_CONFIG_NO_BRAND))
    }

    @Test
    fun `given future date for next year then should be completely valid`() {
        val dateValidator = getValidatorWithDate(2019, 5, 7)

        assertEquals(
            ValidationResult(
                partial = false,
                complete = true
            ), dateValidator.validate("11", "20", CARD_CONFIG_NO_BRAND))
    }

    @Test
    fun `given future date in the current year then should be completely valid`() {
        val dateValidator = getValidatorWithDate(2019, 5, 7)

        assertEquals(
            ValidationResult(
                partial = false,
                complete = true
            ), dateValidator.validate("11", "19", CARD_CONFIG_NO_BRAND))
    }

    @Test
    fun `given past date in the current year then should be completely invalid`() {
        val dateValidator = getValidatorWithDate(2019, 5, 7)

        assertEquals(
            ValidationResult(
                partial = false,
                complete = false
            ), dateValidator.validate("01", "19", CARD_CONFIG_NO_BRAND))
    }

    @Test
    fun `given past date for last year then should be completely invalid`() {
        val dateValidator = getValidatorWithDate(2019, 5, 7)

        assertEquals(
            ValidationResult(
                partial = false,
                complete = false
            ), dateValidator.validate("01", "18", CARD_CONFIG_NO_BRAND))
    }

    @Test
    fun `given last day of a month then should be completely valid`() {
        val now = Calendar.getInstance()
        now.set(Calendar.YEAR, 2019)
        now.set(Calendar.MONTH, 2)
        now.set(Calendar.DAY_OF_MONTH, 31)
        now.set(Calendar.HOUR_OF_DAY, 23)
        now.set(Calendar.MINUTE, 59)
        now.set(Calendar.SECOND, 59)
        now.set(Calendar.MILLISECOND, 999)

        val dateValidator =
            DateValidator(now)

        assertEquals(
            ValidationResult(
                partial = false,
                complete = true
            ), dateValidator.validate("03", "19", CARD_CONFIG_NO_BRAND))
    }

    @Test
    fun `given invalid two digit month and a completely valid year then should be completely invalid`() {
        val dateValidator = getValidatorWithDate(2019, 5, 7)

        assertEquals(
            ValidationResult(
                partial = false,
                complete = false
            ), dateValidator.validate("13", "20", CARD_CONFIG_NO_BRAND))
    }

    @Test
    fun `given partially valid month and a completely invalid year then should be completely invalid`() {
        assertEquals(
            ValidationResult(
                partial = false,
                complete = false
            ), dateValidator.validate("1", "-1", CARD_CONFIG_NO_BRAND))
    }

    @Test
    fun `given partially invalid month and a completely invalid year then should be completely invalid`() {
        assertEquals(
            ValidationResult(
                partial = false,
                complete = false
            ), dateValidator.validate("2", "-1", CARD_CONFIG_NO_BRAND))
    }

    @Test
    fun `given completely valid month and a completely invalid year then should be completely invalid`() {
        assertEquals(
            ValidationResult(
                partial = false,
                complete = false
            ), dateValidator.validate("11", "-1", CARD_CONFIG_NO_BRAND))
    }

    @Test
    fun `given completely invalid month and a completely invalid year then should be completely invalid`() {
        assertEquals(
            ValidationResult(
                partial = false,
                complete = false
            ), dateValidator.validate("13", "-1", CARD_CONFIG_NO_BRAND))
    }

    @Test
    fun `given minus month and a completely valid year then should be completely invalid`() {
        val dateValidator = getValidatorWithDate(2019, 5, 7)

        assertEquals(
            ValidationResult(
                partial = false,
                complete = false
            ), dateValidator.validate("-1", "20", CARD_CONFIG_NO_BRAND))
    }

    @Test
    fun `given alpha month and a completely valid year then should be completely invalid`() {
        val dateValidator = getValidatorWithDate(2019, 5, 7)

        assertEquals(
            ValidationResult(
                partial = false,
                complete = false
            ), dateValidator.validate("A", "20", CARD_CONFIG_NO_BRAND))
    }

    @Test
    fun `given completely valid month and alpha year then should be completely invalid`() {
        val dateValidator = getValidatorWithDate(2019, 5, 7)

        assertEquals(
            ValidationResult(
                partial = false,
                complete = false
            ), dateValidator.validate("12", "A", CARD_CONFIG_NO_BRAND))
    }

    @Test
    fun `given valid months and no year then should be completely valid`() {
        assertEquals(
            ValidationResult(
                partial = false,
                complete = true
            ), dateValidator.validate("01", null, CARD_CONFIG_NO_BRAND))
        assertEquals(
            ValidationResult(
                partial = false,
                complete = true
            ), dateValidator.validate("02", null, CARD_CONFIG_NO_BRAND))
        assertEquals(
            ValidationResult(
                partial = false,
                complete = true
            ), dateValidator.validate("03", null, CARD_CONFIG_NO_BRAND))
        assertEquals(
            ValidationResult(
                partial = false,
                complete = true
            ), dateValidator.validate("04", null, CARD_CONFIG_NO_BRAND))
        assertEquals(
            ValidationResult(
                partial = false,
                complete = true
            ), dateValidator.validate("05", null, CARD_CONFIG_NO_BRAND))
        assertEquals(
            ValidationResult(
                partial = false,
                complete = true
            ), dateValidator.validate("06", null, CARD_CONFIG_NO_BRAND))
        assertEquals(
            ValidationResult(
                partial = false,
                complete = true
            ), dateValidator.validate("07", null, CARD_CONFIG_NO_BRAND))
        assertEquals(
            ValidationResult(
                partial = false,
                complete = true
            ), dateValidator.validate("08", null, CARD_CONFIG_NO_BRAND))
        assertEquals(
            ValidationResult(
                partial = false,
                complete = true
            ), dateValidator.validate("09", null, CARD_CONFIG_NO_BRAND))
        assertEquals(
            ValidationResult(
                partial = false,
                complete = true
            ), dateValidator.validate("10", null, CARD_CONFIG_NO_BRAND))
        assertEquals(
            ValidationResult(
                partial = false,
                complete = true
            ), dateValidator.validate("11", null, CARD_CONFIG_NO_BRAND))
        assertEquals(
            ValidationResult(
                partial = false,
                complete = true
            ), dateValidator.validate("12", null, CARD_CONFIG_NO_BRAND))
    }

    @Test
    fun `given minus month and no year then should be completely invalid`() {
        assertEquals(
            ValidationResult(
                partial = false,
                complete = false
            ), dateValidator.validate("-1", null, CARD_CONFIG_NO_BRAND))
    }

    @Test
    fun `given alpha month and no year then should be completely invalid`() {
        assertEquals(
            ValidationResult(
                partial = false,
                complete = false
            ), dateValidator.validate("A", null, CARD_CONFIG_NO_BRAND))
    }

    @Test
    fun `given invalid month and no year then should be completely invalid`() {
        assertEquals(
            ValidationResult(
                partial = false,
                complete = false
            ), dateValidator.validate("13", null, CARD_CONFIG_NO_BRAND))
    }

    @Test
    fun `given zero as month and no year then should be partially valid`() {
        assertEquals(
            ValidationResult(
                partial = true,
                complete = false
            ), dateValidator.validate("0", null, CARD_CONFIG_NO_BRAND))
    }

    @Test
    fun `given one as month and no year then should be partially valid`() {
        assertEquals(
            ValidationResult(
                partial = true,
                complete = false
            ), dateValidator.validate("1", null, CARD_CONFIG_NO_BRAND))
    }

    @Test
    fun `given invalid partial month and no year then should be completely and partially invalid`() {
        assertEquals(
            ValidationResult(
                partial = false,
                complete = false
            ), dateValidator.validate("2", null, CARD_CONFIG_NO_BRAND))
    }

    @Test
    fun `given minus year and no month then should be completely invalid`() {
        assertEquals(
            ValidationResult(
                partial = false,
                complete = false
            ), dateValidator.validate(null, "-1", CARD_CONFIG_NO_BRAND))
    }

    @Test
    fun `given alpha year and no month then should be completely invalid`() {
        assertEquals(
            ValidationResult(
                partial = false,
                complete = false
            ), dateValidator.validate(null, "A", CARD_CONFIG_NO_BRAND))
    }

    @Test
    fun `given single digit year then should be partially valid`() {
        assertEquals(
            ValidationResult(
                partial = true,
                complete = false
            ), dateValidator.validate(null, "1", CARD_CONFIG_NO_BRAND))
    }

    @Test
    fun `given past year only then should be completely and partially invalid`() {
        val dateValidator = getValidatorWithDate(2019, 5, 7)

        assertEquals(
            ValidationResult(
                partial = false,
                complete = false
            ), dateValidator.validate(null, "18", CARD_CONFIG_NO_BRAND))
    }

    @Test
    fun `given current year only then should be completely valid`() {
        val dateValidator = getValidatorWithDate(2020, 5, 7)

        assertEquals(
            ValidationResult(
                partial = false,
                complete = true
            ), dateValidator.validate(null, "20", CARD_CONFIG_NO_BRAND))
    }

    @Test
    fun `given future year only then should be completely valid`() {
        val dateValidator = getValidatorWithDate(2019, 6, 6)

        assertEquals(
            ValidationResult(
                partial = false,
                complete = true
            ), dateValidator.validate(null, "20", CARD_CONFIG_NO_BRAND))
    }

    @Test
    fun `given empty configuration then dates can be updated`() {
        assertTrue(dateValidator.canUpdate("", "", CARD_CONFIG_NO_BRAND))
        assertTrue(dateValidator.canUpdate("1", "1", CARD_CONFIG_NO_BRAND))
        assertTrue(dateValidator.canUpdate("A", "A", CARD_CONFIG_NO_BRAND))
    }

    @Test
    fun `given empty year rule then dates can be updated`() {
        assertTrue(dateValidator.canUpdate("", "", CARD_CONFIG_NO_BRAND))
        assertTrue(dateValidator.canUpdate("1", "1", CARD_CONFIG_NO_BRAND))
        assertTrue(dateValidator.canUpdate("A", "A", CARD_CONFIG_NO_BRAND))
    }

    @Test
    fun `given empty month rule then dates can be updated`() {
        assertTrue(dateValidator.canUpdate("", "", CARD_CONFIG_NO_BRAND))
        assertTrue(dateValidator.canUpdate("1", "1", CARD_CONFIG_NO_BRAND))
        assertTrue(dateValidator.canUpdate("A", "A", CARD_CONFIG_NO_BRAND))
    }

    @Test
    fun `given empty year then can be updated`() {
        assertTrue(dateValidator.canUpdate(null, "", CARD_CONFIG_NO_BRAND))
    }

    @Test
    fun `given single digit year then then can be updated`() {
        assertTrue(dateValidator.canUpdate(null, "2", CARD_CONFIG_NO_BRAND))
    }

    @Test
    fun `given two digit year then can be updated`() {
        assertTrue(dateValidator.canUpdate(null, "20", CARD_CONFIG_NO_BRAND))
    }

    @Test
    fun `given three digit year then can be updated`() {
        assertTrue(dateValidator.canUpdate(null, "201", CARD_CONFIG_NO_BRAND))
    }

    @Test
    fun `given empty month then then can be updated`() {
        assertTrue(dateValidator.canUpdate("", null, CARD_CONFIG_NO_BRAND))
    }

    @Test
    fun `given single digit month then can be updated`() {
        assertTrue(dateValidator.canUpdate("1", null, CARD_CONFIG_NO_BRAND))
    }

    @Test
    fun `given two digit month then can be updated`() {
        assertTrue(dateValidator.canUpdate("12", null, CARD_CONFIG_NO_BRAND))
    }

    @Test
    fun `given three digit month then can be updated`() {
        assertTrue(dateValidator.canUpdate("123", null, CARD_CONFIG_NO_BRAND))
    }

    @Test
    fun `given empty month and year then dates can be updated`() {
        assertTrue(dateValidator.canUpdate("", "", CARD_CONFIG_NO_BRAND))
    }

    @Test
    fun `given single digit month and empty year then dates can be updated`() {
        assertTrue(dateValidator.canUpdate("1", "", CARD_CONFIG_NO_BRAND))
    }

    @Test
    fun `given two digit month and empty year then can be updated`() {
        assertTrue(dateValidator.canUpdate("12", "", CARD_CONFIG_NO_BRAND))
    }

    @Test
    fun `given two digit month and single digit year then can be updated`() {
        assertTrue(dateValidator.canUpdate("12", "2", CARD_CONFIG_NO_BRAND))
    }

    @Test
    fun `given two digit month and two digit year then cannot be updated`() {
        assertFalse(dateValidator.canUpdate("12", "12", CARD_CONFIG_NO_BRAND))
    }

    @Test
    fun `given three digit month and two digit year then can be updated`() {
        assertTrue(dateValidator.canUpdate("123", "12", CARD_CONFIG_NO_BRAND))
    }

    @Test
    fun `given three digit month and three digit year then can be updated`() {
        assertTrue(dateValidator.canUpdate("123", "123", CARD_CONFIG_NO_BRAND))
    }

    @Test
    fun `should return default month and year rule when retrieving the validation rule`() {
        assertEquals(Pair(EXP_MONTH_RULE, EXP_YEAR_RULE), dateValidator.getValidationRule(CARD_CONFIG_BASIC))
        assertEquals(Pair(EXP_MONTH_RULE, EXP_YEAR_RULE), dateValidator.getValidationRule(CARD_CONFIG_NO_BRAND))
    }

    private fun getValidatorWithDate(year: Int, month: Int, day: Int): DateValidator {
        val now = Calendar.getInstance()
        now.set(Calendar.YEAR, year)
        now.set(Calendar.MONTH, month)
        now.set(Calendar.DAY_OF_MONTH, day)

        return DateValidator(
            now
        )
    }
}