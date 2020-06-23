package com.worldpay.access.checkout.validation.decorators

import android.widget.EditText
import com.nhaarman.mockitokotlin2.mock
import com.worldpay.access.checkout.client.validation.listener.AccessCheckoutCardValidationListener
import com.worldpay.access.checkout.validation.filters.LengthFilterFactory
import com.worldpay.access.checkout.validation.listeners.focus.FocusChangeListenerFactory
import com.worldpay.access.checkout.validation.listeners.text.TextWatcherFactory
import com.worldpay.access.checkout.validation.result.handler.ResultHandlerFactory
import com.worldpay.access.checkout.validation.result.state.CardValidationStateManager
import org.junit.Before
import org.junit.Test
import kotlin.test.assertNotNull

class FieldDecoratorFactoryTest {

    private val cvcEditText = mock<EditText>()
    private val panEditText = mock<EditText>()
    private val expiryDateEditText = mock<EditText>()

    private val resultHandlerFactory = ResultHandlerFactory(mock<AccessCheckoutCardValidationListener>(), mock<CardValidationStateManager>())
    private val textWatcherFactory = TextWatcherFactory(resultHandlerFactory)
    private val focusChangeListenerFactory = FocusChangeListenerFactory(resultHandlerFactory)
    private val lengthFilterFactory = LengthFilterFactory()

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
        val decorator: CvcFieldDecorator = fieldDecoratorFactory.getCvcDecorator(cvcEditText, panEditText)
        assertNotNull(decorator)
    }

    @Test
    fun `should get cvc field decorator when pan is null`() {
        val decorator: CvcFieldDecorator = fieldDecoratorFactory.getCvcDecorator(cvcEditText, null)
        assertNotNull(decorator)
    }

    @Test
    fun `should get pan field decorator`() {
        val decorator: PanFieldDecorator = fieldDecoratorFactory.getPanDecorator(panEditText, cvcEditText)
        assertNotNull(decorator)
    }

    @Test
    fun `should get expiry date field decorator`() {
        val decorator: ExpiryDateFieldDecorator = fieldDecoratorFactory.getExpiryDateDecorator(expiryDateEditText)
        assertNotNull(decorator)
    }

}
