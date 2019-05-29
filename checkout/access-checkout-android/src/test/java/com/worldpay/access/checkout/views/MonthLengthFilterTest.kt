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
class MonthLengthFilterTest {

    private val cardConfiguration = Mockito.mock(CardConfiguration::class.java)
    private lateinit var monthLengthFilter: MonthLengthFilter

    @Before
    fun setup() {
        val monthValidationRule = CardValidationRule(null, null, null, 2)
        given(cardConfiguration.defaults).willReturn(CardDefaults(null, null, monthValidationRule, null))
        monthLengthFilter = MonthLengthFilter(cardConfiguration)
    }

    @Test
    fun givenMaximumMonthLengthIsEnteredThenShouldNotFilterBasedOnInput() {
        val oneDigitMonth = "0"

        // Add a '1' to existing string
        val filteredString = monthLengthFilter.filter("1", 0, 1, SpannableStringBuilder(oneDigitMonth), oneDigitMonth.length, oneDigitMonth.length)

        // Verify that no filtering has been done
        assertNull(filteredString)
    }

    @Test
    fun givenMaximumMonthLengthIsExceededThenShouldFilterBasedOnInput() {
        val twoDigitMonth = "01"

        // Add a '1' to existing string
        val filteredString = monthLengthFilter.filter("1", 0, 1, SpannableStringBuilder(twoDigitMonth), twoDigitMonth.length, twoDigitMonth.length)

        // Verify that no character is added
        Assert.assertEquals("", filteredString)
    }

    @Test
    fun givenNoDefaultConfigurationThenShouldNotFilter() {
        val monthLengthFilter = MonthLengthFilter(CardConfiguration())

        assertNull(monthLengthFilter.filter("0", 0, 1, SpannableStringBuilder("01"), 2, 2))
    }

    @Test
    fun givenNoMonthConfigurationThenShouldNotFilter() {
        val monthLengthFilter = MonthLengthFilter(CardConfiguration(defaults = CardDefaults(null, null, null, null)))

        assertNull(monthLengthFilter.filter("0", 0, 1, SpannableStringBuilder("01"), 2, 2))
    }

    @Test
    fun givenNoValidLengthForMonthRuleThenShouldNotFilter() {
        val monthLengthFilter = MonthLengthFilter(CardConfiguration(defaults = CardDefaults(null, null, CardValidationRule(null, null, null, null), null)))

        assertNull(monthLengthFilter.filter("0", 0, 1, SpannableStringBuilder("01"), 2, 2))
    }

    @Test
    fun givenInputDoesNotExceedMaximumThenShouldNotFilterAndUseCachedLengthInputToValidate() {
        val oneDigitMonth = "0"

        // Add a '1' to existing string
        Assert.assertEquals(0, monthLengthFilter.lengthFiltersBySizeCache.size)
        val filteredString1 = monthLengthFilter.filter("1", 0, 1, SpannableStringBuilder(oneDigitMonth), oneDigitMonth.length, oneDigitMonth.length)
        Assert.assertEquals(1, monthLengthFilter.lengthFiltersBySizeCache.size)
        val filteredString2 = monthLengthFilter.filter("1", 0, 1, SpannableStringBuilder(oneDigitMonth), oneDigitMonth.length, oneDigitMonth.length)
        Assert.assertEquals(1, monthLengthFilter.lengthFiltersBySizeCache.size)

        // Verify that no filtering has been done
        assertNull(filteredString1)
        assertNull(filteredString2)
    }

    @Test
    fun givenNoInputThenShouldNotFilter() {
        assertNull(monthLengthFilter.filter("0", 0, 1, null, 0, 0))
    }

    @Test
    fun shouldNotSelectRuleFromCardBrand() {
        assertNull(monthLengthFilter.ruleSelectorForCardBrand(CardBrand("test", "test", null, emptyList()), SpannableStringBuilder("01")))
    }
}