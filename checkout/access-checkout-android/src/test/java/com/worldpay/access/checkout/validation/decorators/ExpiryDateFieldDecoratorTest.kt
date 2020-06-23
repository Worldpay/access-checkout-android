package com.worldpay.access.checkout.validation.decorators

import android.text.Editable
import android.text.InputFilter
import android.widget.EditText
import com.nhaarman.mockitokotlin2.*
import com.worldpay.access.checkout.R
import com.worldpay.access.checkout.validation.filters.ExpiryDateLengthFilter
import com.worldpay.access.checkout.validation.filters.LengthFilterFactory
import com.worldpay.access.checkout.validation.listeners.focus.ExpiryDateFocusChangeListener
import com.worldpay.access.checkout.validation.listeners.text.ExpiryDateTextWatcher
import org.junit.Before
import org.junit.Test
import kotlin.test.assertEquals
import kotlin.test.assertTrue

class ExpiryDateFieldDecoratorTest {

    private val expiryDateEditText = mock<EditText>()

    private val expiryDateTextWatcher = mock<ExpiryDateTextWatcher>()
    private val expiryDateFocusChangeListener = mock<ExpiryDateFocusChangeListener>()
    private val lengthFilterFactory = LengthFilterFactory()

    private lateinit var expiryDateFieldDecorator: ExpiryDateFieldDecorator

    @Before
    fun setup() {
        expiryDateFieldDecorator = ExpiryDateFieldDecorator(
            expiryDateTextWatcher = expiryDateTextWatcher,
            expiryDateFocusChangeListener = expiryDateFocusChangeListener,
            expiryDateLengthFilter = lengthFilterFactory.getExpiryDateLengthFilter(),
            expiryDateEditText = expiryDateEditText
        )
    }

    @Test
    fun `should add new text watchers when decorating expiry date field each time`() {
        given(expiryDateEditText.filters).willReturn(emptyArray())

        expiryDateFieldDecorator.decorate()

        verify(expiryDateEditText, never()).removeTextChangedListener(any())
        verify(expiryDateEditText).addTextChangedListener(expiryDateTextWatcher)

        reset(expiryDateEditText)

        given(expiryDateEditText.filters).willReturn(emptyArray())
        given(expiryDateEditText.text).willReturn(mock())
        given(mock<Editable>().toString()).willReturn("")

        expiryDateFieldDecorator.decorate()

        verify(expiryDateEditText).removeTextChangedListener(expiryDateTextWatcher)
        verify(expiryDateEditText).addTextChangedListener(expiryDateTextWatcher)
    }

    @Test
    fun `should add hint to expiry date field`() {
        given(expiryDateEditText.filters).willReturn(emptyArray())

        expiryDateFieldDecorator.decorate()

        verify(expiryDateEditText).setHint(R.string.card_expiry_date_hint)
    }

    @Test
    fun `should add filters when decorating expiry date field`() {
        given(expiryDateEditText.filters).willReturn(emptyArray())

        val captor = argumentCaptor<Array<InputFilter>>()

        expiryDateFieldDecorator.decorate()

        verify(expiryDateEditText).filters = captor.capture()

        assertEquals(1, captor.firstValue.size)
        assertTrue(captor.firstValue[0] is ExpiryDateLengthFilter)
    }

    @Test
    fun `should replace any length filters when decorating expiry date field multiple times`() {
        given(expiryDateEditText.filters).willReturn(arrayOf(
            InputFilter.LengthFilter(1000),
            InputFilter.AllCaps(),
            ExpiryDateLengthFilter()
        ))

        val captor = argumentCaptor<Array<InputFilter>>()

        expiryDateFieldDecorator.decorate()

        verify(expiryDateEditText).filters = captor.capture()

        assertEquals(2, captor.firstValue.size)
        assertTrue(captor.firstValue[0] is InputFilter.AllCaps)
        assertTrue(captor.firstValue[1] is ExpiryDateLengthFilter)
    }

    @Test
    fun `should set text when the expiry date field is in layout`() {
        val expiryDateEditable = mock<Editable>()
        given(expiryDateEditText.filters).willReturn(emptyArray())
        given(expiryDateEditText.isCursorVisible).willReturn(true)
        given(expiryDateEditText.text).willReturn(expiryDateEditable)
        given(expiryDateEditable.toString()).willReturn("12/21")

        expiryDateFieldDecorator.decorate()

        verify(expiryDateEditText).isCursorVisible
        verify(expiryDateEditText).setText("12/21")
    }

    @Test
    fun `should not set text when the expiry date field is not in layout`() {
        given(expiryDateEditText.filters).willReturn(emptyArray())
        given(expiryDateEditText.isCursorVisible).willReturn(false)

        expiryDateFieldDecorator.decorate()

        verify(expiryDateEditText).isCursorVisible
        verify(expiryDateEditText, never()).setText(any<String>())
    }

    @Test
    fun `should add focus change listener to expiry date field`() {
        given(expiryDateEditText.filters).willReturn(emptyArray())
        given(expiryDateEditText.isCursorVisible).willReturn(false)

        expiryDateFieldDecorator.decorate()

        verify(expiryDateEditText).onFocusChangeListener = expiryDateFocusChangeListener
    }

}
