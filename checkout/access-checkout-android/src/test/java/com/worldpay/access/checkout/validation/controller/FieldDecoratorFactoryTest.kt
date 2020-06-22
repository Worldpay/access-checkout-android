package com.worldpay.access.checkout.validation.controller

import android.text.Editable
import android.text.InputFilter
import android.widget.EditText
import com.nhaarman.mockitokotlin2.*
import com.worldpay.access.checkout.R
import com.worldpay.access.checkout.testutils.CardConfigurationUtil.Configurations.CARD_CONFIG_NO_BRAND
import com.worldpay.access.checkout.testutils.CardNumberUtil.VISA_PAN
import com.worldpay.access.checkout.validation.filters.CvcLengthFilter
import com.worldpay.access.checkout.validation.filters.ExpiryDateLengthFilter
import com.worldpay.access.checkout.validation.filters.PanLengthFilter
import com.worldpay.access.checkout.validation.listeners.focus.CvcFocusChangeListener
import com.worldpay.access.checkout.validation.listeners.focus.ExpiryDateFocusChangeListener
import com.worldpay.access.checkout.validation.listeners.focus.FocusChangeListenerFactory
import com.worldpay.access.checkout.validation.listeners.focus.PanFocusChangeListener
import com.worldpay.access.checkout.validation.listeners.text.CVCTextWatcher
import com.worldpay.access.checkout.validation.listeners.text.ExpiryDateTextWatcher
import com.worldpay.access.checkout.validation.listeners.text.PANTextWatcher
import com.worldpay.access.checkout.validation.listeners.text.TextWatcherFactory
import org.junit.Before
import org.junit.Test
import kotlin.test.assertEquals
import kotlin.test.assertTrue

class FieldDecoratorFactoryTest {

    private val cvcEditText = mock<EditText>()
    private val panEditText = mock<EditText>()
    private val expiryDateEditText = mock<EditText>()

    private val textWatcherFactory = mock<TextWatcherFactory>()
    private val focusChangeListenerFactory = mock<FocusChangeListenerFactory>()

    private lateinit var fieldDecoratorFactory: FieldDecoratorFactory

    @Before
    fun setup() {
        fieldDecoratorFactory = FieldDecoratorFactory(
            textWatcherFactory = textWatcherFactory,
            focusChangeListenerFactory = focusChangeListenerFactory
        )
    }

    @Test
    fun `should add new text watchers when decorating cvc field each time`() {
        val cvcTextWatcher = mock<CVCTextWatcher>()
        given(cvcEditText.filters).willReturn(emptyArray())
        given(textWatcherFactory.createCvcTextWatcher()).willReturn(cvcTextWatcher)

        fieldDecoratorFactory.decorateCvcField(cvcEditText, panEditText, CARD_CONFIG_NO_BRAND)

        verify(cvcEditText, never()).removeTextChangedListener(any())
        verify(cvcEditText).addTextChangedListener(cvcTextWatcher)

        reset(cvcEditText)

        given(cvcEditText.filters).willReturn(emptyArray())
        given(cvcEditText.text).willReturn(mock())
        given(mock<Editable>().toString()).willReturn("")

        fieldDecoratorFactory.decorateCvcField(cvcEditText, panEditText, CARD_CONFIG_NO_BRAND)

        verify(cvcEditText).removeTextChangedListener(cvcTextWatcher)
        verify(cvcEditText).addTextChangedListener(cvcTextWatcher)
    }

    @Test
    fun `should add hint to cvc field`() {
        given(cvcEditText.filters).willReturn(emptyArray())

        fieldDecoratorFactory.decorateCvcField(cvcEditText, panEditText, CARD_CONFIG_NO_BRAND)

        verify(cvcEditText).setHint(R.string.card_cvc_hint)
    }

    @Test
    fun `should add filters when decorating cvc field`() {
        given(cvcEditText.filters).willReturn(emptyArray())

        val captor = argumentCaptor<Array<InputFilter>>()

        fieldDecoratorFactory.decorateCvcField(cvcEditText, panEditText, CARD_CONFIG_NO_BRAND)

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

        fieldDecoratorFactory.decorateCvcField(cvcEditText, panEditText, CARD_CONFIG_NO_BRAND)

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

        fieldDecoratorFactory.decorateCvcField(cvcEditText, panEditText, CARD_CONFIG_NO_BRAND)

        verify(cvcEditText).isCursorVisible
        verify(cvcEditText).setText("123")
    }

    @Test
    fun `should not set text when the cvc field is not in layout`() {
        given(cvcEditText.filters).willReturn(emptyArray())
        given(cvcEditText.isCursorVisible).willReturn(false)

        fieldDecoratorFactory.decorateCvcField(cvcEditText, panEditText, CARD_CONFIG_NO_BRAND)

        verify(cvcEditText).isCursorVisible
        verify(cvcEditText, never()).setText(any<String>())
    }

    @Test
    fun `should add focus change listener to cvc field`() {
        val listener = mock<CvcFocusChangeListener>()
        given(cvcEditText.filters).willReturn(emptyArray())
        given(cvcEditText.isCursorVisible).willReturn(false)
        given(focusChangeListenerFactory.createCvcFocusChangeListener()).willReturn(listener)
        val argumentCaptor = argumentCaptor<CvcFocusChangeListener>()

        fieldDecoratorFactory.decorateCvcField(cvcEditText, panEditText, CARD_CONFIG_NO_BRAND)

        verify(cvcEditText).onFocusChangeListener = argumentCaptor.capture()

        assertEquals(listener, argumentCaptor.firstValue)
    }

    @Test
    fun `should add new text watchers when decorating pan field each time`() {
        val panTextWatcher = mock<PANTextWatcher>()
        given(panEditText.filters).willReturn(emptyArray())
        given(textWatcherFactory.createPanTextWatcher(cvcEditText, CARD_CONFIG_NO_BRAND)).willReturn(panTextWatcher)

        fieldDecoratorFactory.decoratePanField(panEditText, cvcEditText, CARD_CONFIG_NO_BRAND)

        verify(panEditText, never()).removeTextChangedListener(any())
        verify(panEditText).addTextChangedListener(panTextWatcher)

        reset(panEditText)

        given(panEditText.filters).willReturn(emptyArray())
        given(panEditText.text).willReturn(mock())
        given(mock<Editable>().toString()).willReturn("")

        fieldDecoratorFactory.decoratePanField(panEditText, cvcEditText, CARD_CONFIG_NO_BRAND)

        verify(panEditText).removeTextChangedListener(panTextWatcher)
        verify(panEditText).addTextChangedListener(panTextWatcher)
    }

    @Test
    fun `should add hint to pan field`() {
        given(panEditText.filters).willReturn(emptyArray())

        fieldDecoratorFactory.decoratePanField(panEditText, cvcEditText, CARD_CONFIG_NO_BRAND)

        verify(panEditText).setHint(R.string.card_number_hint)
    }

    @Test
    fun `should add filters when decorating pan field`() {
        given(panEditText.filters).willReturn(emptyArray())

        val captor = argumentCaptor<Array<InputFilter>>()

        fieldDecoratorFactory.decoratePanField(panEditText, cvcEditText, CARD_CONFIG_NO_BRAND)

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

        fieldDecoratorFactory.decoratePanField(panEditText, cvcEditText, CARD_CONFIG_NO_BRAND)

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

        fieldDecoratorFactory.decoratePanField(panEditText, cvcEditText, CARD_CONFIG_NO_BRAND)

        verify(panEditText).isCursorVisible
        verify(panEditText).setText(VISA_PAN)
    }

    @Test
    fun `should not set text when the pan field is not in layout`() {
        given(panEditText.filters).willReturn(emptyArray())
        given(panEditText.isCursorVisible).willReturn(false)

        fieldDecoratorFactory.decoratePanField(panEditText, cvcEditText, CARD_CONFIG_NO_BRAND)

        verify(panEditText).isCursorVisible
        verify(panEditText, never()).setText(any<String>())
    }

    @Test
    fun `should add focus change listener to pan field`() {
        val listener = mock<PanFocusChangeListener>()
        given(panEditText.filters).willReturn(emptyArray())
        given(panEditText.isCursorVisible).willReturn(false)
        given(focusChangeListenerFactory.createPanFocusChangeListener()).willReturn(listener)
        val argumentCaptor = argumentCaptor<PanFocusChangeListener>()

        fieldDecoratorFactory.decoratePanField(panEditText, cvcEditText, CARD_CONFIG_NO_BRAND)

        verify(panEditText).onFocusChangeListener = argumentCaptor.capture()

        assertEquals(listener, argumentCaptor.firstValue)
    }

    @Test
    fun `should add new text watchers when decorating expiry date field each time`() {
        val expiryDateTextWatcher = mock<ExpiryDateTextWatcher>()
        given(expiryDateEditText.filters).willReturn(emptyArray())
        given(textWatcherFactory.createExpiryDateTextWatcher(expiryDateEditText)).willReturn(expiryDateTextWatcher)

        fieldDecoratorFactory.decorateExpiryDateFields(expiryDateEditText, CARD_CONFIG_NO_BRAND)

        verify(expiryDateEditText, never()).removeTextChangedListener(any())
        verify(expiryDateEditText).addTextChangedListener(expiryDateTextWatcher)

        reset(expiryDateEditText)

        given(expiryDateEditText.filters).willReturn(emptyArray())
        given(expiryDateEditText.text).willReturn(mock())
        given(mock<Editable>().toString()).willReturn("")

        fieldDecoratorFactory.decorateExpiryDateFields(expiryDateEditText, CARD_CONFIG_NO_BRAND)

        verify(expiryDateEditText).removeTextChangedListener(expiryDateTextWatcher)
        verify(expiryDateEditText).addTextChangedListener(expiryDateTextWatcher)
    }

    @Test
    fun `should add hint to expiry date field`() {
        given(expiryDateEditText.filters).willReturn(emptyArray())

        fieldDecoratorFactory.decorateExpiryDateFields(expiryDateEditText, CARD_CONFIG_NO_BRAND)

        verify(expiryDateEditText).setHint(R.string.card_expiry_date_hint)
    }

    @Test
    fun `should add filters when decorating expiry date field`() {
        given(expiryDateEditText.filters).willReturn(emptyArray())

        val captor = argumentCaptor<Array<InputFilter>>()

        fieldDecoratorFactory.decorateExpiryDateFields(expiryDateEditText, CARD_CONFIG_NO_BRAND)

        verify(expiryDateEditText).filters = captor.capture()

        assertEquals(1, captor.firstValue.size)
        assertTrue(captor.firstValue[0] is ExpiryDateLengthFilter)
    }

    @Test
    fun `should replace any length filters when decorating expiry date field multiple times`() {
        given(expiryDateEditText.filters).willReturn(arrayOf(
            InputFilter.LengthFilter(1000),
            InputFilter.AllCaps(),
            ExpiryDateLengthFilter(CARD_CONFIG_NO_BRAND)
        ))

        val captor = argumentCaptor<Array<InputFilter>>()

        fieldDecoratorFactory.decorateExpiryDateFields(expiryDateEditText, CARD_CONFIG_NO_BRAND)

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

        fieldDecoratorFactory.decorateExpiryDateFields(expiryDateEditText, CARD_CONFIG_NO_BRAND)

        verify(expiryDateEditText).isCursorVisible
        verify(expiryDateEditText).setText("12/21")
    }

    @Test
    fun `should not set text when the expiry date field is not in layout`() {
        given(expiryDateEditText.filters).willReturn(emptyArray())
        given(expiryDateEditText.isCursorVisible).willReturn(false)

        fieldDecoratorFactory.decorateExpiryDateFields(expiryDateEditText, CARD_CONFIG_NO_BRAND)

        verify(expiryDateEditText).isCursorVisible
        verify(expiryDateEditText, never()).setText(any<String>())
    }

    @Test
    fun `should add focus change listener to expiry date field`() {
        val listener = mock<ExpiryDateFocusChangeListener>()
        given(expiryDateEditText.filters).willReturn(emptyArray())
        given(expiryDateEditText.isCursorVisible).willReturn(false)
        given(focusChangeListenerFactory.createExpiryDateFocusChangeListener()).willReturn(listener)
        val argumentCaptor = argumentCaptor<ExpiryDateFocusChangeListener>()

        fieldDecoratorFactory.decorateExpiryDateFields(expiryDateEditText, CARD_CONFIG_NO_BRAND)

        verify(expiryDateEditText).onFocusChangeListener = argumentCaptor.capture()

        assertEquals(listener, argumentCaptor.firstValue)
    }

}
