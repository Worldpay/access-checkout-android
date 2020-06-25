package com.worldpay.access.checkout.validation.configuration

import com.nhaarman.mockitokotlin2.*
import com.worldpay.access.checkout.api.Callback
import com.worldpay.access.checkout.api.configuration.CardConfiguration
import com.worldpay.access.checkout.api.configuration.CardConfigurationClient
import com.worldpay.access.checkout.testutils.CardConfigurationUtil.Configurations.CARD_CONFIG_BASIC
import com.worldpay.access.checkout.testutils.CardConfigurationUtil.Configurations.CARD_CONFIG_NO_BRAND
import com.worldpay.access.checkout.testutils.CardConfigurationUtil.mockSuccessfulCardConfiguration
import org.junit.Before
import org.junit.Test
import kotlin.test.assertEquals

class CardConfigurationProviderTest {

    private val baseUrl = "localhost"
    private val cardConfigurationClient = mock<CardConfigurationClient>()

    private lateinit var cardConfigurationProvider: CardConfigurationProvider

    @Before
    fun setup() {
        cardConfigurationProvider = CardConfigurationProvider(
            baseUrl = baseUrl,
            cardConfigurationClient = cardConfigurationClient,
            observers = emptyList()
        )
    }

    @Test
    fun `should be getting default card configuration initially`() {
        assertEquals(CARD_CONFIG_NO_BRAND, CardConfigurationProvider.getCardConfiguration())
    }

    @Test
    fun `should reset back to default card configuration each time a provider is created despite having remote card configuration before`() {
        assertEquals(CARD_CONFIG_NO_BRAND, CardConfigurationProvider.getCardConfiguration())

        mockSuccessfulCardConfiguration()

        assertEquals(CARD_CONFIG_BASIC, CardConfigurationProvider.getCardConfiguration())

        CardConfigurationProvider(
            baseUrl = baseUrl,
            cardConfigurationClient = cardConfigurationClient,
            observers = emptyList()
        )

        assertEquals(CARD_CONFIG_NO_BRAND, CardConfigurationProvider.getCardConfiguration())
    }

    @Test
    fun `should be getting remote card configuration once loaded`() {
        assertEquals(CARD_CONFIG_NO_BRAND, CardConfigurationProvider.getCardConfiguration())

        val cardConfigurationClient = mock<CardConfigurationClient>()
        val baseUrl = "http://localhost-mock:8080"
        val captor = argumentCaptor<Callback<CardConfiguration>>()

        CardConfigurationProvider(baseUrl, cardConfigurationClient, emptyList())

        verify(cardConfigurationClient).getCardConfiguration(eq(baseUrl), captor.capture())

        captor.firstValue.onResponse(null, CARD_CONFIG_BASIC)

        assertEquals(CARD_CONFIG_BASIC, CardConfigurationProvider.getCardConfiguration())
    }

    @Test
    fun `should be calling all observers once remote card configuration is retrieved`() {
        assertEquals(CARD_CONFIG_NO_BRAND, CardConfigurationProvider.getCardConfiguration())

        val cardConfigObserver = mock<CardConfigurationObserver>()
        val cardConfigurationClient = mock<CardConfigurationClient>()
        val baseUrl = "http://localhost-mock:8080"
        val captor = argumentCaptor<Callback<CardConfiguration>>()

        CardConfigurationProvider(baseUrl, cardConfigurationClient, listOf(cardConfigObserver))

        verify(cardConfigurationClient).getCardConfiguration(eq(baseUrl), captor.capture())

        captor.firstValue.onResponse(null, CARD_CONFIG_BASIC)

        assertEquals(CARD_CONFIG_BASIC, CardConfigurationProvider.getCardConfiguration())
        verify(cardConfigObserver).update()
    }

    @Test
    fun `should do nothing when remote card configuration call fails`() {
        assertEquals(CARD_CONFIG_NO_BRAND, CardConfigurationProvider.getCardConfiguration())

        val cardConfigObserver = mock<CardConfigurationObserver>()
        val cardConfigurationClient = mock<CardConfigurationClient>()
        val baseUrl = "http://localhost-mock:8080"
        val captor = argumentCaptor<Callback<CardConfiguration>>()

        CardConfigurationProvider(baseUrl, cardConfigurationClient, listOf(cardConfigObserver))

        verify(cardConfigurationClient).getCardConfiguration(eq(baseUrl), captor.capture())

        captor.firstValue.onResponse(IllegalArgumentException(), null)

        assertEquals(CARD_CONFIG_NO_BRAND, CardConfigurationProvider.getCardConfiguration())
        verify(cardConfigObserver, never()).update()
    }

}
