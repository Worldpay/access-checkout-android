package com.worldpay.access.checkout.validation.controller

import com.nhaarman.mockitokotlin2.*
import com.worldpay.access.checkout.api.Callback
import com.worldpay.access.checkout.api.configuration.CardConfiguration
import com.worldpay.access.checkout.api.configuration.CardConfigurationClient
import com.worldpay.access.checkout.testutils.CardConfigurationUtil.Configurations.CARD_CONFIG_BASIC
import com.worldpay.access.checkout.testutils.CardConfigurationUtil.Configurations.CARD_CONFIG_NO_BRAND
import com.worldpay.access.checkout.validation.decorators.CvcFieldDecorator
import com.worldpay.access.checkout.validation.decorators.ExpiryDateFieldDecorator
import com.worldpay.access.checkout.validation.decorators.PanFieldDecorator
import org.junit.Before
import org.junit.Test
import kotlin.test.assertNotNull

class CardDetailsValidationControllerTest {

    private val baseUrl: String = "base url"

    private val panFieldDecorator = mock<PanFieldDecorator>()
    private val cvcFieldDecorator = mock<CvcFieldDecorator>()
    private val expiryDateFieldDecorator = mock<ExpiryDateFieldDecorator>()

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

        verify(panFieldDecorator).decorate(CARD_CONFIG_NO_BRAND)
        verify(cvcFieldDecorator).decorate(CARD_CONFIG_NO_BRAND)
        verify(expiryDateFieldDecorator).decorate(CARD_CONFIG_NO_BRAND)
    }

    @Test
    fun `should redecorate field when remote card configuration is retrieved`() {
        createAccessCheckoutValidationController()

        verify(panFieldDecorator).decorate(CARD_CONFIG_NO_BRAND)
        verify(cvcFieldDecorator).decorate(CARD_CONFIG_NO_BRAND)
        verify(expiryDateFieldDecorator).decorate(CARD_CONFIG_NO_BRAND)

        verify(cardConfigurationClient).getCardConfiguration(eq(baseUrl), callbackCaptor.capture())
        // call the callback with a successful card config
        assertNotNull(callbackCaptor.firstValue)
        callbackCaptor.firstValue.onResponse(null, CARD_CONFIG_BASIC)

        verify(panFieldDecorator).decorate(CARD_CONFIG_NO_BRAND)
        verify(cvcFieldDecorator).decorate(CARD_CONFIG_NO_BRAND)
        verify(expiryDateFieldDecorator).decorate(CARD_CONFIG_NO_BRAND)
    }

    @Test
    fun `should not do anything when remote card configuration is errors`() {
        createAccessCheckoutValidationController()

        verify(panFieldDecorator).decorate(CARD_CONFIG_NO_BRAND)
        verify(cvcFieldDecorator).decorate(CARD_CONFIG_NO_BRAND)
        verify(expiryDateFieldDecorator).decorate(CARD_CONFIG_NO_BRAND)

        verify(cardConfigurationClient).getCardConfiguration(eq(baseUrl), callbackCaptor.capture())

        // call the callback with a successful card config
        assertNotNull(callbackCaptor.firstValue)
        callbackCaptor.firstValue.onResponse(Exception(), null)

        verifyNoMoreInteractions(
            panFieldDecorator, expiryDateFieldDecorator, cvcFieldDecorator
        )
    }

    private fun createAccessCheckoutValidationController() {
        CardDetailsValidationController(
            panFieldDecorator = panFieldDecorator,
            expiryDateFieldDecorator = expiryDateFieldDecorator,
            cvcFieldDecorator = cvcFieldDecorator,
            baseUrl = baseUrl,
            cardConfigurationClient = cardConfigurationClient
        )
    }

}
