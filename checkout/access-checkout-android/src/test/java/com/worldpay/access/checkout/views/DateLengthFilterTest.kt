package com.worldpay.access.checkout.views

import android.text.SpannableStringBuilder
import com.worldpay.access.checkout.model.CardBrand
import com.worldpay.access.checkout.model.CardConfiguration
import com.worldpay.access.checkout.model.CardDefaults
import com.worldpay.access.checkout.model.CardValidationRule
import org.junit.Assert
import org.junit.Assert.assertNull
import org.junit.Before
import org.junit.Test
import org.junit.runner.RunWith
import org.mockito.BDDMockito.given
import org.mockito.Mockito
import org.robolectric.RobolectricTestRunner
import org.robolectric.annotation.Config

@RunWith(RobolectricTestRunner::class)
@Config(manifest = Config.NONE)
class DateLengthFilterTest {

    private val cardConfiguration = Mockito.mock(CardConfiguration::class.java)
    private lateinit var dateLengthFilter: DateLengthFilter

    @Before
    fun setup() {
        val monthValidationRule = CardValidationRule(null, listOf(2))
        val yearValidationRule = CardValidationRule(null, listOf(2))
        given(cardConfiguration.defaults).willReturn(CardDefaults(null, null, monthValidationRule, yearValidationRule))
        dateLengthFilter = DateLengthFilter(cardConfiguration)
    }

    @Test
    fun givenMaximumDateFieldLengthIsEnteredThenShouldNotFilterBasedOnInput() {
        val oneDigit = "0"

        // Add a '1' to existing string
        val filteredString = dateLengthFilter.filter("1", 0, 1, SpannableStringBuilder(oneDigit), oneDigit.length, oneDigit.length)

        // Verify that no filtering has been done
        assertNull(filteredString)
    }

    @Test
    fun givenMaximumDateFieldLengthIsExceededThenShouldFilterBasedOnInput() {
        val twoDigits = "01"

        // Add a '1' to existing string
        val filteredString = dateLengthFilter.filter("1", 0, 1, SpannableStringBuilder(twoDigits), twoDigits.length, twoDigits.length)

        // Verify that no character is added
        Assert.assertEquals("", filteredString)
    }

    @Test
    fun givenNoDefaultConfigurationThenShouldNotFilter() {
        val dateLengthFilter = DateLengthFilter(CardConfiguration())

        assertNull(dateLengthFilter.filter("0", 0, 1, SpannableStringBuilder("01"), 2, 2))
    }

    @Test
    fun givenNoDateFieldConfigurationThenShouldNotFilter() {
        val dateLengthFilter = DateLengthFilter(CardConfiguration(defaults = CardDefaults(null, null, null, null)))

        assertNull(dateLengthFilter.filter("0", 0, 1, SpannableStringBuilder("01"), 2, 2))
    }

    @Test
    fun givenNoValidLengthForDateFieldRulesThenShouldNotFilter() {
        val dateLengthFilter = DateLengthFilter(CardConfiguration(defaults = CardDefaults(null, null, CardValidationRule(null, null), CardValidationRule(null, null))))

        assertNull(dateLengthFilter.filter("0", 0, 1, SpannableStringBuilder("01"), 2, 2))
    }

    @Test
    fun givenInputDoesNotExceedMaximumThenShouldNotFilterAndUseCachedLengthInputToValidate() {
        val oneDigit = "0"

        // Add a '1' to existing string
        Assert.assertEquals(0, dateLengthFilter.lengthFiltersBySizeCache.size)
        val filteredString1 = dateLengthFilter.filter("1", 0, 1, SpannableStringBuilder(oneDigit), oneDigit.length, oneDigit.length)
        Assert.assertEquals(1, dateLengthFilter.lengthFiltersBySizeCache.size)
        val filteredString2 = dateLengthFilter.filter("1", 0, 1, SpannableStringBuilder(oneDigit), oneDigit.length, oneDigit.length)
        Assert.assertEquals(1, dateLengthFilter.lengthFiltersBySizeCache.size)

        // Verify that no filtering has been done
        assertNull(filteredString1)
        assertNull(filteredString2)
    }

    @Test
    fun givenEmptyMonthRuleThenShouldUseYearRuleForFiltering() {
        val yearValidationRule = CardValidationRule(null, listOf(2))
        given(cardConfiguration.defaults).willReturn(CardDefaults(null, null, null, yearValidationRule))

        val oneDigit = "0"

        // Add a '1' to existing string
        val filteredString = dateLengthFilter.filter("1", 0, 1, SpannableStringBuilder(oneDigit), oneDigit.length, oneDigit.length)

        // Verify that no filtering has been done
        assertNull(filteredString)
    }

    @Test
    fun givenNoInputThenShouldNotFilter() {
        assertNull(dateLengthFilter.filter("0", 0, 1, null, 0, 0))
    }

    @Test
    fun shouldNotSelectRuleFromCardBrand() {
        assertNull(dateLengthFilter.ruleSelectorForCardBrand(CardBrand("test", emptyList(), null, null), SpannableStringBuilder("01")))
    }
}