package com.worldpay.access.checkout.api.configuration

import com.nhaarman.mockitokotlin2.mock
import com.worldpay.access.checkout.api.HttpsClient
import com.worldpay.access.checkout.api.URLFactory
import com.worldpay.access.checkout.client.api.exception.AccessCheckoutException
import com.worldpay.access.checkout.testutils.CoroutineTestRule
import java.lang.RuntimeException
import java.net.URL
import kotlin.test.assertEquals
import kotlin.test.assertTrue
import kotlin.test.fail
import kotlinx.coroutines.ExperimentalCoroutinesApi
import kotlinx.coroutines.test.runBlockingTest as runAsBlockingTest
import org.junit.Rule
import org.junit.Test
import org.mockito.BDDMockito.given

@ExperimentalCoroutinesApi
class CardConfigurationClientTest {

    @get:Rule
    var coroutinesTestRule = CoroutineTestRule()

    private val baseUrl = URL("https://some-base-url")
    private val cardConfigResource = "access-checkout/cardTypes.json"
    private val cardConfigUrl = URL("$baseUrl/$cardConfigResource")

    @Test
    fun shouldFetchCardConfigFromServer() = runAsBlockingTest {
        val httpsClient = mock<HttpsClient>()
        val urlFactory = mock<URLFactory>()
        val cardConfigurationParser = mock<CardConfigurationParser>()
        val cardConfiguration = CardConfiguration(emptyList(), DefaultCardRules.CARD_DEFAULTS)

        given(urlFactory.getURL("$baseUrl/$cardConfigResource")).willReturn(cardConfigUrl)
        given(httpsClient.doGet(cardConfigUrl, cardConfigurationParser)).willReturn(cardConfiguration)

        val cardConfigurationClient = CardConfigurationClient(
            baseUrl = baseUrl,
            httpsClient = httpsClient,
            urlFactory = urlFactory,
            cardConfigurationParser = cardConfigurationParser
        )

        val result = cardConfigurationClient.getCardConfiguration()

        assertEquals(result, cardConfiguration)
    }

    @Test
    fun shouldThrowExceptionWhenAnyExceptionIsThrown() = runAsBlockingTest {
        val httpsClient = mock<HttpsClient>()
        val urlFactory = mock<URLFactory>()
        val cardConfigurationParser = mock<CardConfigurationParser>()

        given(urlFactory.getURL("$baseUrl/$cardConfigResource")).willReturn(cardConfigUrl)
        given(httpsClient.doGet(cardConfigUrl, cardConfigurationParser)).willThrow(RuntimeException("run time exception"))

        val cardConfigurationClient = CardConfigurationClient(
            baseUrl = baseUrl,
            httpsClient = httpsClient,
            urlFactory = urlFactory,
            cardConfigurationParser = cardConfigurationParser
        )

        try {
            cardConfigurationClient.getCardConfiguration()
            fail("Expected exception but got none")
        } catch (e: Exception) {
            assertTrue { e is AccessCheckoutException }
            assertEquals("There was an error when trying to fetch the card configuration", e.message)
            assertTrue { e.cause is RuntimeException }
            assertEquals("run time exception", e.cause!!.message)
        }
    }
}
