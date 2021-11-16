package com.worldpay.access.checkout.api.discovery

import com.nhaarman.mockitokotlin2.any
import com.nhaarman.mockitokotlin2.eq
import com.nhaarman.mockitokotlin2.mock
import com.nhaarman.mockitokotlin2.times
import com.worldpay.access.checkout.api.HttpsClient
import com.worldpay.access.checkout.api.serialization.Deserializer
import com.worldpay.access.checkout.client.api.exception.AccessCheckoutException
import com.worldpay.access.checkout.session.api.SessionRequestService
import com.worldpay.access.checkout.testutils.CoroutineTestRule
import java.net.URL
import kotlinx.coroutines.ExperimentalCoroutinesApi
import kotlinx.coroutines.test.runBlockingTest as runAsBlockingTest
import org.junit.Before
import org.junit.Rule
import org.junit.Test
import org.mockito.BDDMockito.given
import org.mockito.BDDMockito.verify
import kotlin.test.*

@ExperimentalCoroutinesApi
class ApiDiscoveryClientTest {

    @get:Rule
    var coroutinesTestRule = CoroutineTestRule()

    private val httpsClient = mock<HttpsClient>()
    private var discoverLinks: DiscoverLinks = DiscoverLinks.verifiedTokens

    private val baseUrl = URL("https://base.url.com")
    private val firstEndpoint = URL("https://endpoint.1.com")
    private val expectedEndpoint = URL("http://expected.endpoint.url")

    private lateinit var apiDiscoveryClient: ApiDiscoveryClient

    @Before
    fun setUp() {
        DiscoveryCache.results.clear()
        apiDiscoveryClient = ApiDiscoveryClient(httpsClient)
    }

    @Test
    fun `should obtain an instance when using the default constructor`() {
        assertNotNull(ApiDiscoveryClient())
    }

    @Test
    fun `should retrieve endpoint from cache when one exists`() = runAsBlockingTest {
        DiscoveryCache.results[discoverLinks.endpoints[0].endpoint] = expectedEndpoint

        val endpoint = apiDiscoveryClient.discoverEndpoint(baseUrl, discoverLinks)

        assertEquals(expectedEndpoint, endpoint)
    }

    @Test
    fun `should save endpoint to cache when endpoint discovery is successful`() = runAsBlockingTest {
        given(
            httpsClient.doGet(eq(baseUrl), any<Deserializer<String>>(), eq(emptyMap()))
        ).willReturn(firstEndpoint.toString())

        given(
            httpsClient.doGet(eq(firstEndpoint), any<Deserializer<String>>(), eq(discoverLinks.endpoints[1].headers))
        ).willReturn(expectedEndpoint.toString())

        assertTrue { DiscoveryCache.results.isEmpty() }

        val endpoint = apiDiscoveryClient.discoverEndpoint(baseUrl, discoverLinks)

        assertEquals(expectedEndpoint, endpoint)

        assertFalse { DiscoveryCache.results.isEmpty() }
        assertEquals(expectedEndpoint, DiscoveryCache.getResult(discoverLinks))
    }

    @Test
    fun `should be able to return endpoint on first attempt`() = runAsBlockingTest {
        given(
            httpsClient.doGet(eq(baseUrl), any<Deserializer<String>>(), eq(emptyMap()))
        ).willReturn(firstEndpoint.toString())

        given(
            httpsClient.doGet(eq(firstEndpoint), any<Deserializer<String>>(), eq(discoverLinks.endpoints[1].headers))
        ).willReturn(expectedEndpoint.toString())

        val endpoint = apiDiscoveryClient.discoverEndpoint(baseUrl, discoverLinks)

        assertEquals(expectedEndpoint, endpoint)
    }

    @Test
    fun `should be able to return endpoint on second attempt`() = runAsBlockingTest {
        given(
            httpsClient.doGet(eq(baseUrl), any<Deserializer<String>>(), eq(emptyMap()))
        )
            .willThrow(RuntimeException("some exception"))
            .willReturn(firstEndpoint.toString())

        given(
            httpsClient.doGet(eq(firstEndpoint), any<Deserializer<String>>(), eq(discoverLinks.endpoints[1].headers))
        ).willReturn(expectedEndpoint.toString())

        val endpoint = apiDiscoveryClient.discoverEndpoint(baseUrl, discoverLinks)

        assertEquals(expectedEndpoint, endpoint)

        verify(httpsClient, times(2)).doGet(eq(baseUrl), any<Deserializer<String>>(), eq(emptyMap()))
        verify(httpsClient).doGet(eq(firstEndpoint), any<Deserializer<String>>(), eq(discoverLinks.endpoints[1].headers))
    }

    @Test
    fun `should throw exception when unable to return endpoint after second attempt`() = runAsBlockingTest {
        given(
            httpsClient.doGet(eq(baseUrl), any<Deserializer<String>>(), eq(emptyMap()))
        )
            .willThrow(RuntimeException("some exception 1"))
            .willThrow(RuntimeException("some exception 2"))
            .willReturn(firstEndpoint.toString())

        given(
            httpsClient.doGet(eq(firstEndpoint), any<Deserializer<String>>(), eq(discoverLinks.endpoints[1].headers))
        ).willReturn(expectedEndpoint.toString())

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
}
