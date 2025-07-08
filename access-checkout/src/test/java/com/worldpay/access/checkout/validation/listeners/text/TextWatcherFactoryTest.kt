package com.worldpay.access.checkout.validation.listeners.text

import android.widget.EditText
import com.worldpay.access.checkout.util.BaseUrlProvider
import com.worldpay.access.checkout.validation.result.handler.ResultHandlerFactory
import org.junit.Before
import org.junit.Test
import org.mockito.kotlin.given
import org.mockito.kotlin.mock
import org.mockito.kotlin.spy
import org.mockito.kotlin.verify
import org.mockito.kotlin.verifyNoInteractions
import kotlin.test.assertEquals
import kotlin.test.assertNotNull

class TextWatcherFactoryTest {

    private val resultHandlerFactory = mock<ResultHandlerFactory>()

    private val cvcEditText = mock<EditText>()
    private val panEditText = mock<EditText>()
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
        given(resultHandlerFactory.getBrandsChangedHandler()).willReturn(mock())

        val textWatcher: PanTextWatcher = textWatcherFactory.createPanTextWatcher(
            panEditText = panEditText,
            cvcEditText = cvcEditText,
            acceptedCardBrands = emptyArray(),
            enablePanFormatting = false,
            checkoutId = "testCheckoutId"
        )
        assertNotNull(textWatcher)
    }

    @Test
    fun `should use CARD_BIN_SERVICE baseUrl when checkoutId is not AUTOMATED-TEST`() {
        given(resultHandlerFactory.getPanValidationResultHandler()).willReturn(mock())
        given(resultHandlerFactory.getCvcValidationResultHandler()).willReturn(mock())
        given(resultHandlerFactory.getBrandsChangedHandler()).willReturn(mock())

        val baseUrlProviderSpy = spy(BaseUrlProvider.instance)
        BaseUrlProvider.instance = baseUrlProviderSpy

        val textWatcher = textWatcherFactory.createPanTextWatcher(
            panEditText = panEditText,
            cvcEditText = cvcEditText,
            acceptedCardBrands = emptyArray(),
            enablePanFormatting = false,
            checkoutId = "testCheckoutId"
        )
        assertNotNull(textWatcher)
        verify(baseUrlProviderSpy).CARD_BIN_SERVICE
    }

    @Test
    fun `should use default CARD_BIN_SERVICE baseUrl when checkoutId is AUTOMATED-TEST`() {
        given(resultHandlerFactory.getPanValidationResultHandler()).willReturn(mock())
        given(resultHandlerFactory.getCvcValidationResultHandler()).willReturn(mock())
        given(resultHandlerFactory.getBrandsChangedHandler()).willReturn(mock())

        val baseUrlProviderSpy = spy(BaseUrlProvider.instance)
        BaseUrlProvider.instance = baseUrlProviderSpy

        val textWatcher = textWatcherFactory.createPanTextWatcher(
            panEditText = panEditText,
            cvcEditText = cvcEditText,
            acceptedCardBrands = emptyArray(),
            enablePanFormatting = false,
            checkoutId = "AUTOMATED-TEST"
        )
        assertNotNull(textWatcher)
        verifyNoInteractions(baseUrlProviderSpy)
    }


    @Test
    fun `should get expiry date text watcher`() {
        given(resultHandlerFactory.getExpiryDateValidationResultHandler()).willReturn(mock())

        val textWatcher: ExpiryDateTextWatcher =
            textWatcherFactory.createExpiryDateTextWatcher(expiryDateEditText)
        assertNotNull(textWatcher)
    }

    @Test
    fun `should get cvc text watcher`() {
        given(resultHandlerFactory.getCvcValidationResultHandler()).willReturn(mock())

        val textWatcher: CvcTextWatcher = textWatcherFactory.createCvcTextWatcher()
        assertNotNull(textWatcher)
    }

}
