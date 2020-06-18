package com.worldpay.access.checkout.validation.controller

import android.text.Editable
import android.text.InputFilter
import android.widget.EditText
import com.nhaarman.mockitokotlin2.*
import com.worldpay.access.checkout.R
import com.worldpay.access.checkout.testutils.CardConfigurationUtil.Configurations.CARD_CONFIG_NO_BRAND
import com.worldpay.access.checkout.testutils.CardNumberUtil.VISA_PAN
import com.worldpay.access.checkout.validation.filters.CvvLengthFilter
import com.worldpay.access.checkout.validation.filters.ExpiryDateLengthFilter
import com.worldpay.access.checkout.validation.filters.PanLengthFilter
import com.worldpay.access.checkout.validation.listeners.text.CVVTextWatcher
import com.worldpay.access.checkout.validation.listeners.text.ExpiryDateTextWatcher
import com.worldpay.access.checkout.validation.listeners.text.PANTextWatcher
import com.worldpay.access.checkout.validation.listeners.text.TextWatcherFactory
import org.junit.Before
import org.junit.Test
import kotlin.test.assertEquals
import kotlin.test.assertTrue

class FieldDecoratorFactoryTest {

    private val cvvEditText = mock<EditText>()
    private val panEditText = mock<EditText>()
    private val expiryDateEditText = mock<EditText>()

    private val textWatcherFactory = mock<TextWatcherFactory>()

    private lateinit var fieldDecoratorFactory: FieldDecoratorFactory

    @Before
    fun setup() {
        fieldDecoratorFactory = FieldDecoratorFactory(
            textWatcherFactory = textWatcherFactory
        )
    }

    @Test
    fun `should add new text watchers when decorating cvv field each time`() {
        val cvvTextWatcher = mock<CVVTextWatcher>()
        given(cvvEditText.filters).willReturn(emptyArray())
        given(textWatcherFactory.createCvvTextWatcher()).willReturn(cvvTextWatcher)

        fieldDecoratorFactory.decorateCvvField(cvvEditText, panEditText, CARD_CONFIG_NO_BRAND)

        verify(cvvEditText, never()).removeTextChangedListener(any())
        verify(cvvEditText).addTextChangedListener(cvvTextWatcher)

        reset(cvvEditText)

        given(cvvEditText.filters).willReturn(emptyArray())
        given(cvvEditText.text).willReturn(mock())
        given(mock<Editable>().toString()).willReturn("")

        fieldDecoratorFactory.decorateCvvField(cvvEditText, panEditText, CARD_CONFIG_NO_BRAND)

        verify(cvvEditText).removeTextChangedListener(cvvTextWatcher)
        verify(cvvEditText).addTextChangedListener(cvvTextWatcher)
    }

    @Test
    fun `should add hint to cvv field`() {
        given(cvvEditText.filters).willReturn(emptyArray())

        fieldDecoratorFactory.decorateCvvField(cvvEditText, panEditText, CARD_CONFIG_NO_BRAND)

        verify(cvvEditText).setHint(R.string.card_cvc_hint)
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
    fun `should set text when the cvv field is in layout`() {
        val cvvEditable = mock<Editable>()
        given(cvvEditText.filters).willReturn(emptyArray())
        given(cvvEditText.isCursorVisible).willReturn(true)
        given(cvvEditText.text).willReturn(cvvEditable)
        given(cvvEditable.toString()).willReturn("123")

        fieldDecoratorFactory.decorateCvvField(cvvEditText, panEditText, CARD_CONFIG_NO_BRAND)

        verify(cvvEditText).isCursorVisible
        verify(cvvEditText).setText("123")
    }

    @Test
    fun `should not set text when the cvv field is not in layout`() {
        val cvvEditable = mock<Editable>()
        given(cvvEditText.filters).willReturn(emptyArray())
        given(cvvEditText.isCursorVisible).willReturn(false)
        given(cvvEditText.text).willReturn(cvvEditable)
        given(cvvEditable.toString()).willReturn("123")

        fieldDecoratorFactory.decorateCvvField(cvvEditText, panEditText, CARD_CONFIG_NO_BRAND)

        verify(cvvEditText).isCursorVisible
        verify(cvvEditText, never()).setText(any<String>())
    }

    @Test
    fun `should add new text watchers when decorating pan field each time`() {
        val panTextWatcher = mock<PANTextWatcher>()
        given(panEditText.filters).willReturn(emptyArray())
        given(textWatcherFactory.createPanTextWatcher(cvvEditText, CARD_CONFIG_NO_BRAND)).willReturn(panTextWatcher)

        fieldDecoratorFactory.decoratePanField(panEditText, cvvEditText, CARD_CONFIG_NO_BRAND)

        verify(panEditText, never()).removeTextChangedListener(any())
        verify(panEditText).addTextChangedListener(panTextWatcher)

        reset(panEditText)

        given(panEditText.filters).willReturn(emptyArray())
        given(panEditText.text).willReturn(mock())
        given(mock<Editable>().toString()).willReturn("")

        fieldDecoratorFactory.decoratePanField(panEditText, cvvEditText, CARD_CONFIG_NO_BRAND)

        verify(panEditText).removeTextChangedListener(panTextWatcher)
        verify(panEditText).addTextChangedListener(panTextWatcher)
    }

    @Test
    fun `should add hint to pan field`() {
        given(panEditText.filters).willReturn(emptyArray())

        fieldDecoratorFactory.decoratePanField(panEditText, cvvEditText, CARD_CONFIG_NO_BRAND)

        verify(panEditText).setHint(R.string.card_number_hint)
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
    fun `should set text when the pan field is in layout`() {
        val panEditable = mock<Editable>()
        given(panEditText.filters).willReturn(emptyArray())
        given(panEditText.isCursorVisible).willReturn(true)
        given(panEditText.text).willReturn(panEditable)
        given(panEditable.toString()).willReturn(VISA_PAN)

        fieldDecoratorFactory.decoratePanField(panEditText, cvvEditText, CARD_CONFIG_NO_BRAND)

        verify(panEditText).isCursorVisible
        verify(panEditText).setText(VISA_PAN)
    }

    @Test
    fun `should not set text when the pan field is not in layout`() {
        val panEditable = mock<Editable>()
        given(panEditText.filters).willReturn(emptyArray())
        given(panEditText.isCursorVisible).willReturn(false)
        given(panEditText.text).willReturn(panEditable)
        given(panEditable.toString()).willReturn(VISA_PAN)

        fieldDecoratorFactory.decoratePanField(panEditText, cvvEditText, CARD_CONFIG_NO_BRAND)

        verify(panEditText).isCursorVisible
        verify(panEditText, never()).setText(any<String>())
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
        val expiryDateEditable = mock<Editable>()
        given(expiryDateEditText.filters).willReturn(emptyArray())
        given(expiryDateEditText.isCursorVisible).willReturn(false)
        given(expiryDateEditText.text).willReturn(expiryDateEditable)
        given(expiryDateEditable.toString()).willReturn("12/21")

        fieldDecoratorFactory.decorateExpiryDateFields(expiryDateEditText, CARD_CONFIG_NO_BRAND)

        verify(expiryDateEditText).isCursorVisible
        verify(expiryDateEditText, never()).setText(any<String>())
    }

}
