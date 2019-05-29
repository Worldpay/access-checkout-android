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
class YearLengthFilterTest {

    private val cardConfiguration = Mockito.mock(CardConfiguration::class.java)
    private lateinit var yearLengthFilter: YearLengthFilter

    @Before
    fun setup() {
        val yearValidationRule = CardValidationRule(null, null, null, 2)
        given(cardConfiguration.defaults).willReturn(CardDefaults(null, null, null, yearValidationRule))
        yearLengthFilter = YearLengthFilter(cardConfiguration)
    }

    @Test
    fun givenMaximumYearLengthIsEnteredThenShouldNotFilterBasedOnInput() {
        val oneDigitYear = "0"

        // Add a '1' to existing string
        val filteredString = yearLengthFilter.filter("1", 0, 1, SpannableStringBuilder(oneDigitYear), oneDigitYear.length, oneDigitYear.length)

        // Verify that no filtering has been done
        assertNull(filteredString)
    }

    @Test
    fun givenMaximumYearLengthIsExceededThenShouldFilterBasedOnInput() {
        val twoDigitYear = "01"

        // Add a '1' to existing string
        val filteredString = yearLengthFilter.filter("1", 0, 1, SpannableStringBuilder(twoDigitYear), twoDigitYear.length, twoDigitYear.length)

        // Verify that no character is added
        Assert.assertEquals("", filteredString)
    }

    @Test
    fun givenNoDefaultConfigurationThenShouldNotFilter() {
        val yearLengthFilter = YearLengthFilter(CardConfiguration())

        assertNull(yearLengthFilter.filter("0", 0, 1, SpannableStringBuilder("01"), 2, 2))
    }

    @Test
    fun givenNoYearConfigurationThenShouldNotFilter() {
        val yearLengthFilter = YearLengthFilter(CardConfiguration(defaults = CardDefaults(null, null, null, null)))

        assertNull(yearLengthFilter.filter("0", 0, 1, SpannableStringBuilder("01"), 2, 2))
    }

    @Test
    fun givenNoValidLengthForYearRuleThenShouldNotFilter() {
        val yearLengthFilter = YearLengthFilter(CardConfiguration(defaults = CardDefaults(null, null, CardValidationRule(null, null, null, null), null)))

        assertNull(yearLengthFilter.filter("0", 0, 1, SpannableStringBuilder("01"), 2, 2))
    }

    @Test
    fun givenInputDoesNotExceedMaximumThenShouldNotFilterAndUseCachedLengthInputToValidate() {
        val oneDigitYear = "0"

        // Add a '1' to existing string
        Assert.assertEquals(0, yearLengthFilter.lengthFiltersBySizeCache.size)
        val filteredString1 = yearLengthFilter.filter("1", 0, 1, SpannableStringBuilder(oneDigitYear), oneDigitYear.length, oneDigitYear.length)
        Assert.assertEquals(1, yearLengthFilter.lengthFiltersBySizeCache.size)
        val filteredString2 = yearLengthFilter.filter("1", 0, 1, SpannableStringBuilder(oneDigitYear), oneDigitYear.length, oneDigitYear.length)
        Assert.assertEquals(1, yearLengthFilter.lengthFiltersBySizeCache.size)

        // Verify that no filtering has been done
        assertNull(filteredString1)
        assertNull(filteredString2)
    }

    @Test
    fun givenNoInputThenShouldNotFilter() {
        assertNull(yearLengthFilter.filter("0", 0, 1, null, 0, 0))
    }

    @Test
    fun shouldNotSelectRuleFromCardBrand() {
        assertNull(yearLengthFilter.ruleSelectorForCardBrand(CardBrand("test", "test", null, emptyList()), SpannableStringBuilder("01")))
    }
}