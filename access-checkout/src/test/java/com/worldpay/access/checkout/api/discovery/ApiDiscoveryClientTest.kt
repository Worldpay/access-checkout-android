package com.worldpay.access.checkout.api.discovery

import com.worldpay.access.checkout.api.HttpsClient
import com.worldpay.access.checkout.api.serialization.PlainResponseDeserializer
import com.worldpay.access.checkout.client.api.exception.AccessCheckoutException
import com.worldpay.access.checkout.testutils.CoroutineTestRule
import java.net.URL
import kotlin.test.*
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

@ExperimentalCoroutinesApi
class ApiDiscoveryClientTest {

    @get:Rule
    var coroutinesTestRule = CoroutineTestRule()

    private val httpsClient = mock<HttpsClient>()
    private var discoverLinks: DiscoverLinks = DiscoverLinks.cardSessions

    private val baseUrl = URL("https://base.url")
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

    private lateinit var apiDiscoveryClient: ApiDiscoveryClient

    @Before
    fun setUp() {
        DiscoveryCache.results.clear()
        DiscoveryCache.responses.clear()
        apiDiscoveryClient = ApiDiscoveryClient(httpsClient)
    }

    @Test
    fun `should obtain an instance when using the default constructor`() {
        assertNotNull(ApiDiscoveryClient())
    }

    @Test
    fun `should retrieve endpoint from cache when one exists`() = runTest {
        val cacheKey = "${discoverLinks.endpoints[0].key},${discoverLinks.endpoints[1].key}"
        DiscoveryCache.results[cacheKey] = sessionsCardUrl

        val endpoint = apiDiscoveryClient.discoverEndpoint(baseUrl, discoverLinks)

        assertEquals(sessionsCardUrl, endpoint)
    }

    @Test
    fun `should save endpoint to cache when endpoint discovery is successful`() = runTest {
        given(
            httpsClient.doGet(eq(baseUrl), eq(PlainResponseDeserializer), eq(emptyMap()))
        ).willReturn(response1)

        given(
            httpsClient.doGet(eq(serviceSessionsUrl), eq(PlainResponseDeserializer), eq(discoverLinks.endpoints[1].headers))
        ).willReturn(response2)

        assertTrue { DiscoveryCache.results.isEmpty() }

        val endpoint = apiDiscoveryClient.discoverEndpoint(baseUrl, discoverLinks)

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
            httpsClient.doGet(eq(serviceSessionsUrl), eq(PlainResponseDeserializer), eq(discoverLinks.endpoints[1].headers))
        ).willReturn(response2)

        val endpoint = apiDiscoveryClient.discoverEndpoint(baseUrl, discoverLinks)

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
            httpsClient.doGet(eq(serviceSessionsUrl), eq(PlainResponseDeserializer), eq(discoverLinks.endpoints[1].headers))
        ).willReturn(response2)

        val endpoint = apiDiscoveryClient.discoverEndpoint(baseUrl, discoverLinks)

        assertEquals(sessionsCardUrl, endpoint)

        verify(httpsClient, times(2)).doGet(eq(baseUrl), eq(PlainResponseDeserializer), eq(emptyMap()))
        verify(httpsClient).doGet(eq(serviceSessionsUrl), eq(PlainResponseDeserializer), eq(discoverLinks.endpoints[1].headers))
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
            httpsClient.doGet(eq(serviceSessionsUrl), eq(PlainResponseDeserializer), eq(discoverLinks.endpoints[1].headers))
        ).willReturn(response2)

        try {
            apiDiscoveryClient.discoverEndpoint(baseUrl, discoverLinks)
            fail("Expected exception but got none")
        } catch (ace: AccessCheckoutException) {
            assertEquals("Could not discover session endpoint", ace.message)
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
            httpsClient.doGet(eq(serviceSessionsUrl), eq(PlainResponseDeserializer), eq(discoverLinks.endpoints[1].headers))
        ).willReturn(response2)

        assertTrue { DiscoveryCache.results.isEmpty() }

        apiDiscoveryClient.discoverEndpoint(baseUrl, discoverLinks)

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
            httpsClient.doGet(eq(serviceSessionsUrl), eq(PlainResponseDeserializer), eq(discoverLinks.endpoints[1].headers))
        ).willReturn(response2)

        assertTrue { DiscoveryCache.results.isEmpty() }

        val url1Discovered = apiDiscoveryClient.discoverEndpoint(baseUrl, DiscoverLinks.cardSessions)
        val url2Discovered = apiDiscoveryClient.discoverEndpoint(baseUrl, DiscoverLinks.cvcSessions)

        assertEquals(url1Discovered, sessionsCardUrl)
        assertEquals(url2Discovered, sessionsPaymentsCvcUrl)

        verify(httpsClient, times(1)).doGet(eq(baseUrl), eq(PlainResponseDeserializer), eq(emptyMap()))
        verify(httpsClient, times(1)).doGet(eq(serviceSessionsUrl), eq(PlainResponseDeserializer),
            eq(DiscoverLinks.cardSessions.endpoints[1].headers)
        )
    }
}
