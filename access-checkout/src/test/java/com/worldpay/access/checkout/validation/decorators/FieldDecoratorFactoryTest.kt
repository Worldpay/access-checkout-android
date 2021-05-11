package com.worldpay.access.checkout.validation.decorators

import android.widget.EditText
import com.nhaarman.mockitokotlin2.mock
import com.worldpay.access.checkout.validation.filters.LengthFilterFactory
import com.worldpay.access.checkout.validation.listeners.focus.FocusChangeListenerFactory
import com.worldpay.access.checkout.validation.listeners.text.TextWatcherFactory
import org.junit.Before
import org.junit.Test
import org.mockito.BDDMockito.given
import org.mockito.BDDMockito.verify
import kotlin.test.assertNotNull

class FieldDecoratorFactoryTest {

    private val cvcEditText = mock<EditText>()
    private val panEditText = mock<EditText>()
    private val expiryDateEditText = mock<EditText>()

    private val textWatcherFactory = mock<TextWatcherFactory>()
    private val focusChangeListenerFactory = mock<FocusChangeListenerFactory>()
    private val lengthFilterFactory = mock<LengthFilterFactory>()

    private lateinit var fieldDecoratorFactory: FieldDecoratorFactory

    @Before
    fun setup() {
        fieldDecoratorFactory = FieldDecoratorFactory(
            textWatcherFactory = textWatcherFactory,
            focusChangeListenerFactory = focusChangeListenerFactory,
            lengthFilterFactory = lengthFilterFactory
        )
    }

    @Test
    fun `should get cvc field decorator when pan is not null`() {
        given(textWatcherFactory.createCvcTextWatcher()).willReturn(mock())
        given(focusChangeListenerFactory.createCvcFocusChangeListener()).willReturn(mock())
        given(lengthFilterFactory.getCvcLengthFilter(panEditText)).willReturn(mock())

        val decorator: CvcFieldDecorator = fieldDecoratorFactory.getCvcDecorator(cvcEditText, panEditText)

        assertNotNull(decorator)
        verify(textWatcherFactory).createCvcTextWatcher()
        verify(focusChangeListenerFactory).createCvcFocusChangeListener()
        verify(lengthFilterFactory).getCvcLengthFilter(panEditText)
    }

    @Test
    fun `should get cvc field decorator when pan is null`() {
        given(textWatcherFactory.createCvcTextWatcher()).willReturn(mock())
        given(focusChangeListenerFactory.createCvcFocusChangeListener()).willReturn(mock())
        given(lengthFilterFactory.getCvcLengthFilter(null)).willReturn(mock())

        val decorator: CvcFieldDecorator = fieldDecoratorFactory.getCvcDecorator(cvcEditText, null)

        assertNotNull(decorator)
        verify(textWatcherFactory).createCvcTextWatcher()
        verify(focusChangeListenerFactory).createCvcFocusChangeListener()
        verify(lengthFilterFactory).getCvcLengthFilter(null)
    }

    @Test
    fun `should get pan field decorator`() {
        val acceptedCardBrands = arrayOf("VISA", "AMEX")

        given(textWatcherFactory.createPanTextWatcher(
            panEditText = panEditText,
            cvcEditText = cvcEditText,
            acceptedCardBrands = acceptedCardBrands,
            disablePanFormatting = false

        )).willReturn(mock())
        given(focusChangeListenerFactory.createPanFocusChangeListener()).willReturn(mock())
        given(lengthFilterFactory.getPanLengthFilter(false)).willReturn(mock())

        val decorator: PanFieldDecorator = fieldDecoratorFactory.getPanDecorator(
            panEditText = panEditText,
            cvcEditText = cvcEditText,
            acceptedCardBrands = acceptedCardBrands,
            disablePanFormatting = false
        )

        assertNotNull(decorator)
        verify(textWatcherFactory).createPanTextWatcher(
            panEditText = panEditText,
            cvcEditText = cvcEditText,
            acceptedCardBrands = acceptedCardBrands,
            disablePanFormatting = false
        )

        verify(focusChangeListenerFactory).createPanFocusChangeListener()
        verify(lengthFilterFactory).getPanLengthFilter(false)
    }

    @Test
    fun `should get expiry date field decorator`() {
        given(textWatcherFactory.createExpiryDateTextWatcher(expiryDateEditText)).willReturn(mock())
        given(focusChangeListenerFactory.createExpiryDateFocusChangeListener()).willReturn(mock())
        given(lengthFilterFactory.getExpiryDateLengthFilter()).willReturn(mock())

        val decorator: ExpiryDateFieldDecorator = fieldDecoratorFactory.getExpiryDateDecorator(expiryDateEditText)

        assertNotNull(decorator)
        verify(textWatcherFactory).createExpiryDateTextWatcher(expiryDateEditText)
        verify(focusChangeListenerFactory).createExpiryDateFocusChangeListener()
        verify(lengthFilterFactory).getExpiryDateLengthFilter()
    }

}
