package com.worldpay.access.checkout.validation.listeners.text

import android.widget.EditText
import com.nhaarman.mockitokotlin2.given
import com.nhaarman.mockitokotlin2.mock
import com.worldpay.access.checkout.api.configuration.CardConfiguration
import com.worldpay.access.checkout.validation.result.handler.ResultHandlerFactory
import org.junit.Before
import org.junit.Test
import kotlin.test.assertTrue

class TextWatcherFactoryTest {

    private val resultHandlerFactory = mock<ResultHandlerFactory>()
    private val cardConfiguration = mock<CardConfiguration>()

    private val cvcEditText = mock<EditText>()
    private val expiryDateEditText = mock<EditText>()

    private lateinit var textWatcherFactory: TextWatcherFactory

    @Before
    fun setup() {
        textWatcherFactory = TextWatcherFactory(resultHandlerFactory)
    }

    @Test
    fun `should get pan text watcher`() {
        given(resultHandlerFactory.getPanValidationResultHandler()).willReturn(mock())
        given(resultHandlerFactory.getCvcValidationResultHandler()).willReturn(mock())
        given(resultHandlerFactory.getBrandChangedHandler()).willReturn(mock())
        assertTrue(textWatcherFactory.createPanTextWatcher(cvcEditText) is PANTextWatcher)
    }

    @Test
    fun `should get expiry date text watcher`() {
        given(resultHandlerFactory.getExpiryDateValidationResultHandler()).willReturn(mock())
        assertTrue(textWatcherFactory.createExpiryDateTextWatcher(expiryDateEditText) is ExpiryDateTextWatcher)
    }

    @Test
    fun `should get cvc text watcher`() {
        given(resultHandlerFactory.getCvcValidationResultHandler()).willReturn(mock())
        assertTrue(textWatcherFactory.createCvcTextWatcher() is CVCTextWatcher)
    }

}
