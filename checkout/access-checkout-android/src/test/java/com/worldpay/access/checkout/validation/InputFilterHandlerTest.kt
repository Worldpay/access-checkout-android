package com.worldpay.access.checkout.validation

import android.text.InputFilter
import android.widget.EditText
import com.nhaarman.mockitokotlin2.given
import com.nhaarman.mockitokotlin2.mock
import com.worldpay.access.checkout.api.configuration.CardValidationRule
import com.worldpay.access.checkout.testutils.CardConfigurationUtil.Brands.VISA_BRAND
import com.worldpay.access.checkout.testutils.CardConfigurationUtil.Defaults.PAN_RULE
import org.junit.Before
import org.junit.Test
import org.junit.runner.RunWith
import org.robolectric.RobolectricTestRunner
import org.robolectric.shadows.ShadowInstrumentation
import kotlin.test.assertEquals
import kotlin.test.assertTrue

@RunWith(RobolectricTestRunner::class)
class InputFilterHandlerTest {

    private val context = ShadowInstrumentation.getInstrumentation().context
    private val pan = EditText(context)
    private val expiryMonth = EditText(context)
    private val expiryYear = EditText(context)
    private val cvv = EditText(context)

    private lateinit var inputFilterHandler: InputFilterHandler

    @Before
    fun setup() {
        inputFilterHandler = InputFilterHandler()

        assertEquals(0, pan.filters.size)
        assertEquals(0, expiryMonth.filters.size)
        assertEquals(0, expiryYear.filters.size)
        assertEquals(0, cvv.filters.size)
    }

    @Test
    fun `should set max length on edit text for branded rule`() {
        inputFilterHandler.handle(pan, VISA_BRAND.pan)

        assertEquals(1, pan.filters.size)
        assertTrue(pan.filters[0] is InputFilter.LengthFilter)
        val filter = pan.filters[0] as InputFilter.LengthFilter
        assertEquals(19, filter.max)
    }

    @Test
    fun `should set max length on edit text for default rule`() {
        inputFilterHandler.handle(pan, PAN_RULE)

        assertEquals(1, pan.filters.size)
        assertTrue(pan.filters[0] is InputFilter.LengthFilter)
        val filter = pan.filters[0] as InputFilter.LengthFilter
        assertEquals(19, filter.max)
    }

    @Test
    fun `should use default max length where max length cannot be determined from rule`() {
        val cardValidationRule = mock<CardValidationRule>()

        given(cardValidationRule.validLengths).willReturn(emptyList())

        inputFilterHandler.handle(pan, cardValidationRule)

        assertEquals(1, pan.filters.size)
        assertTrue(pan.filters[0] is InputFilter.LengthFilter)
        val filter = pan.filters[0] as InputFilter.LengthFilter
        assertEquals(100, filter.max)
    }

}