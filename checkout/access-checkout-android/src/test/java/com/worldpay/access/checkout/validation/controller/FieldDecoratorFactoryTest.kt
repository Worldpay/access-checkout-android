package com.worldpay.access.checkout.validation.controller

import android.text.InputFilter
import android.widget.EditText
import com.nhaarman.mockitokotlin2.*
import com.worldpay.access.checkout.client.validation.AccessCheckoutCardValidationListener
import com.worldpay.access.checkout.testutils.CardConfigurationUtil.Configurations.CARD_CONFIG_NO_BRAND
import com.worldpay.access.checkout.validation.filters.CvvLengthFilter
import com.worldpay.access.checkout.validation.filters.ExpiryMonthLengthFilter
import com.worldpay.access.checkout.validation.filters.ExpiryYearLengthFilter
import com.worldpay.access.checkout.validation.filters.PanLengthFilter
import com.worldpay.access.checkout.validation.watchers.*
import org.junit.Before
import org.junit.Test
import org.mockito.Mockito
import kotlin.test.assertEquals
import kotlin.test.assertTrue

class FieldDecoratorFactoryTest {

    private val cvvEditText = mock<EditText>()
    private val panEditText = mock<EditText>()
    private val expiryMonthEditText = mock<EditText>()
    private val expiryYearEditText = mock<EditText>()

    private lateinit var fieldDecoratorFactory: FieldDecoratorFactory

    @Before
    fun setup() {
        val textWatcherFactory = TextWatcherFactory(
            accessCheckoutValidationListener = mock<AccessCheckoutCardValidationListener>()
        )

        fieldDecoratorFactory = FieldDecoratorFactory(
            textWatcherFactory = textWatcherFactory
        )
    }

    @Test
    fun `should add new text watchers when decorating cvv field each time`() {
        given(cvvEditText.filters).willReturn(emptyArray())

        fieldDecoratorFactory.decorateCvvField(cvvEditText, panEditText, CARD_CONFIG_NO_BRAND)

        verify(cvvEditText, never()).removeTextChangedListener(any())
        verify(cvvEditText).addTextChangedListener(Mockito.any(CVVTextWatcher::class.java))

        reset(cvvEditText)

        given(cvvEditText.filters).willReturn(emptyArray())

        fieldDecoratorFactory.decorateCvvField(cvvEditText, panEditText, CARD_CONFIG_NO_BRAND)

        verify(cvvEditText).removeTextChangedListener(Mockito.any(CVVTextWatcher::class.java))
        verify(cvvEditText).addTextChangedListener(Mockito.any(CVVTextWatcher::class.java))
    }

    @Test
    fun `should add filters when decorating cvv field`() {
        given(cvvEditText.filters).willReturn(emptyArray())

        val captor = argumentCaptor<Array<InputFilter>>()

        fieldDecoratorFactory.decorateCvvField(cvvEditText, panEditText, CARD_CONFIG_NO_BRAND)

        verify(cvvEditText).filters = captor.capture()

        assertEquals(1, captor.firstValue.size)
        assertTrue(captor.firstValue[0] is CvvLengthFilter)
    }

    @Test
    fun `should replace any length filters when decorating cvv field multiple times`() {
        given(cvvEditText.filters).willReturn(arrayOf(
            InputFilter.LengthFilter(1000),
            InputFilter.AllCaps(),
            CvvLengthFilter(panEditText, CARD_CONFIG_NO_BRAND)
        ))

        val captor = argumentCaptor<Array<InputFilter>>()

        fieldDecoratorFactory.decorateCvvField(cvvEditText, panEditText, CARD_CONFIG_NO_BRAND)

        verify(cvvEditText).filters = captor.capture()

        assertEquals(2, captor.firstValue.size)
        assertTrue(captor.firstValue[0] is InputFilter.AllCaps)
        assertTrue(captor.firstValue[1] is CvvLengthFilter)
    }

    @Test
    fun `should add new text watchers when decorating pan field each time`() {
        given(panEditText.filters).willReturn(emptyArray())

        fieldDecoratorFactory.decoratePanField(panEditText, cvvEditText, CARD_CONFIG_NO_BRAND)

        verify(panEditText, never()).removeTextChangedListener(any())
        verify(panEditText).addTextChangedListener(Mockito.any(PANTextWatcher::class.java))

        reset(panEditText)

        given(panEditText.filters).willReturn(emptyArray())

        fieldDecoratorFactory.decoratePanField(panEditText, cvvEditText, CARD_CONFIG_NO_BRAND)

        verify(panEditText).removeTextChangedListener(Mockito.any(PANTextWatcher::class.java))
        verify(panEditText).addTextChangedListener(Mockito.any(PANTextWatcher::class.java))
    }

    @Test
    fun `should add filters when decorating pan field`() {
        given(panEditText.filters).willReturn(emptyArray())

        val captor = argumentCaptor<Array<InputFilter>>()

        fieldDecoratorFactory.decoratePanField(panEditText, cvvEditText, CARD_CONFIG_NO_BRAND)

        verify(panEditText).filters = captor.capture()

        assertEquals(1, captor.firstValue.size)
        assertTrue(captor.firstValue[0] is PanLengthFilter)
    }

    @Test
    fun `should replace any length filters when decorating pan field multiple times`() {
        given(panEditText.filters).willReturn(arrayOf(
            InputFilter.LengthFilter(1000),
            InputFilter.AllCaps(),
            PanLengthFilter(CARD_CONFIG_NO_BRAND)
        ))

        val captor = argumentCaptor<Array<InputFilter>>()

        fieldDecoratorFactory.decoratePanField(panEditText, cvvEditText, CARD_CONFIG_NO_BRAND)

        verify(panEditText).filters = captor.capture()

        assertEquals(2, captor.firstValue.size)
        assertTrue(captor.firstValue[0] is InputFilter.AllCaps)
        assertTrue(captor.firstValue[1] is PanLengthFilter)
    }

    @Test
    fun `should add new text watchers when decorating expiry month field each time`() {
        given(expiryMonthEditText.filters).willReturn(emptyArray())
        given(expiryYearEditText.filters).willReturn(emptyArray())

        fieldDecoratorFactory.decorateExpiryDateFields(expiryMonthEditText, expiryYearEditText, CARD_CONFIG_NO_BRAND)

        verify(expiryMonthEditText, never()).removeTextChangedListener(any())
        verify(expiryMonthEditText).addTextChangedListener(Mockito.any(ExpiryMonthTextWatcher::class.java))

        reset(expiryMonthEditText)

        given(expiryMonthEditText.filters).willReturn(emptyArray())
        given(expiryYearEditText.filters).willReturn(emptyArray())

        fieldDecoratorFactory.decorateExpiryDateFields(expiryMonthEditText, expiryYearEditText, CARD_CONFIG_NO_BRAND)

        verify(expiryMonthEditText).removeTextChangedListener(Mockito.any(ExpiryMonthTextWatcher::class.java))
        verify(expiryMonthEditText).addTextChangedListener(Mockito.any(ExpiryMonthTextWatcher::class.java))
    }

    @Test
    fun `should add filters when decorating expiry month field`() {
        given(expiryMonthEditText.filters).willReturn(emptyArray())
        given(expiryYearEditText.filters).willReturn(emptyArray())

        val captor = argumentCaptor<Array<InputFilter>>()

        fieldDecoratorFactory.decorateExpiryDateFields(expiryMonthEditText, expiryYearEditText, CARD_CONFIG_NO_BRAND)

        verify(expiryMonthEditText).filters = captor.capture()

        assertEquals(1, captor.firstValue.size)
        assertTrue(captor.firstValue[0] is ExpiryMonthLengthFilter)
    }

    @Test
    fun `should replace any length filters when decorating expiry month field multiple times`() {
        given(expiryMonthEditText.filters).willReturn(arrayOf(
            InputFilter.LengthFilter(1000),
            InputFilter.AllCaps(),
            ExpiryMonthLengthFilter(CARD_CONFIG_NO_BRAND)
        ))
        given(expiryYearEditText.filters).willReturn(emptyArray())

        val captor = argumentCaptor<Array<InputFilter>>()

        fieldDecoratorFactory.decorateExpiryDateFields(expiryMonthEditText, expiryYearEditText, CARD_CONFIG_NO_BRAND)

        verify(expiryMonthEditText).filters = captor.capture()

        assertEquals(2, captor.firstValue.size)
        assertTrue(captor.firstValue[0] is InputFilter.AllCaps)
        assertTrue(captor.firstValue[1] is ExpiryMonthLengthFilter)
    }

    @Test
    fun `should add new text watchers when decorating expiry year field each time`() {
        given(expiryYearEditText.filters).willReturn(emptyArray())
        given(expiryMonthEditText.filters).willReturn(emptyArray())

        fieldDecoratorFactory.decorateExpiryDateFields(expiryMonthEditText, expiryYearEditText, CARD_CONFIG_NO_BRAND)

        verify(expiryYearEditText, never()).removeTextChangedListener(any())
        verify(expiryYearEditText).addTextChangedListener(Mockito.any(ExpiryYearTextWatcher::class.java))

        reset(expiryYearEditText)

        given(expiryYearEditText.filters).willReturn(emptyArray())
        given(expiryMonthEditText.filters).willReturn(emptyArray())

        fieldDecoratorFactory.decorateExpiryDateFields(expiryMonthEditText, expiryYearEditText, CARD_CONFIG_NO_BRAND)

        verify(expiryYearEditText).removeTextChangedListener(Mockito.any(ExpiryYearTextWatcher::class.java))
        verify(expiryYearEditText).addTextChangedListener(Mockito.any(ExpiryYearTextWatcher::class.java))
    }

    @Test
    fun `should add filters when decorating expiry year field`() {
        given(expiryYearEditText.filters).willReturn(emptyArray())
        given(expiryMonthEditText.filters).willReturn(emptyArray())

        val captor = argumentCaptor<Array<InputFilter>>()

        fieldDecoratorFactory.decorateExpiryDateFields(expiryMonthEditText, expiryYearEditText, CARD_CONFIG_NO_BRAND)

        verify(expiryYearEditText).filters = captor.capture()

        assertEquals(1, captor.firstValue.size)
        assertTrue(captor.firstValue[0] is ExpiryYearLengthFilter)
    }

    @Test
    fun `should replace any length filters when decorating expiry year field multiple times`() {
        given(expiryYearEditText.filters).willReturn(arrayOf(
            InputFilter.LengthFilter(1000),
            InputFilter.AllCaps(),
            ExpiryYearLengthFilter(CARD_CONFIG_NO_BRAND)
        ))
        given(expiryMonthEditText.filters).willReturn(emptyArray())

        val captor = argumentCaptor<Array<InputFilter>>()

        fieldDecoratorFactory.decorateExpiryDateFields(expiryMonthEditText, expiryYearEditText, CARD_CONFIG_NO_BRAND)

        verify(expiryYearEditText).filters = captor.capture()

        assertEquals(2, captor.firstValue.size)
        assertTrue(captor.firstValue[0] is InputFilter.AllCaps)
        assertTrue(captor.firstValue[1] is ExpiryYearLengthFilter)
    }

}
