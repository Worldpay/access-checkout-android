package com.worldpay.access.checkout.views

import android.text.SpannableStringBuilder
import com.nhaarman.mockitokotlin2.mock
import com.worldpay.access.checkout.api.configuration.CardConfiguration
import com.worldpay.access.checkout.api.configuration.CardValidationRule
import com.worldpay.access.checkout.api.configuration.RemoteCardBrand
import com.worldpay.access.checkout.testutils.CardConfigurationUtil.Brands.VISA_BRAND
import com.worldpay.access.checkout.testutils.CardConfigurationUtil.Defaults.CARD_DEFAULTS
import com.worldpay.access.checkout.testutils.CardConfigurationUtil.Defaults.CVV_RULE
import com.worldpay.access.checkout.validation.CardValidator
import com.worldpay.access.checkout.validation.ValidationResult
import org.junit.Assert.assertEquals
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
class PANLengthFilterTest {

    private val cardValidator = Mockito.mock(CardValidator::class.java)
    private val cardConfiguration = Mockito.mock(CardConfiguration::class.java)
    private lateinit var panLengthFilter: PANLengthFilter

    @Before
    fun setup() {
        given(cardValidator.cardConfiguration).willReturn(cardConfiguration)
        panLengthFilter = PANLengthFilter(cardValidator)
    }

    @Test
    fun givenAnIdentifiedCardAndMaximumCardLengthIsEnteredThenShouldNotFilterBasedOnInput() {
        val fifteenDigitCardInput = "400000000000000"

        val validationResult = ValidationResult(partial = true, complete = false)
        given(cardValidator.validatePAN(fifteenDigitCardInput)).willReturn(Pair(validationResult, VISA_BRAND))

        // Add a '0' to existing string
        val filteredString = panLengthFilter.filter("0", 0, 1, SpannableStringBuilder(fifteenDigitCardInput), fifteenDigitCardInput.length, fifteenDigitCardInput.length)

        // Verify that no filtering has been done
        assertNull(filteredString)
    }

    @Test
    fun givenAnIdentifiedCardWithNoValidationRulesAndMaximumCardLengthIsEnteredThenShouldNotFilterBasedOnInput() {
        val fifteenDigitCardInput = "400000000000000"

        val validationResult = ValidationResult(partial = true, complete = false)
        given(cardConfiguration.defaults).willReturn(CARD_DEFAULTS)
        given(cardValidator.validatePAN(fifteenDigitCardInput)).willReturn(Pair(validationResult, VISA_BRAND))

        // Add a '0' to existing string
        val filteredString = panLengthFilter.filter("0", 0, 1, SpannableStringBuilder(fifteenDigitCardInput), fifteenDigitCardInput.length, fifteenDigitCardInput.length)

        // Verify that no filtering has been done
        assertNull(filteredString)
    }

    @Test
    fun givenANonIdentifiedCardAndMaximumCardLengthIsEnteredBasedOnDefaultsThenShouldNotFilterBasedOnInput() {
        val fifteenDigitCardInput = "400000000000000"

        val validationResult = ValidationResult(partial = true, complete = false)
        given(cardConfiguration.defaults).willReturn(CARD_DEFAULTS)
        given(cardValidator.validatePAN(fifteenDigitCardInput)).willReturn(Pair(validationResult, null))

        // Add a '0' to existing string
        val filteredString = panLengthFilter.filter("0", 0, 1, SpannableStringBuilder(fifteenDigitCardInput), fifteenDigitCardInput.length, fifteenDigitCardInput.length)

        // Verify that no filtering has been done
        assertNull(filteredString)
    }

    @Test
    fun givenAnIdentifiedCardAndMaximumCardLengthIsExceededThenShouldFilterBasedOnInput() {
        val sixteenDigitCardInput = "4000000000000000"

        val cardValidationRule =
            CardValidationRule(
                "^40\\d{0,14}$",
                listOf(16)
            )
        val cardBrand = RemoteCardBrand(
            "visa",
            emptyList(),
            CVV_RULE,
            cardValidationRule
        )
        val validationResult = ValidationResult(partial = false, complete = true)
        given(cardValidator.validatePAN(sixteenDigitCardInput)).willReturn(Pair(validationResult, cardBrand))

        // Add a '0' to existing string
        val filteredString = panLengthFilter.filter("0", 0, 1, SpannableStringBuilder(sixteenDigitCardInput), sixteenDigitCardInput.length, sixteenDigitCardInput.length)

        // Verify that no character is added
        assertEquals("", filteredString)
    }

    @Test
    fun givenAnIdentifiedCardWithNoValidationRulesAndMaximumCardLengthIsExceededBasedOnDefaultsThenShouldFilterBasedOnInput() {
        val nineteenDigitCardInput = "4000000000000000000"

        val validationResult = ValidationResult(partial = true, complete = false)
        given(cardConfiguration.defaults).willReturn(CARD_DEFAULTS)
        given(cardValidator.validatePAN(nineteenDigitCardInput)).willReturn(Pair(validationResult, VISA_BRAND))

        // Add a '0' to existing string
        val filteredString = panLengthFilter.filter("0", 0, 1, SpannableStringBuilder(nineteenDigitCardInput), nineteenDigitCardInput.length, nineteenDigitCardInput.length)

        // Verify that no character is added
        assertEquals("", filteredString)
    }

    @Test
    fun givenAnIdentifiedCardWithAValidationRuleWithNoLimitsThenShouldNotFilter() {
        val nineteenDigitCardInput = "4000000000000000000"

        val cardBrand = RemoteCardBrand(
            name = "visa",
            images = emptyList(),
            cvv = CVV_RULE,
            pan = CardValidationRule("^4\\d{0,19}$", emptyList())
        )
        val validationResult = ValidationResult(partial = true, complete = false)
        given(cardConfiguration.defaults).willReturn(CARD_DEFAULTS)
        given(cardValidator.validatePAN(nineteenDigitCardInput)).willReturn(Pair(validationResult, cardBrand))

        // Add a '0' to existing string
        val filteredString = panLengthFilter.filter("0", 0, 1, SpannableStringBuilder(nineteenDigitCardInput), nineteenDigitCardInput.length, nineteenDigitCardInput.length)

        // Verify that no character is added
        assertNull(filteredString)
    }

    @Test
    fun givenANonIdentifiedCardAndMaximumCardLengthIsExceededBasedOnDefaultsThenShouldFilterBasedOnInput() {
        val nineteenDigitCardInput = "4000000000000000000"

        val validationResult = ValidationResult(partial = true, complete = false)
        given(cardConfiguration.defaults).willReturn(CARD_DEFAULTS)
        given(cardValidator.validatePAN(nineteenDigitCardInput)).willReturn(Pair(validationResult, null))

        // Add a '0' to existing string
        val filteredString = panLengthFilter.filter("0", 0, 1, SpannableStringBuilder(nineteenDigitCardInput), nineteenDigitCardInput.length, nineteenDigitCardInput.length)

        // Verify that no character is added
        assertEquals("", filteredString)
    }

    @Test
    fun givenAnIdentifiedCardAndInputDoesNotExceedMaximumThenShouldNotFilter() {
        val fourteenDigitCardInput = "40000000000000"

        val validationResult = ValidationResult(partial = true, complete = false)
        given(cardValidator.validatePAN(fourteenDigitCardInput)).willReturn(Pair(validationResult, VISA_BRAND))

        // Add a '0' to existing string
        val filteredString = panLengthFilter.filter("0", 0, 1, SpannableStringBuilder(fourteenDigitCardInput), fourteenDigitCardInput.length, fourteenDigitCardInput.length)

        // Verify that no filtering has been done
        assertNull(filteredString)
    }

    @Test
    fun givenAnIdentifiedCardWithNoValidationRulesAndInputDoesNotExceedMaximumBasedOnDefaultsThenShouldNotFilter() {
        val fourteenDigitCardInput = "40000000000000"

        val validationResult = ValidationResult(partial = true, complete = false)

        given(cardConfiguration.defaults).willReturn(CARD_DEFAULTS)
        given(cardValidator.validatePAN(fourteenDigitCardInput)).willReturn(Pair(validationResult, VISA_BRAND))

        // Add a '0' to existing string
        val filteredString = panLengthFilter.filter("0", 0, 1, SpannableStringBuilder(fourteenDigitCardInput), fourteenDigitCardInput.length, fourteenDigitCardInput.length)

        // Verify that no filtering has been done
        assertNull(filteredString)
    }

    @Test
    fun givenANonIdentifiedCardAndInputDoesNotExceedMaximumBasedOnDefaultsThenShouldNotFilter() {
        val fourteenDigitCardInput = "40000000000000"

        val validationResult = ValidationResult(partial = true, complete = false)
        given(cardConfiguration.defaults).willReturn(CARD_DEFAULTS)
        given(cardValidator.validatePAN(fourteenDigitCardInput)).willReturn(Pair(validationResult, null))

        // Add a '0' to existing string
        val filteredString = panLengthFilter.filter("0", 0, 1, SpannableStringBuilder(fourteenDigitCardInput), fourteenDigitCardInput.length, fourteenDigitCardInput.length)

        // Verify that no filtering has been done
        assertNull(filteredString)
    }

    @Test
    fun givenAnIdentifiedCardAndInputDoesNotExceedMaximumThenShouldNotFilterAndUseCachedLengthInputToValidate() {
        val fourteenDigitCardInput = "40000000000000"

        val validationResult = ValidationResult(partial = true, complete = false)
        given(cardValidator.validatePAN(fourteenDigitCardInput)).willReturn(Pair(validationResult, VISA_BRAND))

        // Add a '0' to existing string
        assertEquals(0, panLengthFilter.lengthFiltersBySizeCache.size)
        val filteredString1 = panLengthFilter.filter("0", 0, 1, SpannableStringBuilder(fourteenDigitCardInput), fourteenDigitCardInput.length, fourteenDigitCardInput.length)
        assertEquals(1, panLengthFilter.lengthFiltersBySizeCache.size)
        val filteredString2 = panLengthFilter.filter("0", 0, 1, SpannableStringBuilder(fourteenDigitCardInput), fourteenDigitCardInput.length, fourteenDigitCardInput.length)
        assertEquals(1, panLengthFilter.lengthFiltersBySizeCache.size)

        // Verify that no filtering has been done
        assertNull(filteredString1)
        assertNull(filteredString2)
    }

    @Test
    fun givenNoInputThenShouldNotFilter() {
        assertNull(panLengthFilter.filter("0", 0, 1, null, 0, 0))
    }

    @Test
    fun `should get brand pan rule if one is found`() {
        assertEquals(VISA_BRAND.pan, panLengthFilter.getMaxLengthRule(VISA_BRAND, CARD_DEFAULTS))
    }

    @Test
    fun `should get default pan rule if brand is null`() {
        assertEquals(CARD_DEFAULTS.pan, panLengthFilter.getMaxLengthRule(null, CARD_DEFAULTS))
    }

    @Test
    fun `should still be able to get card defaults if card configuration is not provided`() {
        val cardValidator: CardValidator = mock()
        val panLengthFilter = PANLengthFilter(cardValidator)

        val fifteenDigitCardInput = "400000000000000"

        val validationResult = ValidationResult(partial = true, complete = false)
        given(cardValidator.cardConfiguration).willReturn(null)
        given(cardValidator.validatePAN(fifteenDigitCardInput)).willReturn(Pair(validationResult, null))

        // Add a '0' to existing string
        val filteredString = panLengthFilter.filter("0", 0, 1, SpannableStringBuilder(fifteenDigitCardInput), fifteenDigitCardInput.length, fifteenDigitCardInput.length)

        // Verify that no filtering has been done
        assertNull(filteredString)
    }

}
