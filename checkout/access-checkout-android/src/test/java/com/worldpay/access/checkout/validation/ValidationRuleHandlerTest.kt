package com.worldpay.access.checkout.validation

import android.text.InputFilter
import android.widget.EditText
import com.nhaarman.mockitokotlin2.given
import com.nhaarman.mockitokotlin2.mock
import com.worldpay.access.checkout.api.configuration.CardValidationRule
import com.worldpay.access.checkout.testutils.CardConfigurationUtil.Brands.VISA_BRAND
import com.worldpay.access.checkout.testutils.CardConfigurationUtil.Defaults.CVV_RULE
import com.worldpay.access.checkout.testutils.CardConfigurationUtil.Defaults.EXP_MONTH_RULE
import com.worldpay.access.checkout.testutils.CardConfigurationUtil.Defaults.EXP_YEAR_RULE
import com.worldpay.access.checkout.testutils.CardConfigurationUtil.Defaults.PAN_RULE
import com.worldpay.access.checkout.validation.card.CardDetailComponents
import com.worldpay.access.checkout.validation.card.CardDetailType.*
import com.worldpay.access.checkout.validation.card.CardDetailType.CVV
import org.junit.Before
import org.junit.Test
import org.junit.runner.RunWith
import org.robolectric.RobolectricTestRunner
import org.robolectric.shadows.ShadowInstrumentation
import kotlin.test.assertEquals
import kotlin.test.assertTrue

@RunWith(RobolectricTestRunner::class)
class ValidationRuleHandlerTest {

    private val context = ShadowInstrumentation.getInstrumentation().context
    private val pan = EditText(context)
    private val expiryMonth = EditText(context)
    private val expiryYear = EditText(context)
    private val cvv = EditText(context)

    private lateinit var validationRuleHandler: ValidationRuleHandler

    @Before
    fun setup() {
        val cardDetailComponents = CardDetailComponents(
            pan = pan,
            expiryMonth = expiryMonth,
            expiryYear = expiryYear,
            cvv = cvv
        )

        validationRuleHandler = ValidationRuleHandler(cardDetailComponents)

        assertEquals(0, pan.filters.size)
        assertEquals(0, expiryMonth.filters.size)
        assertEquals(0, expiryYear.filters.size)
        assertEquals(0, cvv.filters.size)
    }

    @Test
    fun `should set the input filter on the pan with branded rule`() {
        validationRuleHandler.handle(PAN, VISA_BRAND.pan)

        assertEquals(1, pan.filters.size)
        assertTrue(pan.filters[0] is InputFilter.LengthFilter)
        val filter = pan.filters[0] as InputFilter.LengthFilter
        assertEquals(19, filter.max)
    }

    @Test
    fun `should set the input filter on the pan with default rule`() {
        validationRuleHandler.handle(PAN, PAN_RULE)

        assertEquals(1, pan.filters.size)
        assertTrue(pan.filters[0] is InputFilter.LengthFilter)
        val filter = pan.filters[0] as InputFilter.LengthFilter
        assertEquals(19, filter.max)
    }

    @Test
    fun `should set the input filter on the expiry month`() {
        validationRuleHandler.handle(EXPIRY_MONTH, EXP_MONTH_RULE)

        assertEquals(1, expiryMonth.filters.size)
        assertTrue(expiryMonth.filters[0] is InputFilter.LengthFilter)
        val filter = expiryMonth.filters[0] as InputFilter.LengthFilter
        assertEquals(2, filter.max)
    }

    @Test
    fun `should set the input filter on the expiry year`() {
        validationRuleHandler.handle(EXPIRY_YEAR, EXP_YEAR_RULE)

        assertEquals(1, expiryYear.filters.size)
        assertTrue(expiryYear.filters[0] is InputFilter.LengthFilter)
        val filter = expiryYear.filters[0] as InputFilter.LengthFilter
        assertEquals(2, filter.max)
    }

    @Test
    fun `should set the input filter on the cvv with branded rule`() {
        validationRuleHandler.handle(CVV, VISA_BRAND.cvv)

        assertEquals(1, cvv.filters.size)
        assertTrue(cvv.filters[0] is InputFilter.LengthFilter)
        val filter = cvv.filters[0] as InputFilter.LengthFilter
        assertEquals(3, filter.max)
    }

    @Test
    fun `should set the input filter on the cvv with default rule`() {
        validationRuleHandler.handle(CVV, CVV_RULE)

        assertEquals(1, cvv.filters.size)
        assertTrue(cvv.filters[0] is InputFilter.LengthFilter)
        val filter = cvv.filters[0] as InputFilter.LengthFilter
        assertEquals(4, filter.max)
    }

    @Test
    fun `should use default max length where max length cannot be determined from rule`() {
        val cardValidationRule = mock<CardValidationRule>()

        given(cardValidationRule.validLengths).willReturn(emptyList())

        validationRuleHandler.handle(PAN, cardValidationRule)

        assertEquals(1, pan.filters.size)
        assertTrue(pan.filters[0] is InputFilter.LengthFilter)
        val filter = pan.filters[0] as InputFilter.LengthFilter
        assertEquals(100, filter.max)
    }

}