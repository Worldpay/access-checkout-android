package com.worldpay.access.checkout.validation.decorators

import android.text.Editable
import android.text.InputFilter
import android.widget.EditText
import com.nhaarman.mockitokotlin2.*
import com.worldpay.access.checkout.R
import com.worldpay.access.checkout.testutils.CardConfigurationUtil.Configurations.CARD_CONFIG_NO_BRAND
import com.worldpay.access.checkout.validation.filters.CvcLengthFilter
import com.worldpay.access.checkout.validation.filters.LengthFilterFactory
import com.worldpay.access.checkout.validation.listeners.focus.CvcFocusChangeListener
import com.worldpay.access.checkout.validation.listeners.text.CVCTextWatcher
import org.junit.Before
import org.junit.Test
import kotlin.test.assertEquals
import kotlin.test.assertTrue

class CvcFieldDecoratorTest {

    private val cvcEditText = mock<EditText>()
    private val panEditText = mock<EditText>()

    private val cvcTextWatcher = mock<CVCTextWatcher>()
    private val cvcFocusChangeListener = mock<CvcFocusChangeListener>()
    private val lengthFilterFactory = LengthFilterFactory()

    private lateinit var cvcFieldDecorator : CvcFieldDecorator

    @Before
    fun setup() {
        cvcFieldDecorator = CvcFieldDecorator(
            cvcTextWatcher = cvcTextWatcher,
            cvcFocusChangeListener = cvcFocusChangeListener,
            lengthFilterFactory = lengthFilterFactory,
            cvcEditText = cvcEditText,
            panEditText = panEditText
        )
    }

    @Test
    fun `should add new text watchers when decorating cvc field each time`() {
        given(cvcEditText.filters).willReturn(emptyArray())

        cvcFieldDecorator.decorate(CARD_CONFIG_NO_BRAND)

        verify(cvcEditText, never()).removeTextChangedListener(any())
        verify(cvcEditText).addTextChangedListener(cvcTextWatcher)

        reset(cvcEditText)

        given(cvcEditText.filters).willReturn(emptyArray())
        given(cvcEditText.text).willReturn(mock())
        given(mock<Editable>().toString()).willReturn("")

        cvcFieldDecorator.decorate(CARD_CONFIG_NO_BRAND)

        verify(cvcEditText).removeTextChangedListener(cvcTextWatcher)
        verify(cvcEditText).addTextChangedListener(cvcTextWatcher)
    }

    @Test
    fun `should add hint to cvc field`() {
        given(cvcEditText.filters).willReturn(emptyArray())

        cvcFieldDecorator.decorate(CARD_CONFIG_NO_BRAND)

        verify(cvcEditText).setHint(R.string.card_cvc_hint)
    }

    @Test
    fun `should add filters when decorating cvc field`() {
        given(cvcEditText.filters).willReturn(emptyArray())

        val captor = argumentCaptor<Array<InputFilter>>()

        cvcFieldDecorator.decorate(CARD_CONFIG_NO_BRAND)

        verify(cvcEditText).filters = captor.capture()

        assertEquals(1, captor.firstValue.size)
        assertTrue(captor.firstValue[0] is CvcLengthFilter)
    }

    @Test
    fun `should replace any length filters when decorating cvc field multiple times`() {
        given(cvcEditText.filters).willReturn(arrayOf(
            InputFilter.LengthFilter(1000),
            InputFilter.AllCaps(),
            CvcLengthFilter(panEditText, CARD_CONFIG_NO_BRAND)
        ))

        val captor = argumentCaptor<Array<InputFilter>>()

        cvcFieldDecorator.decorate(CARD_CONFIG_NO_BRAND)

        verify(cvcEditText).filters = captor.capture()

        assertEquals(2, captor.firstValue.size)
        assertTrue(captor.firstValue[0] is InputFilter.AllCaps)
        assertTrue(captor.firstValue[1] is CvcLengthFilter)
    }

    @Test
    fun `should set text when the cvc field is in layout`() {
        val cvcEditable = mock<Editable>()
        given(cvcEditText.filters).willReturn(emptyArray())
        given(cvcEditText.isCursorVisible).willReturn(true)
        given(cvcEditText.text).willReturn(cvcEditable)
        given(cvcEditable.toString()).willReturn("123")

        cvcFieldDecorator.decorate(CARD_CONFIG_NO_BRAND)

        verify(cvcEditText).isCursorVisible
        verify(cvcEditText).setText("123")
    }

    @Test
    fun `should not set text when the cvc field is not in layout`() {
        given(cvcEditText.filters).willReturn(emptyArray())
        given(cvcEditText.isCursorVisible).willReturn(false)

        cvcFieldDecorator.decorate(CARD_CONFIG_NO_BRAND)

        verify(cvcEditText).isCursorVisible
        verify(cvcEditText, never()).setText(any<String>())
    }

    @Test
    fun `should add focus change listener to cvc field`() {
        given(cvcEditText.filters).willReturn(emptyArray())
        given(cvcEditText.isCursorVisible).willReturn(false)

        cvcFieldDecorator.decorate(CARD_CONFIG_NO_BRAND)

        verify(cvcEditText).onFocusChangeListener = cvcFocusChangeListener
    }

}
