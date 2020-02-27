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
import org.mockito.Mockito.mock
import org.robolectric.RobolectricTestRunner
import org.robolectric.annotation.Config

@RunWith(RobolectricTestRunner::class)
@Config(manifest = Config.NONE)
class CVVLengthFilterTest {

    private val panView: CardTextView = mock(CardTextView::class.java)
    private val cardValidator: CardValidator = mock(CardValidator::class.java)
    private val cardConfiguration: CardConfiguration = mock(CardConfiguration::class.java)
    private val cardNumber = "444"

    private lateinit var cvvLengthFilter: CVVLengthFilter

    @Before
    fun setup() {
        given(cardValidator.cardConfiguration).willReturn(cardConfiguration)
        cvvLengthFilter = CVVLengthFilter(cardValidator, panView)

        given(panView.getInsertedText()).willReturn(cardNumber)
    }

    @Test
    fun givenAnIdentifiedCardAndMaximumCvvLengthIsEnteredThenShouldNotFilterBasedOnInput() {
        val currentCVV = "12"
        val cvvRule = CardValidationRule(null, null, null, 3)
        val cardBrand = CardBrand("visa", emptyList(), cvvRule, emptyList())
        given(cardValidator.validateCVV(currentCVV, cardNumber)).willReturn(Pair(mock(ValidationResult::class.java), cardBrand))

        val filtered = cvvLengthFilter.filter("3", 0, 1, SpannableStringBuilder(currentCVV), 2, 2)

        assertNull(filtered)
    }

    @Test
    fun givenAnIdentifiedCardAndCvvLengthIsStillUnderMaximumThenShouldNotFilter() {
        val currentCVV = "1"
        val cvvRule = CardValidationRule(null, null, null, 3)
        val cardBrand = CardBrand("visa", emptyList(), cvvRule, emptyList())
        given(cardValidator.validateCVV(currentCVV, cardNumber)).willReturn(Pair(mock(ValidationResult::class.java), cardBrand))

        val filtered = cvvLengthFilter.filter("2", 0, 1, SpannableStringBuilder(currentCVV), 1, 1)

        assertNull(filtered)
    }

    @Test
    fun givenAnIdentifiedCardAndMaximumCvvLengthIsExceededThenShouldFilterBasedOnInput() {
        val currentCVV = "123"
        val cvvRule = CardValidationRule(null, null, null, 3)
        val cardBrand = CardBrand("visa", emptyList(), cvvRule, emptyList())
        given(cardValidator.validateCVV(currentCVV, cardNumber)).willReturn(Pair(mock(ValidationResult::class.java), cardBrand))

        val filtered = cvvLengthFilter.filter("4", 0, 1, SpannableStringBuilder(currentCVV), 3, 3)

        assertEquals("", filtered)
    }

    @Test
    fun givenAnIdentifiedCardButNoCvvRuleAndMaximumCvvLengthIsEnteredForDefaultCVVRuleThenShouldNotFilterBasedOnInput() {
        val currentCVV = "12"
        val cardBrand = CardBrand("visa", emptyList(), null, emptyList())
        val cvvRule = CardValidationRule(null, null, 3, null)
        given(cardConfiguration.defaults).willReturn(CardDefaults(null, cvvRule, null, null))
        given(cardValidator.validateCVV(currentCVV, cardNumber)).willReturn(Pair(mock(ValidationResult::class.java), cardBrand))

        val filtered = cvvLengthFilter.filter("3", 0, 1, SpannableStringBuilder(currentCVV), 2, 2)

        assertNull(filtered)
    }

    @Test
    fun givenAnIdentifiedCardButNoCvvRuleAndCvvLengthIsStillUnderMaximumForDefaultCVVRuleThenShouldNotFilterBasedOnInput() {
        val currentCVV = "1"
        val cardBrand = CardBrand("visa", emptyList(), null, emptyList())
        val cvvRule = CardValidationRule(null, null, 3, null)
        given(cardConfiguration.defaults).willReturn(CardDefaults(null, cvvRule, null, null))
        given(cardValidator.validateCVV(currentCVV, cardNumber)).willReturn(Pair(mock(ValidationResult::class.java), cardBrand))

        val filtered = cvvLengthFilter.filter("2", 0, 1, SpannableStringBuilder(currentCVV), 1, 1)

        assertNull(filtered)
    }

    @Test
    fun givenAnIdentifiedCardButNoCvvRuleAndMaximumCvvLengthIsExceededForDefaultCVVRuleThenShouldNotFilterBasedOnInput() {
        val currentCVV = "123"
        val cardBrand = CardBrand("visa", emptyList(), null, emptyList())
        val cvvRule = CardValidationRule(null, null, 3, null)
        given(cardConfiguration.defaults).willReturn(CardDefaults(null, cvvRule, null, null))
        given(cardValidator.validateCVV(currentCVV, cardNumber)).willReturn(Pair(mock(ValidationResult::class.java), cardBrand))

        val filtered = cvvLengthFilter.filter("4", 0, 1, SpannableStringBuilder(currentCVV), 3, 3)

        assertEquals("", filtered)
    }

    @Test
    fun givenANonIdentifiedCardAndMaximumCvvLengthIsEnteredForDefaultCVVRuleThenShouldNotFilterBasedOnInput() {
        val currentCVV = "12"
        val cvvRule = CardValidationRule(null, null, 3, null)
        given(cardConfiguration.defaults).willReturn(CardDefaults(null, cvvRule, null, null))
        given(cardValidator.validateCVV(currentCVV, cardNumber)).willReturn(Pair(mock(ValidationResult::class.java), null))

        val filtered = cvvLengthFilter.filter("3", 0, 1, SpannableStringBuilder(currentCVV), 2, 2)

        assertNull(filtered)
    }

    @Test
    fun givenANullPanAndMaximumCvvLengthIsEnteredForDefaultCVVRuleThenShouldNotFilterBasedOnInput() {
        cvvLengthFilter = CVVLengthFilter(cardValidator, null)

        val currentCVV = "12"
        val cvvRule = CardValidationRule(null, null, 3, null)
        given(cardConfiguration.defaults).willReturn(CardDefaults(null, cvvRule, null, null))
        given(cardValidator.validateCVV(currentCVV, null)).willReturn(Pair(mock(ValidationResult::class.java), null))

        val filtered = cvvLengthFilter.filter("3", 0, 1, SpannableStringBuilder(currentCVV), 2, 2)

        assertNull(filtered)
    }

    @Test
    fun givenANonIdentifiedCardAndCvvLengthIsStillUnderMaximumForDefaultCVVRuleThenShouldNotFilter() {
        val currentCVV = "1"
        val cvvRule = CardValidationRule(null, null, 3, null)
        given(cardConfiguration.defaults).willReturn(CardDefaults(null, cvvRule, null, null))
        given(cardValidator.validateCVV(currentCVV, cardNumber)).willReturn(Pair(mock(ValidationResult::class.java), null))

        val filtered = cvvLengthFilter.filter("2", 0, 1, SpannableStringBuilder(currentCVV), 1, 1)

        assertNull(filtered)
    }

    @Test
    fun givenANullPanAndCvvLengthIsStillUnderMaximumForDefaultCVVRuleThenShouldNotFilter() {
        cvvLengthFilter = CVVLengthFilter(cardValidator, null)

        val currentCVV = "1"
        val cvvRule = CardValidationRule(null, null, 3, null)
        given(cardConfiguration.defaults).willReturn(CardDefaults(null, cvvRule, null, null))
        given(cardValidator.validateCVV(currentCVV, null)).willReturn(Pair(mock(ValidationResult::class.java), null))

        val filtered = cvvLengthFilter.filter("2", 0, 1, SpannableStringBuilder(currentCVV), 1, 1)

        assertNull(filtered)
    }

    @Test
    fun givenANonIdentifiedCardAndMaximumCvvLengthIsExceededForDefaultCVVRuleThenShouldFilterBasedOnInput() {
        val currentCVV = "123"
        val cvvRule = CardValidationRule(null, null, 3, null)
        given(cardConfiguration.defaults).willReturn(CardDefaults(null, cvvRule, null, null))
        given(cardValidator.validateCVV(currentCVV, cardNumber)).willReturn(Pair(mock(ValidationResult::class.java), null))

        val filtered = cvvLengthFilter.filter("4", 0, 1, SpannableStringBuilder(currentCVV), 3, 3)

        assertEquals("", filtered)
    }

    @Test
    fun givenNullPanIsPassedAndMaximumCvvLengthIsExceededForDefaultCVVRuleThenShouldFilterBasedOnInput() {
        cvvLengthFilter = CVVLengthFilter(cardValidator, null)

        val currentCVV = "123"
        val cvvRule = CardValidationRule(null, null, 3, null)
        given(cardConfiguration.defaults).willReturn(CardDefaults(null, cvvRule, null, null))
        given(cardValidator.validateCVV(currentCVV, null)).willReturn(Pair(mock(ValidationResult::class.java), null))

        val filtered = cvvLengthFilter.filter("4", 0, 1, SpannableStringBuilder(currentCVV), 3, 3)

        assertEquals("", filtered)
    }

    @Test
    fun givenANonIdentifiedCardAndNoDefaultConfigurationThenShouldNotFilterBasedOnInput() {
        val currentCVV = "123"
        given(cardValidator.validateCVV(currentCVV, cardNumber)).willReturn(Pair(mock(ValidationResult::class.java), null))

        val filtered = cvvLengthFilter.filter("4", 0, 1, SpannableStringBuilder(currentCVV), 3, 3)

        assertNull(filtered)
    }

    @Test
    fun givenANullPanAndNoDefaultConfigurationThenShouldNotFilterBasedOnInput() {
        cvvLengthFilter = CVVLengthFilter(cardValidator, null)

        val currentCVV = "123"
        given(cardValidator.validateCVV(currentCVV, null)).willReturn(Pair(mock(ValidationResult::class.java), null))

        val filtered = cvvLengthFilter.filter("4", 0, 1, SpannableStringBuilder(currentCVV), 3, 3)

        assertNull(filtered)
    }

    @Test
    fun givenNoInputThenShouldNotFilter() {
        assertNull(cvvLengthFilter.filter("0", 0, 1, null, 0, 0))
    }

    @Test
    fun givenAnIdentifiedCardAndInputDoesNotExceedMaximumCVVThenShouldNotFilterAndUseCachedLengthInputToValidate() {
        val currentCVV = "12"
        val cvvRule = CardValidationRule(null, null, null, 3)
        val cardBrand = CardBrand("visa", emptyList(), cvvRule, emptyList())
        given(cardValidator.validateCVV(currentCVV, cardNumber)).willReturn(Pair(mock(ValidationResult::class.java), cardBrand))

        // Add a '0' to existing string
        assertEquals(0, cvvLengthFilter.lengthFiltersBySizeCache.size)
        val filteredString1 = cvvLengthFilter.filter("0", 0, 1, SpannableStringBuilder(currentCVV), 2, 2)
        assertEquals(1, cvvLengthFilter.lengthFiltersBySizeCache.size)
        val filteredString2 = cvvLengthFilter.filter("0", 0, 1, SpannableStringBuilder(currentCVV), 2, 2)
        assertEquals(1, cvvLengthFilter.lengthFiltersBySizeCache.size)

        // Verify that no filtering has been done
        assertNull(filteredString1)
        assertNull(filteredString2)
    }


}