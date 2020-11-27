package com.worldpay.access.checkout.api.configuration

import com.nhaarman.mockitokotlin2.mock
import com.worldpay.access.checkout.api.Callback
import com.worldpay.access.checkout.api.HttpClient
import com.worldpay.access.checkout.api.URLFactory
import com.worldpay.access.checkout.client.api.exception.AccessCheckoutException
import com.worldpay.access.checkout.testutils.CardConfigurationUtil.Configurations.CARD_CONFIG_NO_BRAND
import org.awaitility.Awaitility
import org.junit.Test
import org.junit.runner.RunWith
import org.mockito.BDDMockito.given
import org.robolectric.RobolectricTestRunner
import java.net.MalformedURLException
import java.net.URL
import java.util.concurrent.TimeUnit
import kotlin.test.assertEquals
import kotlin.test.assertNotNull
import kotlin.test.assertNull
import kotlin.test.assertTrue

@RunWith(RobolectricTestRunner::class)
class CardConfigurationAsyncTaskTest {

    @Test
    fun `should throw an exception where url is empty`() {
        var asserted = false

        val callback = object : Callback<CardConfiguration> {
            override fun onResponse(error: Exception?, response: CardConfiguration?) {
                assertNotNull(error)
                assertTrue(error is AccessCheckoutException)
                assertEquals("Empty URL specified", error.message)
                asserted = true
            }
        }

        val cardConfigurationAsyncTask = CardConfigurationAsyncTask(callback)

        cardConfigurationAsyncTask.execute("")

        Awaitility.await().atMost(5, TimeUnit.SECONDS).until { asserted }
    }

    @Test
    fun `should throw an exception where url is null`() {
        var asserted = false

        val callback = object : Callback<CardConfiguration> {
            override fun onResponse(error: Exception?, response: CardConfiguration?) {
                assertNotNull(error)
                assertTrue(error is AccessCheckoutException)
                assertEquals("Empty URL specified", error.message)
                asserted = true
            }
        }

        val cardConfigurationAsyncTask = CardConfigurationAsyncTask(callback)

        cardConfigurationAsyncTask.execute(null)

        Awaitility.await().atMost(5, TimeUnit.SECONDS).until { asserted }
    }

    @Test
    fun `should throw an exception where url is malformed`() {
        var asserted = false

        val callback = object : Callback<CardConfiguration> {
            override fun onResponse(error: Exception?, response: CardConfiguration?) {
                assertNotNull(error)
                assertTrue(error is AccessCheckoutException)
                assertTrue(error.cause is MalformedURLException)
                assertEquals("Invalid URL specified", error.message)
                asserted = true
            }
        }

        val cardConfigurationAsyncTask = CardConfigurationAsyncTask(callback)

        cardConfigurationAsyncTask.execute("abc")

        Awaitility.await().atMost(5, TimeUnit.SECONDS).until { asserted }
    }

    @Test
    fun `should return card configuration given correct url`() {
        var asserted = false

        val callback = object : Callback<CardConfiguration> {
            override fun onResponse(error: Exception?, response: CardConfiguration?) {
                assertNotNull(response)
                assertEquals(CARD_CONFIG_NO_BRAND, response)
                assertNull(error)
                asserted = true
            }
        }
        val baseURL = "https://localhost:8443"

        val urlFactory = mock<URLFactory>()
        val url = URL(baseURL)
        given(urlFactory.getURL("$baseURL/access-checkout/cardTypes.json")).willReturn(url)
        val httpClient = mock<HttpClient>()
        val cardConfigurationParser = mock<CardConfigurationParser>()
        given(httpClient.doGet(url, cardConfigurationParser)).willReturn(CARD_CONFIG_NO_BRAND)
        val cardConfigurationAsyncTask = CardConfigurationAsyncTask(callback, urlFactory, httpClient,
            cardConfigurationParser
        )

        cardConfigurationAsyncTask.execute(baseURL)

        Awaitility.await().atMost(5, TimeUnit.SECONDS).until { asserted }
    }

    @Test
    fun `should throw exception where exception is thrown by the client`() {
        var asserted = false
        val expectedException = AccessCheckoutException("Something went wrong!")

        val callback = object : Callback<CardConfiguration> {
            override fun onResponse(error: Exception?, response: CardConfiguration?) {
                assertEquals(expectedException, error)
                asserted = true
            }
        }

        val baseURL = "https://localhost:8443"

        val httpClient = mock<HttpClient>()
        val cardConfigurationParser = mock<CardConfigurationParser>()
        val urlFactory = mock<URLFactory>()
        val url = URL(baseURL)
        given(urlFactory.getURL("$baseURL/access-checkout/cardTypes.json")).willReturn(url)
        given(httpClient.doGet(url, cardConfigurationParser)).willThrow(expectedException)

        val cardConfigurationAsyncTask = CardConfigurationAsyncTask(callback, urlFactory, httpClient, cardConfigurationParser)
        cardConfigurationAsyncTask.execute(baseURL)

        Awaitility.await().atMost(5, TimeUnit.SECONDS).until { asserted }
    }

    @Test
    fun `should throw an exception where no parameters are passed to execute`() {
        var asserted = false

        val callback = object : Callback<CardConfiguration> {
            override fun onResponse(error: Exception?, response: CardConfiguration?) {
                assertNotNull(error)
                assertTrue(error is AccessCheckoutException)
                assertEquals("There was an error when trying to fetch the card configuration", error.message)
                assertEquals(0, error.validationRules.size)
                assertTrue(error.cause is ArrayIndexOutOfBoundsException)
                asserted = true
            }
        }

        val cardConfigurationAsyncTask = CardConfigurationAsyncTask(callback)

        cardConfigurationAsyncTask.execute()

        Awaitility.await().atMost(5, TimeUnit.SECONDS).until { asserted }
    }



}
