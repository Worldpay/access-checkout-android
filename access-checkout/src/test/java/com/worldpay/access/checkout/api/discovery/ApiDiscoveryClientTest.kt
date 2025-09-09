package com.worldpay.access.checkout.api.discovery

import com.worldpay.access.checkout.api.HttpsClient
import com.worldpay.access.checkout.api.serialization.PlainResponseDeserializer
import com.worldpay.access.checkout.client.api.exception.AccessCheckoutException
import com.worldpay.access.checkout.testutils.CoroutineTestRule
import kotlinx.coroutines.ExperimentalCoroutinesApi
import kotlinx.coroutines.test.runTest
import org.junit.Before
import org.junit.Rule
import org.junit.Test
import org.mockito.BDDMockito.given
import org.mockito.BDDMockito.verify
import org.mockito.kotlin.eq
import org.mockito.kotlin.mock
import org.mockito.kotlin.times
import java.net.MalformedURLException
import java.net.URL
import kotlin.test.assertEquals
import kotlin.test.assertFalse
import kotlin.test.assertTrue
import kotlin.test.fail

@ExperimentalCoroutinesApi
class ApiDiscoveryClientTest {

    @get:Rule
    var coroutinesTestRule = CoroutineTestRule()

    private val httpsClient = mock<HttpsClient>()
    private var discoverLinks: DiscoverLinks = DiscoverLinks.cardSessions

    private val baseUrlAsString = "https://base.url"
    private val baseUrl = URL(baseUrlAsString)
    private val serviceSessionsUrl = URL("https://sessions.url")
    private val sessionsCardUrl = URL("http://sessions-card.url")
    private val sessionsPaymentsCvcUrl = URL("http://sessions-payments-cvc.url")

    private val response1 = """
        {
            "_links": {
                "service:payments": {
                  "href": "https://access.worldpay.com/payments"
                },
                "service:sessions": {
                  "href": "$serviceSessionsUrl"
                },
                "service:payouts": {
                  "href": "https://access.worldpay.com/payouts"
                }
            }
        }
    """

    private val response2 = """
        {
          "_links": {
            "sessions:card": {
              "href": "$sessionsCardUrl"
            },
            "sessions:apm": {
              "href": "https://access.worldpay.com/sessions/apm"
            },
            "sessions:paymentsCvc": {
              "href": "$sessionsPaymentsCvcUrl"
            }
          }
        }
    """

    @Before
    fun setUp() {
        DiscoveryCache.results.clear()
        DiscoveryCache.responses.clear()

        // Resets the singleton instance stored statically on the class
        ApiDiscoveryClient.reset()
    }

    @Test
    fun `should throw exception when initialising discovery with a malformed URL`() = runTest {
        try {
            ApiDiscoveryClient.initialise("something-else")
            fail("Expected exception but got none")
        } catch (e: AccessCheckoutException) {
            assertEquals("The base URL passed to the SDK is not a valid URL (something-else)", e.message)
        }
    }

    @Test
    fun `should throw exception when attempting to use discovery without initialising it`() = runTest {
        try {
            ApiDiscoveryClient.discoverEndpoint(discoverLinks)
            fail("Expected exception but got none")
        } catch (ace: IllegalStateException) {
            assertEquals("ApiDiscoveryClient must be initialised before using it", ace.message)
        }
    }

    @Test
    fun `should retrieve endpoint from cache when one exists`() = runTest {
        val cacheKey = "${discoverLinks.endpoints[0].key},${discoverLinks.endpoints[1].key}"
        DiscoveryCache.results[cacheKey] = sessionsCardUrl

        ApiDiscoveryClient.initialise(baseUrlAsString, httpsClient)
        val endpoint = ApiDiscoveryClient.discoverEndpoint(discoverLinks)

        assertEquals(sessionsCardUrl, endpoint)
    }

    @Test
    fun `should save endpoint to cache when endpoint discovery is successful`() = runTest {
        given(
            httpsClient.doGet(eq(baseUrl), eq(PlainResponseDeserializer), eq(emptyMap()))
        ).willReturn(response1)

        given(
            httpsClient.doGet(
                eq(serviceSessionsUrl),
                eq(PlainResponseDeserializer),
                eq(discoverLinks.endpoints[1].headers)
            )
        ).willReturn(response2)

        assertTrue { DiscoveryCache.results.isEmpty() }

        ApiDiscoveryClient.initialise(baseUrlAsString, httpsClient)
        val endpoint = ApiDiscoveryClient.discoverEndpoint(discoverLinks)

        assertEquals(sessionsCardUrl, endpoint)

        assertFalse { DiscoveryCache.results.isEmpty() }
        assertEquals(sessionsCardUrl, DiscoveryCache.getResult(discoverLinks))
    }

    @Test
    fun `should be able to return endpoint on first attempt`() = runTest {
        given(
            httpsClient.doGet(eq(baseUrl), eq(PlainResponseDeserializer), eq(emptyMap()))
        ).willReturn(response1)

        given(
            httpsClient.doGet(
                eq(serviceSessionsUrl),
                eq(PlainResponseDeserializer),
                eq(discoverLinks.endpoints[1].headers)
            )
        ).willReturn(response2)

        ApiDiscoveryClient.initialise(baseUrlAsString, httpsClient)
        val endpoint = ApiDiscoveryClient.discoverEndpoint(discoverLinks)

        assertEquals(sessionsCardUrl, endpoint)
    }

    @Test
    fun `should be able to return endpoint on second attempt`() = runTest {
        given(
            httpsClient.doGet(eq(baseUrl), eq(PlainResponseDeserializer), eq(emptyMap()))
        )
            .willThrow(RuntimeException("some exception"))
            .willReturn(response1)

        given(
            httpsClient.doGet(
                eq(serviceSessionsUrl),
                eq(PlainResponseDeserializer),
                eq(discoverLinks.endpoints[1].headers)
            )
        ).willReturn(response2)

        ApiDiscoveryClient.initialise(baseUrlAsString, httpsClient)
        val endpoint = ApiDiscoveryClient.discoverEndpoint(discoverLinks)

        assertEquals(sessionsCardUrl, endpoint)

        verify(httpsClient, times(2)).doGet(
            eq(baseUrl),
            eq(PlainResponseDeserializer),
            eq(emptyMap())
        )
        verify(httpsClient).doGet(
            eq(serviceSessionsUrl),
            eq(PlainResponseDeserializer),
            eq(discoverLinks.endpoints[1].headers)
        )
    }

    @Test
    fun `should throw exception when unable to return endpoint after second attempt`() = runTest {
        given(
            httpsClient.doGet(eq(baseUrl), eq(PlainResponseDeserializer), eq(emptyMap()))
        )
            .willThrow(RuntimeException("some exception 1"))
            .willThrow(RuntimeException("some exception 2"))
            .willReturn(response1)

        given(
            httpsClient.doGet(
                eq(serviceSessionsUrl),
                eq(PlainResponseDeserializer),
                eq(discoverLinks.endpoints[1].headers)
            )
        ).willReturn(response2)

        try {
            ApiDiscoveryClient.initialise(baseUrlAsString, httpsClient)
            ApiDiscoveryClient.discoverEndpoint(discoverLinks)
            fail("Expected exception but got none")
        } catch (ace: AccessCheckoutException) {
            assertEquals("Could not discover endpoint", ace.message)
            assertEquals("some exception 2", ace.cause?.message)
        } catch (ex: Exception) {
            fail("expected to get AccessCheckoutException but did not")
        }
    }

    @Test
    fun `should cache responses received during service discovery`() = runTest {
        given(
            httpsClient.doGet(eq(baseUrl), eq(PlainResponseDeserializer), eq(emptyMap()))
        ).willReturn(response1)

        given(
            httpsClient.doGet(
                eq(serviceSessionsUrl),
                eq(PlainResponseDeserializer),
                eq(discoverLinks.endpoints[1].headers)
            )
        ).willReturn(response2)

        assertTrue { DiscoveryCache.results.isEmpty() }

        ApiDiscoveryClient.initialise(baseUrlAsString, httpsClient)
        ApiDiscoveryClient.discoverEndpoint(discoverLinks)

        assertFalse { DiscoveryCache.results.isEmpty() }
        assertEquals(response1, DiscoveryCache.getResponse(baseUrl))
        assertEquals(response2, DiscoveryCache.getResponse(serviceSessionsUrl))
    }

    @Test
    fun `should use responses cached across multiple end points discoveries`() = runTest {
        given(
            httpsClient.doGet(eq(baseUrl), eq(PlainResponseDeserializer), eq(emptyMap()))
        ).willReturn(response1)

        given(
            httpsClient.doGet(
                eq(serviceSessionsUrl),
                eq(PlainResponseDeserializer),
                eq(discoverLinks.endpoints[1].headers)
            )
        ).willReturn(response2)

        assertTrue { DiscoveryCache.results.isEmpty() }

        ApiDiscoveryClient.initialise(baseUrlAsString, httpsClient)
        val url1Discovered =
            ApiDiscoveryClient.discoverEndpoint(DiscoverLinks.cardSessions)
        val url2Discovered = ApiDiscoveryClient.discoverEndpoint(DiscoverLinks.cvcSessions)

        assertEquals(url1Discovered, sessionsCardUrl)
        assertEquals(url2Discovered, sessionsPaymentsCvcUrl)

        verify(httpsClient, times(1)).doGet(
            eq(baseUrl),
            eq(PlainResponseDeserializer),
            eq(emptyMap())
        )
        verify(httpsClient, times(1)).doGet(
            eq(serviceSessionsUrl), eq(PlainResponseDeserializer),
            eq(DiscoverLinks.cardSessions.endpoints[1].headers)
        )
    }
}
