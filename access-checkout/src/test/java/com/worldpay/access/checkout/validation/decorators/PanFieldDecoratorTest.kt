package com.worldpay.access.checkout.validation.decorators

import android.text.Editable
import android.text.InputFilter
import android.widget.EditText
import com.nhaarman.mockitokotlin2.any
import com.nhaarman.mockitokotlin2.argumentCaptor
import com.nhaarman.mockitokotlin2.given
import com.nhaarman.mockitokotlin2.mock
import com.nhaarman.mockitokotlin2.never
import com.nhaarman.mockitokotlin2.reset
import com.nhaarman.mockitokotlin2.spy
import com.nhaarman.mockitokotlin2.verify
import com.worldpay.access.checkout.R
import com.worldpay.access.checkout.testutils.CardNumberUtil.VISA_PAN
import com.worldpay.access.checkout.validation.filters.LengthFilterFactory
import com.worldpay.access.checkout.validation.filters.PanLengthFilter
import com.worldpay.access.checkout.validation.listeners.focus.PanFocusChangeListener
import com.worldpay.access.checkout.validation.listeners.text.PanTextWatcher
import kotlin.test.assertEquals
import kotlin.test.assertTrue
import org.junit.Before
import org.junit.Test

class PanFieldDecoratorTest {

    private val panEditText = mock<EditText>()

    private val panTextWatcher = mock<PanTextWatcher>()
    private val panFocusChangeListener = mock<PanFocusChangeListener>()
    private val lengthFilterFactory = LengthFilterFactory()

    private lateinit var panFieldDecorator: PanFieldDecorator

    @Before
    fun setup() {
        panFieldDecorator = PanFieldDecorator(
            panTextWatcher = panTextWatcher,
            panFocusChangeListener = panFocusChangeListener,
            panLengthFilter = lengthFilterFactory.getPanLengthFilter(false),
            panEditText = panEditText
        )
    }

    @Test
    fun `should add new text watchers when decorating pan field each time`() {
        given(panEditText.filters).willReturn(emptyArray())

        panFieldDecorator.decorate()

        verify(panEditText, never()).removeTextChangedListener(any())
        verify(panEditText).addTextChangedListener(panTextWatcher)

        reset(panEditText)

        given(panEditText.filters).willReturn(emptyArray())
        given(panEditText.text).willReturn(mock())
        given(mock<Editable>().toString()).willReturn("")

        panFieldDecorator.decorate()

        verify(panEditText).removeTextChangedListener(panTextWatcher)
        verify(panEditText).addTextChangedListener(panTextWatcher)
    }

    @Test
    fun `should add hint to pan field`() {
        given(panEditText.filters).willReturn(emptyArray())

        panFieldDecorator.decorate()

        verify(panEditText).setHint(R.string.card_number_hint)
    }

    @Test
    fun `should add filters when decorating pan field`() {
        given(panEditText.filters).willReturn(emptyArray())

        val captor = argumentCaptor<Array<InputFilter>>()

        panFieldDecorator.decorate()

        verify(panEditText).filters = captor.capture()

        assertEquals(1, captor.firstValue.size)
        assertTrue(captor.firstValue[0] is PanLengthFilter)
    }

    @Test
    fun `should replace any length filters when decorating pan field multiple times`() {
        given(panEditText.filters).willReturn(
            arrayOf(
                InputFilter.LengthFilter(1000),
                InputFilter.AllCaps(),
                PanLengthFilter(false)
            )
        )

        val captor = argumentCaptor<Array<InputFilter>>()

        panFieldDecorator.decorate()

        verify(panEditText).filters = captor.capture()

        assertEquals(2, captor.firstValue.size)
        assertTrue(captor.firstValue[0] is InputFilter.AllCaps)
        assertTrue(captor.firstValue[1] is PanLengthFilter)
    }

    @Test
    fun `should set text when the pan field is in layout`() {
        val panEditable = mock<Editable>()
        given(panEditText.filters).willReturn(emptyArray())
        given(panEditText.isCursorVisible).willReturn(true)
        given(panEditText.text).willReturn(panEditable)
        given(panEditable.toString()).willReturn(VISA_PAN)

        panFieldDecorator.decorate()

        verify(panEditText).isCursorVisible
        verify(panEditText).setText(VISA_PAN)
    }

    @Test
    fun `should not set text when the pan field is not in layout`() {
        given(panEditText.filters).willReturn(emptyArray())
        given(panEditText.isCursorVisible).willReturn(false)

        panFieldDecorator.decorate()

        verify(panEditText).isCursorVisible
        verify(panEditText, never()).setText(any<String>())
    }

    @Test
    fun `should add focus change listener to pan field`() {
        given(panEditText.filters).willReturn(emptyArray())
        given(panEditText.isCursorVisible).willReturn(false)

        panFieldDecorator.decorate()

        verify(panEditText).onFocusChangeListener = panFocusChangeListener
    }

    @Test
    fun `should call decorate when calling update function`() {
        given(panEditText.filters).willReturn(emptyArray())
        given(panEditText.isCursorVisible).willReturn(false)

        val fieldDecorator = spy(panFieldDecorator)

        fieldDecorator.update()

        verify(fieldDecorator).decorate()
    }
}
