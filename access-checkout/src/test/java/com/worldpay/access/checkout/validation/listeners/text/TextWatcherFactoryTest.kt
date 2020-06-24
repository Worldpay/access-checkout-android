package com.worldpay.access.checkout.validation.listeners.text

import android.widget.EditText
import com.nhaarman.mockitokotlin2.given
import com.nhaarman.mockitokotlin2.mock
import com.worldpay.access.checkout.validation.result.handler.ResultHandlerFactory
import org.junit.Before
import org.junit.Test
import kotlin.test.assertNotNull

class TextWatcherFactoryTest {

    private val resultHandlerFactory = mock<ResultHandlerFactory>()

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

        val textWatcher : PanTextWatcher = textWatcherFactory.createPanTextWatcher(cvcEditText)
        assertNotNull(textWatcher)
    }

    @Test
    fun `should get expiry date text watcher`() {
        given(resultHandlerFactory.getExpiryDateValidationResultHandler()).willReturn(mock())

        val textWatcher : ExpiryDateTextWatcher = textWatcherFactory.createExpiryDateTextWatcher(expiryDateEditText)
        assertNotNull(textWatcher)
    }

    @Test
    fun `should get cvc text watcher`() {
        given(resultHandlerFactory.getCvcValidationResultHandler()).willReturn(mock())

        val textWatcher : CvcTextWatcher = textWatcherFactory.createCvcTextWatcher()
        assertNotNull(textWatcher)
    }

}