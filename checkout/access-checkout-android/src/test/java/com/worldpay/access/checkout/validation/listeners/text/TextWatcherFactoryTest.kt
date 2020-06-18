package com.worldpay.access.checkout.validation.listeners.text

import android.widget.EditText
import com.nhaarman.mockitokotlin2.given
import com.nhaarman.mockitokotlin2.mock
import com.worldpay.access.checkout.api.configuration.CardConfiguration
import com.worldpay.access.checkout.validation.result.ResultHandlerFactory
import org.junit.Before
import org.junit.Test
import kotlin.test.assertTrue

class TextWatcherFactoryTest {

    private val resultHandlerFactory = mock<ResultHandlerFactory>()
    private val cardConfiguration = mock<CardConfiguration>()

    private val cvvEditText = mock<EditText>()
    private val expiryDateEditText = mock<EditText>()

    private lateinit var textWatcherFactory: TextWatcherFactory

    @Before
    fun setup() {
        textWatcherFactory = TextWatcherFactory(resultHandlerFactory)
    }

    @Test
    fun `should get pan text watcher`() {
        given(resultHandlerFactory.getPanValidationResultHandler()).willReturn(mock())
        given(resultHandlerFactory.getCvvValidationResultHandler()).willReturn(mock())
        given(resultHandlerFactory.getBrandChangedHandler()).willReturn(mock())
        assertTrue(textWatcherFactory.createPanTextWatcher(cvvEditText, cardConfiguration) is PANTextWatcher)
    }

    @Test
    fun `should get expiry date text watcher`() {
        given(resultHandlerFactory.getExpiryDateValidationResultHandler()).willReturn(mock())
        assertTrue(textWatcherFactory.createExpiryDateTextWatcher(expiryDateEditText) is ExpiryDateTextWatcher)
    }

    @Test
    fun `should get cvv text watcher`() {
        given(resultHandlerFactory.getCvvValidationResultHandler()).willReturn(mock())
        assertTrue(textWatcherFactory.createCvvTextWatcher() is CVVTextWatcher)
    }

}
