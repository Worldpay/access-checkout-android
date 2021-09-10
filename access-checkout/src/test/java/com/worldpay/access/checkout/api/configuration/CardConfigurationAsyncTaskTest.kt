package com.worldpay.access.checkout.api.configuration

import android.os.Looper.getMainLooper
import com.nhaarman.mockitokotlin2.mock
import com.worldpay.access.checkout.api.Callback
import com.worldpay.access.checkout.api.HttpsClient
import com.worldpay.access.checkout.api.URLFactory
import com.worldpay.access.checkout.client.api.exception.AccessCheckoutException
import com.worldpay.access.checkout.testutils.CardConfigurationUtil.Configurations.CARD_CONFIG_NO_BRAND
import java.net.MalformedURLException
import java.net.URL
import kotlin.test.assertEquals
import kotlin.test.assertNotNull
import kotlin.test.assertNull
import kotlin.test.assertTrue
import org.junit.Test
import org.junit.runner.RunWith
import org.mockito.BDDMockito.given
import org.robolectric.RobolectricTestRunner
import org.robolectric.Shadows.shadowOf
import org.robolectric.annotation.LooperMode
import org.robolectric.annotation.LooperMode.Mode.PAUSED

@RunWith(RobolectricTestRunner::class)
@LooperMode(PAUSED)
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

        cardConfigurationAsyncTask.execute("").get()
        shadowOf(getMainLooper()).idle()

        assertTrue { asserted }
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

        cardConfigurationAsyncTask.execute(null).get()
        shadowOf(getMainLooper()).idle()

        assertTrue { asserted }
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

        cardConfigurationAsyncTask.execute("abc").get()
        shadowOf(getMainLooper()).idle()

        assertTrue { asserted }
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
        val httpClient = mock<HttpsClient>()
        val cardConfigurationParser = mock<CardConfigurationParser>()
        given(httpClient.doGet(url, cardConfigurationParser)).willReturn(CARD_CONFIG_NO_BRAND)
        val cardConfigurationAsyncTask = CardConfigurationAsyncTask(
            callback, urlFactory, httpClient,
            cardConfigurationParser
        )

        cardConfigurationAsyncTask.execute(baseURL).get()
        shadowOf(getMainLooper()).idle()

        assertTrue { asserted }
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

        val httpClient = mock<HttpsClient>()
        val cardConfigurationParser = mock<CardConfigurationParser>()
        val urlFactory = mock<URLFactory>()
        val url = URL(baseURL)
        given(urlFactory.getURL("$baseURL/access-checkout/cardTypes.json")).willReturn(url)
        given(httpClient.doGet(url, cardConfigurationParser)).willThrow(expectedException)

        val cardConfigurationAsyncTask = CardConfigurationAsyncTask(callback, urlFactory, httpClient, cardConfigurationParser)
        cardConfigurationAsyncTask.execute(baseURL).get()
        shadowOf(getMainLooper()).idle()

        assertTrue { asserted }
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

        cardConfigurationAsyncTask.execute().get()
        shadowOf(getMainLooper()).idle()

        assertTrue { asserted }
    }
}
