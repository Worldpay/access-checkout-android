package com.worldpay.access.checkout.validation.controller

import android.text.Editable
import android.text.InputFilter
import android.widget.EditText
import com.nhaarman.mockitokotlin2.*
import com.worldpay.access.checkout.R
import com.worldpay.access.checkout.client.validation.listener.AccessCheckoutCardValidationListener
import com.worldpay.access.checkout.testutils.CardConfigurationUtil.Configurations.CARD_CONFIG_NO_BRAND
import com.worldpay.access.checkout.validation.filters.CvvLengthFilter
import com.worldpay.access.checkout.validation.filters.ExpiryDateLengthFilter
import com.worldpay.access.checkout.validation.filters.PanLengthFilter
import com.worldpay.access.checkout.validation.state.CardValidationStateManager
import com.worldpay.access.checkout.validation.watchers.CVVTextWatcher
import com.worldpay.access.checkout.validation.watchers.ExpiryDateTextWatcher
import com.worldpay.access.checkout.validation.watchers.PANTextWatcher
import com.worldpay.access.checkout.validation.watchers.TextWatcherFactory
import org.junit.Before
import org.junit.Test
import org.mockito.Mockito
import kotlin.test.assertEquals
import kotlin.test.assertTrue

class FieldDecoratorFactoryTest {

    private val cvvEditText = mock<EditText>()
    private val cvvEditable = mock<Editable>()

    private val panEditText = mock<EditText>()
    private val panEditable = mock<Editable>()

    private val expiryDateEditText = mock<EditText>()
    private val expiryDateEditable = mock<Editable>()

    private lateinit var fieldDecoratorFactory: FieldDecoratorFactory

    @Before
    fun setup() {
        val textWatcherFactory = TextWatcherFactory(
            accessCheckoutValidationListener = mock<AccessCheckoutCardValidationListener>(),
            validationStateManager = mock<CardValidationStateManager>()
        )

        fieldDecoratorFactory = FieldDecoratorFactory(
            textWatcherFactory = textWatcherFactory
        )

        given(panEditText.text).willReturn(panEditable)
        given(panEditable.toString()).willReturn("")

        given(expiryDateEditText.text).willReturn(expiryDateEditable)
        given(expiryDateEditable.toString()).willReturn("")

        given(cvvEditText.text).willReturn(cvvEditable)
        given(cvvEditable.toString()).willReturn("")
    }

    @Test
    fun `should add new text watchers when decorating cvv field each time`() {
        given(cvvEditText.filters).willReturn(emptyArray())

        fieldDecoratorFactory.decorateCvvField(cvvEditText, panEditText, CARD_CONFIG_NO_BRAND)

        verify(cvvEditText, never()).removeTextChangedListener(any())
        verify(cvvEditText).addTextChangedListener(Mockito.any(CVVTextWatcher::class.java))

        reset(cvvEditText)

        given(cvvEditText.filters).willReturn(emptyArray())
        given(cvvEditText.text).willReturn(cvvEditable)
        given(cvvEditable.toString()).willReturn("")

        fieldDecoratorFactory.decorateCvvField(cvvEditText, panEditText, CARD_CONFIG_NO_BRAND)

        verify(cvvEditText).removeTextChangedListener(Mockito.any(CVVTextWatcher::class.java))
        verify(cvvEditText).addTextChangedListener(Mockito.any(CVVTextWatcher::class.java))
    }

    @Test
    fun `should add hint to cvv field`() {
        given(cvvEditText.filters).willReturn(emptyArray())

        fieldDecoratorFactory.decorateCvvField(cvvEditText, panEditText, CARD_CONFIG_NO_BRAND)

        verify(cvvEditText).setHint(R.string.card_cvv_hint)
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
        given(panEditText.text).willReturn(panEditable)
        given(panEditable.toString()).willReturn("")

        fieldDecoratorFactory.decoratePanField(panEditText, cvvEditText, CARD_CONFIG_NO_BRAND)

        verify(panEditText).removeTextChangedListener(Mockito.any(PANTextWatcher::class.java))
        verify(panEditText).addTextChangedListener(Mockito.any(PANTextWatcher::class.java))
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
    fun `should add new text watchers when decorating expiry date field each time`() {
        given(expiryDateEditText.filters).willReturn(emptyArray())

        fieldDecoratorFactory.decorateExpiryDateFields(expiryDateEditText, CARD_CONFIG_NO_BRAND)

        verify(expiryDateEditText, never()).removeTextChangedListener(any())
        verify(expiryDateEditText).addTextChangedListener(Mockito.any(ExpiryDateTextWatcher::class.java))

        reset(expiryDateEditText)

        given(expiryDateEditText.filters).willReturn(emptyArray())
        given(expiryDateEditText.text).willReturn(expiryDateEditable)
        given(expiryDateEditable.toString()).willReturn("")

        fieldDecoratorFactory.decorateExpiryDateFields(expiryDateEditText, CARD_CONFIG_NO_BRAND)

        verify(expiryDateEditText).removeTextChangedListener(Mockito.any(ExpiryDateTextWatcher::class.java))
        verify(expiryDateEditText).addTextChangedListener(Mockito.any(ExpiryDateTextWatcher::class.java))
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

}
