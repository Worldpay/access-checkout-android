package com.worldpay.access.checkout.views

import android.text.SpannableStringBuilder
import com.worldpay.access.checkout.model.CardBrand
import com.worldpay.access.checkout.model.CardConfiguration
import com.worldpay.access.checkout.model.CardDefaults
import com.worldpay.access.checkout.model.CardValidationRule
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

        val cardValidationRule = CardValidationRule(null, listOf(16))
        val cardBrand = CardBrand("visa", emptyList(), null, cardValidationRule)
        val validationResult = ValidationResult(partial = true, complete = false)
        given(cardValidator.validatePAN(fifteenDigitCardInput)).willReturn(Pair(validationResult, cardBrand))

        // Add a '0' to existing string
        val filteredString = panLengthFilter.filter("0", 0, 1, SpannableStringBuilder(fifteenDigitCardInput), fifteenDigitCardInput.length, fifteenDigitCardInput.length)

        // Verify that no filtering has been done
        assertNull(filteredString)
    }

    @Test
    fun givenAnIdentifiedCardWithNoValidationRulesAndMaximumCardLengthIsEnteredThenShouldNotFilterBasedOnInput() {
        val fifteenDigitCardInput = "400000000000000"

        val defaultValidationRule = CardValidationRule(null, listOf(16))
        val cardBrand = CardBrand("visa", emptyList(), null, null)
        val validationResult = ValidationResult(partial = true, complete = false)
        given(cardConfiguration.defaults).willReturn(CardDefaults(defaultValidationRule, null, null, null))
        given(cardValidator.validatePAN(fifteenDigitCardInput)).willReturn(Pair(validationResult, cardBrand))

        // Add a '0' to existing string
        val filteredString = panLengthFilter.filter("0", 0, 1, SpannableStringBuilder(fifteenDigitCardInput), fifteenDigitCardInput.length, fifteenDigitCardInput.length)

        // Verify that no filtering has been done
        assertNull(filteredString)
    }

    @Test
    fun givenANonIdentifiedCardAndMaximumCardLengthIsEnteredBasedOnDefaultsThenShouldNotFilterBasedOnInput() {
        val fifteenDigitCardInput = "400000000000000"

        val defaultValidationRule = CardValidationRule(null, listOf(16))
        val validationResult = ValidationResult(partial = true, complete = false)
        given(cardConfiguration.defaults).willReturn(CardDefaults(defaultValidationRule, null, null, null))
        given(cardValidator.validatePAN(fifteenDigitCardInput)).willReturn(Pair(validationResult, null))

        // Add a '0' to existing string
        val filteredString = panLengthFilter.filter("0", 0, 1, SpannableStringBuilder(fifteenDigitCardInput), fifteenDigitCardInput.length, fifteenDigitCardInput.length)

        // Verify that no filtering has been done
        assertNull(filteredString)
    }

    @Test
    fun givenAnIdentifiedCardAndMaximumCardLengthIsExceededThenShouldFilterBasedOnInput() {
        val sixteenDigitCardInput = "4000000000000000"

        val cardValidationRule = CardValidationRule("^40\\d{0,14}$", listOf(16))
        val cardBrand = CardBrand("visa", emptyList(), null, cardValidationRule)
        val validationResult = ValidationResult(partial = false, complete = true)
        given(cardValidator.validatePAN(sixteenDigitCardInput)).willReturn(Pair(validationResult, cardBrand))

        // Add a '0' to existing string
        val filteredString = panLengthFilter.filter("0", 0, 1, SpannableStringBuilder(sixteenDigitCardInput), sixteenDigitCardInput.length, sixteenDigitCardInput.length)

        // Verify that no character is added
        assertEquals("", filteredString)
    }

    @Test
    fun givenAnIdentifiedCardWithNoValidationRulesAndMaximumCardLengthIsExceededBasedOnDefaultsThenShouldFilterBasedOnInput() {
        val sixteenDigitCardInput = "4000000000000000"

        val cardBrand = CardBrand("visa", emptyList(), null, null)
        val defaultValidationRule = CardValidationRule(null, listOf(16))
        val validationResult = ValidationResult(partial = true, complete = false)
        given(cardConfiguration.defaults).willReturn(CardDefaults(defaultValidationRule, null, null, null))
        given(cardValidator.validatePAN(sixteenDigitCardInput)).willReturn(Pair(validationResult, cardBrand))

        // Add a '0' to existing string
        val filteredString = panLengthFilter.filter("0", 0, 1, SpannableStringBuilder(sixteenDigitCardInput), sixteenDigitCardInput.length, sixteenDigitCardInput.length)

        // Verify that no character is added
        assertEquals("", filteredString)
    }

    @Test
    fun givenAnIdentifiedCardWithAValidationRuleWithNoLimitsSetAndMaximumCardLengthIsExceededBasedOnDefaultsThenShouldFilterBasedOnInput() {
        val sixteenDigitCardInput = "4000000000000000"

        val cardValidationRule = CardValidationRule("^4\\d{0,19}$", emptyList())
        val cardBrand = CardBrand("visa", emptyList(), null, cardValidationRule)
        val defaultValidationRule = CardValidationRule(null, listOf(16))
        val validationResult = ValidationResult(partial = true, complete = false)
        given(cardConfiguration.defaults).willReturn(CardDefaults(defaultValidationRule, null, null, null))
        given(cardValidator.validatePAN(sixteenDigitCardInput)).willReturn(Pair(validationResult, cardBrand))

        // Add a '0' to existing string
        val filteredString = panLengthFilter.filter("0", 0, 1, SpannableStringBuilder(sixteenDigitCardInput), sixteenDigitCardInput.length, sixteenDigitCardInput.length)

        // Verify that no character is added
        assertEquals("", filteredString)
    }

    @Test
    fun givenANonIdentifiedCardAndMaximumCardLengthIsExceededBasedOnDefaultsThenShouldFilterBasedOnInput() {
        val sixteenDigitCardInput = "4000000000000000"

        val defaultValidationRule = CardValidationRule(null, listOf(16))
        val validationResult = ValidationResult(partial = true, complete = false)
        given(cardConfiguration.defaults).willReturn(CardDefaults(defaultValidationRule, null, null, null))
        given(cardValidator.validatePAN(sixteenDigitCardInput)).willReturn(Pair(validationResult, null))

        // Add a '0' to existing string
        val filteredString = panLengthFilter.filter("0", 0, 1, SpannableStringBuilder(sixteenDigitCardInput), sixteenDigitCardInput.length, sixteenDigitCardInput.length)

        // Verify that no character is added
        assertEquals("", filteredString)
    }

    @Test
    fun givenAnIdentifiedCardAndInputDoesNotExceedMaximumThenShouldNotFilter() {
        val fourteenDigitCardInput = "40000000000000"

        val cardValidationRule = CardValidationRule(null, listOf(16))
        val cardBrand = CardBrand("visa", emptyList(), null, cardValidationRule)
        val validationResult = ValidationResult(partial = true, complete = false)
        given(cardValidator.validatePAN(fourteenDigitCardInput)).willReturn(Pair(validationResult, cardBrand))

        // Add a '0' to existing string
        val filteredString = panLengthFilter.filter("0", 0, 1, SpannableStringBuilder(fourteenDigitCardInput), fourteenDigitCardInput.length, fourteenDigitCardInput.length)

        // Verify that no filtering has been done
        assertNull(filteredString)
    }

    @Test
    fun givenAnIdentifiedCardWithNoValidationRulesAndInputDoesNotExceedMaximumBasedOnDefaultsThenShouldNotFilter() {
        val fourteenDigitCardInput = "40000000000000"

        val defaultValidationRule = CardValidationRule(null, listOf(16))
        val validationResult = ValidationResult(partial = true, complete = false)
        val cardBrand = CardBrand("visa", emptyList(), null, null)
        given(cardConfiguration.defaults).willReturn(CardDefaults(defaultValidationRule, null, null, null))
        given(cardValidator.validatePAN(fourteenDigitCardInput)).willReturn(Pair(validationResult, cardBrand))

        // Add a '0' to existing string
        val filteredString = panLengthFilter.filter("0", 0, 1, SpannableStringBuilder(fourteenDigitCardInput), fourteenDigitCardInput.length, fourteenDigitCardInput.length)

        // Verify that no filtering has been done
        assertNull(filteredString)
    }

    @Test
    fun givenANonIdentifiedCardAndInputDoesNotExceedMaximumBasedOnDefaultsThenShouldNotFilter() {
        val fourteenDigitCardInput = "40000000000000"

        val defaultValidationRule = CardValidationRule(null, listOf(16))
        val validationResult = ValidationResult(partial = true, complete = false)
        given(cardConfiguration.defaults).willReturn(CardDefaults(defaultValidationRule, null, null, null))
        given(cardValidator.validatePAN(fourteenDigitCardInput)).willReturn(Pair(validationResult, null))

        // Add a '0' to existing string
        val filteredString = panLengthFilter.filter("0", 0, 1, SpannableStringBuilder(fourteenDigitCardInput), fourteenDigitCardInput.length, fourteenDigitCardInput.length)

        // Verify that no filtering has been done
        assertNull(filteredString)
    }

    @Test
    fun givenNonIdentifiedCardAndNoCardConfigurationThenShouldNotFilter() {
        given(cardValidator.cardConfiguration).willReturn(null)
        val panLengthFilter = PANLengthFilter(cardValidator)

        val thirtyDigitCardInput = "400000000000000000000000000000"

        val validationResult = ValidationResult(partial = true, complete = true)
        given(cardValidator.validatePAN(thirtyDigitCardInput)).willReturn(Pair(validationResult, null))

        // Add a '0' to existing string
        val filteredString = panLengthFilter.filter("0", 0, 1, SpannableStringBuilder(thirtyDigitCardInput), thirtyDigitCardInput.length, thirtyDigitCardInput.length)

        // Verify that no filtering has been done
        assertNull(filteredString)
    }

    @Test
    fun givenNonIdentifiedCardAndCardConfigurationWithNoDefaultPANRulesThenShouldNotFilter() {
        val thirtyDigitCardInput = "400000000000000000000000000000"

        val validationResult = ValidationResult(partial = true, complete = true)
        given(cardConfiguration.defaults).willReturn(CardDefaults(null, null, null, null))
        given(cardValidator.validatePAN(thirtyDigitCardInput)).willReturn(Pair(validationResult, null))

        // Add a '0' to existing string
        val filteredString = panLengthFilter.filter("0", 0, 1, SpannableStringBuilder(thirtyDigitCardInput), thirtyDigitCardInput.length, thirtyDigitCardInput.length)

        // Verify that no filtering has been done
        assertNull(filteredString)
    }

    @Test
    fun givenAnIdentifiedCardAndInputDoesNotExceedMaximumThenShouldNotFilterAndUseCachedLengthInputToValidate() {
        val fourteenDigitCardInput = "40000000000000"

        val cardValidationRule = CardValidationRule(null, listOf(16))
        val cardBrand = CardBrand("visa", emptyList(), null, cardValidationRule)
        val validationResult = ValidationResult(partial = true, complete = false)
        given(cardValidator.validatePAN(fourteenDigitCardInput)).willReturn(Pair(validationResult, cardBrand))

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
}