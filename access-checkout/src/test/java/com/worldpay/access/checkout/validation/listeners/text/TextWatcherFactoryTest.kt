package com.worldpay.access.checkout.validation.listeners.text

import com.nhaarman.mockitokotlin2.given
import com.nhaarman.mockitokotlin2.mock
import com.worldpay.access.checkout.ui.AccessEditText
import com.worldpay.access.checkout.validation.result.handler.ResultHandlerFactory
import kotlin.test.assertNotNull
import org.junit.Before
import org.junit.Test

class TextWatcherFactoryTest {

    private val resultHandlerFactory = mock<ResultHandlerFactory>()

    private val cvcEditText = mock<AccessEditText>()
    private val panEditText = mock<AccessEditText>()
    private val expiryDateEditText = mock<AccessEditText>()

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

        val textWatcher: PanTextWatcher = textWatcherFactory.createPanTextWatcher(
            panAccessEditText = panEditText,
            cvcAccessEditText = cvcEditText,
            acceptedCardBrands = emptyArray(),
            enablePanFormatting = false
        )
        assertNotNull(textWatcher)
    }

    @Test
    fun `should get expiry date text watcher`() {
        given(resultHandlerFactory.getExpiryDateValidationResultHandler()).willReturn(mock())

        val textWatcher: ExpiryDateTextWatcher = textWatcherFactory.createExpiryDateTextWatcher(expiryDateEditText)
        assertNotNull(textWatcher)
    }

    @Test
    fun `should get cvc text watcher`() {
        given(resultHandlerFactory.getCvcValidationResultHandler()).willReturn(mock())

        val textWatcher: CvcTextWatcher = textWatcherFactory.createCvcTextWatcher()
        assertNotNull(textWatcher)
    }
}
