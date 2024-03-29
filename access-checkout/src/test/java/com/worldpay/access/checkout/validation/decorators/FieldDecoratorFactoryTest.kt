package com.worldpay.access.checkout.validation.decorators

import android.widget.EditText
import com.worldpay.access.checkout.validation.filters.AccessCheckoutInputFilterFactory
import com.worldpay.access.checkout.validation.listeners.focus.FocusChangeListenerFactory
import com.worldpay.access.checkout.validation.listeners.text.TextWatcherFactory
import kotlin.test.assertNotNull
import org.junit.Before
import org.junit.Test
import org.mockito.kotlin.given
import org.mockito.kotlin.mock
import org.mockito.kotlin.verify

class FieldDecoratorFactoryTest {

    private val cvcEditText = mock<EditText>()
    private val panEditText = mock<EditText>()
    private val expiryDateEditText = mock<EditText>()

    private val textWatcherFactory = mock<TextWatcherFactory>()
    private val focusChangeListenerFactory = mock<FocusChangeListenerFactory>()
    private val accessCheckoutInputFilterFactory = mock<AccessCheckoutInputFilterFactory>()

    private lateinit var fieldDecoratorFactory: FieldDecoratorFactory

    @Before
    fun setup() {
        fieldDecoratorFactory = FieldDecoratorFactory(
            textWatcherFactory = textWatcherFactory,
            focusChangeListenerFactory = focusChangeListenerFactory,
            accessCheckoutInputFilterFactory = accessCheckoutInputFilterFactory
        )
    }

    @Test
    fun `should get cvc field decorator when pan is not null`() {
        given(textWatcherFactory.createCvcTextWatcher()).willReturn(mock())
        given(focusChangeListenerFactory.createCvcFocusChangeListener()).willReturn(mock())
        given(accessCheckoutInputFilterFactory.getCvcLengthFilter(panEditText)).willReturn(mock())

        val decorator: CvcFieldDecorator =
            fieldDecoratorFactory.getCvcDecorator(cvcEditText, panEditText)

        assertNotNull(decorator)
        verify(textWatcherFactory).createCvcTextWatcher()
        verify(focusChangeListenerFactory).createCvcFocusChangeListener()
        verify(accessCheckoutInputFilterFactory).getCvcLengthFilter(panEditText)
    }

    @Test
    fun `should get cvc field decorator when pan is null`() {
        given(textWatcherFactory.createCvcTextWatcher()).willReturn(mock())
        given(focusChangeListenerFactory.createCvcFocusChangeListener()).willReturn(mock())
        given(accessCheckoutInputFilterFactory.getCvcLengthFilter(null)).willReturn(mock())

        val decorator: CvcFieldDecorator = fieldDecoratorFactory.getCvcDecorator(cvcEditText, null)

        assertNotNull(decorator)
        verify(textWatcherFactory).createCvcTextWatcher()
        verify(focusChangeListenerFactory).createCvcFocusChangeListener()
        verify(accessCheckoutInputFilterFactory).getCvcLengthFilter(null)
    }

    @Test
    fun `should get pan field decorator`() {
        val acceptedCardBrands = arrayOf("VISA", "AMEX")

        given(
            textWatcherFactory.createPanTextWatcher(
                panEditText = panEditText,
                cvcEditText = cvcEditText,
                acceptedCardBrands = acceptedCardBrands,
                enablePanFormatting = false

            )
        ).willReturn(mock())
        given(focusChangeListenerFactory.createPanFocusChangeListener()).willReturn(mock())
        given(accessCheckoutInputFilterFactory.getPanNumericFilter()).willReturn(mock())

        val decorator: PanFieldDecorator = fieldDecoratorFactory.getPanDecorator(
            panEditText = panEditText,
            cvcEditText = cvcEditText,
            acceptedCardBrands = acceptedCardBrands,
            enablePanFormatting = false
        )

        assertNotNull(decorator)
        verify(textWatcherFactory).createPanTextWatcher(
            panEditText = panEditText,
            cvcEditText = cvcEditText,
            acceptedCardBrands = acceptedCardBrands,
            enablePanFormatting = false
        )

        verify(focusChangeListenerFactory).createPanFocusChangeListener()
        verify(accessCheckoutInputFilterFactory).getPanNumericFilter()
    }

    @Test
    fun `should get expiry date field decorator`() {
        given(textWatcherFactory.createExpiryDateTextWatcher(expiryDateEditText)).willReturn(mock())
        given(focusChangeListenerFactory.createExpiryDateFocusChangeListener()).willReturn(mock())
        given(accessCheckoutInputFilterFactory.getExpiryDateLengthFilter()).willReturn(mock())

        val decorator: ExpiryDateFieldDecorator =
            fieldDecoratorFactory.getExpiryDateDecorator(expiryDateEditText)

        assertNotNull(decorator)
        verify(textWatcherFactory).createExpiryDateTextWatcher(expiryDateEditText)
        verify(focusChangeListenerFactory).createExpiryDateFocusChangeListener()
        verify(accessCheckoutInputFilterFactory).getExpiryDateLengthFilter()
    }
}
