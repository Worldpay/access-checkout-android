package com.worldpay.access.checkout.validation.controller

import android.widget.EditText
import com.nhaarman.mockitokotlin2.*
import com.worldpay.access.checkout.api.Callback
import com.worldpay.access.checkout.api.configuration.CardConfiguration
import com.worldpay.access.checkout.api.configuration.CardConfigurationClient
import com.worldpay.access.checkout.testutils.CardConfigurationUtil.Configurations.CARD_CONFIG_BASIC
import com.worldpay.access.checkout.testutils.CardConfigurationUtil.Configurations.CARD_CONFIG_NO_BRAND
import org.junit.Before
import org.junit.Test
import kotlin.test.assertNotNull

class CardDetailsValidationControllerTest {

    private val baseUrl: String = "base url"

    // fields
    private val pan = mock<EditText>()
    private val expiryMonth = mock<EditText>()
    private val expiryYear = mock<EditText>()
    private val cvv = mock<EditText>()

    private val fieldDecoratorFactory = mock<FieldDecoratorFactory>()

    private val cardConfigurationClient = mock<CardConfigurationClient>()

    private lateinit var callbackCaptor: KArgumentCaptor<Callback<CardConfiguration>>

    @Before
    fun setup() {
        callbackCaptor = argumentCaptor()
    }

    @Test
    fun `should make call to retrieve remote card configuration upon initialisation`() {
        createAccessCheckoutValidationController()

        verify(cardConfigurationClient).getCardConfiguration(eq(baseUrl), callbackCaptor.capture())

        assertNotNull(callbackCaptor.firstValue)
    }

    @Test
    fun `should decorate each field that is passed in upon initialisation`() {
        createAccessCheckoutValidationController()

        verify(fieldDecoratorFactory).decorateCvvField(cvv, pan, CARD_CONFIG_NO_BRAND)
        verify(fieldDecoratorFactory).decoratePanField(pan, cvv, CARD_CONFIG_NO_BRAND)
        verify(fieldDecoratorFactory).decorateExpMonthField(expiryMonth, CARD_CONFIG_NO_BRAND)
        verify(fieldDecoratorFactory).decorateExpYearField(expiryYear, CARD_CONFIG_NO_BRAND)
    }

    @Test
    fun `should redecorate field when remote card configuration is retrieved`() {
        createAccessCheckoutValidationController()

        verify(fieldDecoratorFactory).decorateCvvField(cvv, pan, CARD_CONFIG_NO_BRAND)
        verify(fieldDecoratorFactory).decoratePanField(pan, cvv, CARD_CONFIG_NO_BRAND)
        verify(fieldDecoratorFactory).decorateExpMonthField(expiryMonth, CARD_CONFIG_NO_BRAND)
        verify(fieldDecoratorFactory).decorateExpYearField(expiryYear, CARD_CONFIG_NO_BRAND)

        verify(cardConfigurationClient).getCardConfiguration(eq(baseUrl), callbackCaptor.capture())
        // call the callback with a successful card config
        assertNotNull(callbackCaptor.firstValue)
        callbackCaptor.firstValue.onResponse(null, CARD_CONFIG_BASIC)

        verify(fieldDecoratorFactory).decorateCvvField(cvv, pan, CARD_CONFIG_BASIC)
        verify(fieldDecoratorFactory).decoratePanField(pan, cvv, CARD_CONFIG_BASIC)
        verify(fieldDecoratorFactory).decorateExpMonthField(expiryMonth, CARD_CONFIG_BASIC)
        verify(fieldDecoratorFactory).decorateExpYearField(expiryYear, CARD_CONFIG_BASIC)
    }

    @Test
    fun `should not do anything when remote card configuration is errors`() {
        createAccessCheckoutValidationController()

        verify(fieldDecoratorFactory).decorateCvvField(cvv, pan, CARD_CONFIG_NO_BRAND)
        verify(fieldDecoratorFactory).decoratePanField(pan, cvv, CARD_CONFIG_NO_BRAND)
        verify(fieldDecoratorFactory).decorateExpMonthField(expiryMonth, CARD_CONFIG_NO_BRAND)
        verify(fieldDecoratorFactory).decorateExpYearField(expiryYear, CARD_CONFIG_NO_BRAND)

        verify(cardConfigurationClient).getCardConfiguration(eq(baseUrl), callbackCaptor.capture())

        // call the callback with a successful card config
        assertNotNull(callbackCaptor.firstValue)
        callbackCaptor.firstValue.onResponse(Exception(), null)

        verifyNoMoreInteractions(
            fieldDecoratorFactory,
            pan,
            expiryMonth,
            expiryYear,
            cvv
        )
    }

    private fun createAccessCheckoutValidationController() {
        CardDetailsValidationController(
            panEditText = pan,
            expiryMonthEditText = expiryMonth,
            expiryYearEditText = expiryYear,
            cvvEditText = cvv,
            baseUrl = baseUrl,
            cardConfigurationClient = cardConfigurationClient,
            fieldDecoratorFactory = fieldDecoratorFactory
        )
    }

}
