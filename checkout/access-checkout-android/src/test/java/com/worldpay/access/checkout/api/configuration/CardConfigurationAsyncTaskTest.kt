package com.worldpay.access.checkout.api.configuration

import com.nhaarman.mockitokotlin2.mock
import com.worldpay.access.checkout.api.AccessCheckoutException
import com.worldpay.access.checkout.api.Callback
import com.worldpay.access.checkout.api.HttpClient
import com.worldpay.access.checkout.api.URLFactory
import com.worldpay.access.checkout.config.CardConfigurationParser
import com.worldpay.access.checkout.model.CardConfiguration
import org.awaitility.Awaitility
import org.junit.Test
import org.junit.runner.RunWith
import org.mockito.BDDMockito.given
import org.robolectric.RobolectricTestRunner
import org.robolectric.annotation.Config
import java.net.MalformedURLException
import java.net.URL
import java.util.concurrent.TimeUnit
import kotlin.test.assertEquals
import kotlin.test.assertNotNull
import kotlin.test.assertNull
import kotlin.test.assertTrue

@RunWith(RobolectricTestRunner::class)
@Config(manifest = Config.NONE)
class CardConfigurationAsyncTaskTest {

    @Test
    fun givenEmptyURL_ThenShouldThrowAnException() {
        var asserted = false

        val callback = object : Callback<CardConfiguration> {
            override fun onResponse(error: Exception?, response: CardConfiguration?) {
                assertNotNull(error)
                assertTrue(error is AccessCheckoutException.AccessCheckoutConfigurationException)
                assertEquals("Empty URL specified", error.message)
                asserted = true
            }
        }

        val cardConfigurationAsyncTask = CardConfigurationAsyncTask(callback)

        cardConfigurationAsyncTask.execute("")

        Awaitility.await().atMost(5, TimeUnit.SECONDS).until { asserted }
    }

    @Test
    fun givenNullURL_ThenShouldThrowAnException() {
        var asserted = false

        val callback = object : Callback<CardConfiguration> {
            override fun onResponse(error: Exception?, response: CardConfiguration?) {
                assertNotNull(error)
                assertTrue(error is AccessCheckoutException.AccessCheckoutConfigurationException)
                assertEquals("Empty URL specified", error.message)
                asserted = true
            }
        }

        val cardConfigurationAsyncTask = CardConfigurationAsyncTask(callback)

        cardConfigurationAsyncTask.execute(null)

        Awaitility.await().atMost(5, TimeUnit.SECONDS).until { asserted }
    }

    @Test
    fun givenMalformedURL_ThenShouldThrowAnException() {
        var asserted = false

        val callback = object : Callback<CardConfiguration> {
            override fun onResponse(error: Exception?, response: CardConfiguration?) {
                assertNotNull(error)
                assertTrue(error is AccessCheckoutException.AccessCheckoutConfigurationException)
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
    fun givenValidURL_ThenShouldReturnCardConfiguration() {
        val cardConfiguration = CardConfiguration()

        var asserted = false

        val callback = object : Callback<CardConfiguration> {
            override fun onResponse(error: Exception?, response: CardConfiguration?) {
                assertNotNull(response)
                assertEquals(cardConfiguration, response)
                assertNull(error)
                asserted = true
            }
        }
        val baseURL = "http://localhost"

        val urlFactory = mock<URLFactory>()
        val url = URL(baseURL)
        given(urlFactory.getURL("$baseURL/access-checkout/cardConfiguration.json")).willReturn(url)
        val httpClient = mock<HttpClient>()
        val cardConfigurationParser = mock<CardConfigurationParser>()
        given(httpClient.doGet(url, cardConfigurationParser)).willReturn(cardConfiguration)
        val cardConfigurationAsyncTask = CardConfigurationAsyncTask(callback, urlFactory, httpClient,
            cardConfigurationParser
        )

        cardConfigurationAsyncTask.execute(baseURL)

        Awaitility.await().atMost(5, TimeUnit.SECONDS).until { asserted }
    }


    @Test
    fun givenSomeExceptionIsThrownByTheClient_ThenShouldReturnExceptionInTheCallback() {
        var asserted = false

        val callback = object : Callback<CardConfiguration> {
            override fun onResponse(error: Exception?, response: CardConfiguration?) {
                assertNotNull(error)
                assertTrue(error is AccessCheckoutException.AccessCheckoutConfigurationException)
                assertTrue(error.cause is AccessCheckoutException.AccessCheckoutHttpException)
                assertEquals("There was an error when trying to fetch the card configuration", error.message)
                asserted = true
            }
        }

        val baseURL = "http://localhost"

        val httpClient = mock<HttpClient>()
        val cardConfigurationParser = mock<CardConfigurationParser>()
        val urlFactory = mock<URLFactory>()
        val url = URL(baseURL)
        given(urlFactory.getURL("$baseURL/access-checkout/cardConfiguration.json")).willReturn(url)
        given(httpClient.doGet(url, cardConfigurationParser)).willThrow(AccessCheckoutException.AccessCheckoutHttpException("Something went wrong!", null))
        val cardConfigurationAsyncTask = CardConfigurationAsyncTask(callback, urlFactory, httpClient, cardConfigurationParser)

        cardConfigurationAsyncTask.execute(baseURL)

        Awaitility.await().atMost(5, TimeUnit.SECONDS).until { asserted }
    }


}