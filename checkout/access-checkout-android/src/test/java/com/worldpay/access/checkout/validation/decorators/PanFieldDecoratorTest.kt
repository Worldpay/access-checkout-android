package com.worldpay.access.checkout.validation.decorators

import android.text.Editable
import android.text.InputFilter
import android.widget.EditText
import com.nhaarman.mockitokotlin2.*
import com.worldpay.access.checkout.R
import com.worldpay.access.checkout.testutils.CardConfigurationUtil.Configurations.CARD_CONFIG_NO_BRAND
import com.worldpay.access.checkout.testutils.CardNumberUtil.VISA_PAN
import com.worldpay.access.checkout.validation.filters.LengthFilterFactory
import com.worldpay.access.checkout.validation.filters.PanLengthFilter
import com.worldpay.access.checkout.validation.listeners.focus.PanFocusChangeListener
import com.worldpay.access.checkout.validation.listeners.text.PANTextWatcher
import com.worldpay.access.checkout.validation.listeners.text.TextWatcherFactory
import org.junit.Before
import org.junit.Test
import kotlin.test.assertEquals
import kotlin.test.assertTrue

class PanFieldDecoratorTest {

    private val cvcEditText = mock<EditText>()
    private val panEditText = mock<EditText>()

    private val textWatcherFactory = mock<TextWatcherFactory>()
    private val panFocusChangeListener = mock<PanFocusChangeListener>()
    private val lengthFilterFactory = LengthFilterFactory()

    private lateinit var panFieldDecorator: PanFieldDecorator

    @Before
    fun setup() {
        panFieldDecorator = PanFieldDecorator(
            textWatcherFactory = textWatcherFactory,
            panFocusChangeListener = panFocusChangeListener,
            lengthFilterFactory = lengthFilterFactory,
            panEditText = panEditText,
            cvcEditText = cvcEditText
        )
    }

    @Test
    fun `should add new text watchers when decorating pan field each time`() {
        val panTextWatcher = mock<PANTextWatcher>()
        given(panEditText.filters).willReturn(emptyArray())
        given(textWatcherFactory.createPanTextWatcher(cvcEditText, CARD_CONFIG_NO_BRAND)).willReturn(panTextWatcher)

        panFieldDecorator.decorate(CARD_CONFIG_NO_BRAND)

        verify(panEditText, never()).removeTextChangedListener(any())
        verify(panEditText).addTextChangedListener(panTextWatcher)

        reset(panEditText)

        given(panEditText.filters).willReturn(emptyArray())
        given(panEditText.text).willReturn(mock())
        given(mock<Editable>().toString()).willReturn("")

        panFieldDecorator.decorate(CARD_CONFIG_NO_BRAND)

        verify(panEditText).removeTextChangedListener(panTextWatcher)
        verify(panEditText).addTextChangedListener(panTextWatcher)
    }

    @Test
    fun `should add hint to pan field`() {
        given(panEditText.filters).willReturn(emptyArray())

        panFieldDecorator.decorate(CARD_CONFIG_NO_BRAND)

        verify(panEditText).setHint(R.string.card_number_hint)
    }

    @Test
    fun `should add filters when decorating pan field`() {
        given(panEditText.filters).willReturn(emptyArray())

        val captor = argumentCaptor<Array<InputFilter>>()

        panFieldDecorator.decorate(CARD_CONFIG_NO_BRAND)

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

        panFieldDecorator.decorate(CARD_CONFIG_NO_BRAND)

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

        panFieldDecorator.decorate(CARD_CONFIG_NO_BRAND)

        verify(panEditText).isCursorVisible
        verify(panEditText).setText(VISA_PAN)
    }

    @Test
    fun `should not set text when the pan field is not in layout`() {
        given(panEditText.filters).willReturn(emptyArray())
        given(panEditText.isCursorVisible).willReturn(false)

        panFieldDecorator.decorate(CARD_CONFIG_NO_BRAND)

        verify(panEditText).isCursorVisible
        verify(panEditText, never()).setText(any<String>())
    }

    @Test
    fun `should add focus change listener to pan field`() {
        given(panEditText.filters).willReturn(emptyArray())
        given(panEditText.isCursorVisible).willReturn(false)

        panFieldDecorator.decorate(CARD_CONFIG_NO_BRAND)

        verify(panEditText).onFocusChangeListener = panFocusChangeListener
    }

}
