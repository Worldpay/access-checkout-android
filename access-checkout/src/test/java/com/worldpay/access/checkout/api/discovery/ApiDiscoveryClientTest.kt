package com.worldpay.access.checkout.api.discovery

import com.nhaarman.mockitokotlin2.mock
import com.worldpay.access.checkout.api.HttpsClient
import com.worldpay.access.checkout.testutils.CoroutineTestRule
import java.net.URL
import kotlin.test.assertEquals
import kotlinx.coroutines.ExperimentalCoroutinesApi
import kotlinx.coroutines.test.runBlockingTest as runAsBlockingTest
import org.junit.Before
import org.junit.Rule
import org.junit.Test
import org.mockito.BDDMockito.given

@ExperimentalCoroutinesApi
class ApiDiscoveryClientTest {
// TODO: US707277 - some tests need writing here
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
    fun `should retrieve endpoint from cache when one exists`() = runAsBlockingTest {
        DiscoveryCache.results[discoverLinks.endpoints[0].endpoint] = expectedEndpoint

        val endpoint = apiDiscoveryClient.discoverEndpoint(baseUrl, discoverLinks)

        assertEquals(expectedEndpoint, endpoint)
    }

    @Test
    fun `should save endpoint to cache when endpoint discovery is successful`() = runAsBlockingTest {
        given(
            httpsClient.doGet(baseUrl, discoverLinks.endpoints[0].getDeserializer(), discoverLinks.endpoints[0].headers)
        ).willReturn(firstEndpoint.toString())

        given(
            httpsClient.doGet(firstEndpoint, discoverLinks.endpoints[1].getDeserializer(), discoverLinks.endpoints[1].headers)
        ).willReturn(expectedEndpoint.toString())

        val endpoint = apiDiscoveryClient.discoverEndpoint(baseUrl, discoverLinks)

        assertEquals(expectedEndpoint, endpoint)
    }

    @Test
    fun `should be able to return endpoint on first attempt`() {
    }

    @Test
    fun `should be able to return endpoint on second attempt`() {
    }

    @Test
    fun `should be able to return endpoint on third attempt`() {
    }

    @Test
    fun `should throw exception when unable to return endpoint after third attempt`() {
    }
/*
    @Test
    fun `should execute async task when valid url is provided without callback for first time discovery`() {
        val accessCheckoutDiscoveryAsyncTask = mock<EndpointDiscoveryClient>()

        given(httpsClient.doGet()).willReturn(accessCheckoutDiscoveryAsyncTask)

        val baseUrl = URL("https://localhost:8443")

        apiDiscoveryClient.discoverEndpoint(baseUrl, discoverLinks)

        verify(accessCheckoutDiscoveryAsyncTask, times(1)).discoverEndpoint(baseUrl, discoverLinks.endpoints)
    }

    @Test
    fun `should execute async task and receive success callback when valid url and callback provided`() {
        var assertionsRan = false
        val sessionResponse = "session_response"

        val callback = object : Callback<String> {
            override fun onResponse(error: Exception?, response: String?) {
                assertNotNull(response)
                assertEquals(sessionResponse, response)
                assertionsRan = true
            }
        }

        given(endpointDiscoveryClientFactoryMock.getEndpointDiscoveryClient()).willReturn(endpointDiscoveryClientMock)

        apiDiscoveryClient.discoverEndpoint("https://localhost:8443", discoverLinks)

        val argumentCaptor = argumentCaptor<Callback<String>>()
        verify(endpointDiscoveryClientFactoryMock).getEndpointDiscoveryClient(argumentCaptor.capture(), any())
        argumentCaptor.firstValue.onResponse(null, sessionResponse)

        verify(endpointDiscoveryClientMock, times(1)).execute("https://localhost:8443")
        assertTrue(assertionsRan, "Did not run callback assertions - callback was never invoked")
    }

    @Test
    fun `should execute async task and receive success callback when valid url and callback provided and three levels of discovery required`() {
        var assertionsRan = false
        val sessionResponse = "session_response"

        val callback = object : Callback<String> {
            override fun onResponse(error: Exception?, response: String?) {
                assertNotNull(response)
                assertEquals(sessionResponse, response)
                assertionsRan = true
            }
        }

        discoverLinks = DiscoverLinks(listOf(Endpoint("one"), Endpoint("two"), Endpoint("three")))

        given(endpointDiscoveryClientFactoryMock.getEndpointDiscoveryClient(any(), any())).willReturn(endpointDiscoveryClientMock)

        apiDiscoveryClient.discoverEndpoint("https://localhost:8443", callback, discoverLinks)

        val argumentCaptor = argumentCaptor<Callback<String>>()
        verify(endpointDiscoveryClientFactoryMock).getEndpointDiscoveryClient(argumentCaptor.capture(), any())
        argumentCaptor.firstValue.onResponse(null, sessionResponse)

        verify(endpointDiscoveryClientMock, times(1)).execute("https://localhost:8443")
        assertTrue(assertionsRan, "Did not run callback assertions - callback was never invoked")
    }

    @Test
    fun `should return url to sessions and not re-execute async task when URL is available and has already been discovered`() {
        var assertionsRan1 = false
        var assertionsRan2 = false
        val sessionResponse = "session_response"

        val callback = object : Callback<String> {
            override fun onResponse(error: Exception?, response: String?) {
                assertNotNull(response)
                assertEquals(sessionResponse, response)
                assertionsRan1 = true
            }
        }

        val secondCallback = object : Callback<String> {
            override fun onResponse(error: Exception?, response: String?) {
                assertNull(error)
                assertNotNull(response)
                assertEquals(sessionResponse, response)
                assertionsRan2 = true
            }
        }

        val argumentCaptor = argumentCaptor<Callback<String>>()

        apiDiscoveryClient.discoverEndpoint("https://localhost:8443", callback, discoverLinks)
        verify(endpointDiscoveryClientFactoryMock).getEndpointDiscoveryClient(argumentCaptor.capture(), any())

        argumentCaptor.firstValue.onResponse(null, sessionResponse)
        apiDiscoveryClient.discoverEndpoint("https://localhost:8443", secondCallback, discoverLinks)

        verify(endpointDiscoveryClientMock, times(1)).execute("https://localhost:8443")

        assertTrue(assertionsRan1, "Did not run callback 1 assertions - callback 1 was never invoked")
        assertTrue(assertionsRan2, "Did not run callback 2 assertions - callback 2 was never invoked")
    }

    @Test
    fun `should return error message if maximum number attempts have been made and an error was the last response`() {
        var assertionsRan = false
        val exceptionMessage = "Some exception message"
        val callback = object : Callback<String> {
            override fun onResponse(error: Exception?, response: String?) {
                assertNull(response)
                assertNotNull(error)
                assertTrue(error is RuntimeException)
                assertEquals(error.message, exceptionMessage)
                assertionsRan = true
            }
        }

        val argumentCaptor = argumentCaptor<Callback<String>>()
        given(endpointDiscoveryClientFactoryMock.getEndpointDiscoveryClient(argumentCaptor.capture(), any())).willReturn(endpointDiscoveryClientMock)

        apiDiscoveryClient.discoverEndpoint("https://localhost:8443", callback, discoverLinks)

        verify(endpointDiscoveryClientFactoryMock).getEndpointDiscoveryClient(argumentCaptor.capture(), any())
        argumentCaptor.firstValue.onResponse(RuntimeException(exceptionMessage), null)
        argumentCaptor.firstValue.onResponse(RuntimeException(exceptionMessage), null)

        verify(endpointDiscoveryClientMock, times(2)).execute("https://localhost:8443")
        assertTrue(assertionsRan, "Did not run callback assertions - callback was never invoked")
    }

    @Test
    fun `should return error message if maximum number attempts have been made and an error was the last response - on the second discover call`() {
        var assertionsRan = 0
        val exceptionMessage = "Some exception message"
        val callback = object : Callback<String> {
            override fun onResponse(error: Exception?, response: String?) {
                assertNull(response)
                assertNotNull(error)
                assertTrue(error is RuntimeException)
                assertEquals(error.message, exceptionMessage)
                assertionsRan++
            }
        }

        val argumentCaptor = argumentCaptor<Callback<String>>()
        given(endpointDiscoveryClientFactoryMock.getEndpointDiscoveryClient(argumentCaptor.capture(), any())).willReturn(endpointDiscoveryClientMock)

        apiDiscoveryClient.discoverEndpoint("https://localhost:8443", callback, discoverLinks)

        verify(endpointDiscoveryClientFactoryMock).getEndpointDiscoveryClient(argumentCaptor.capture(), any())
        argumentCaptor.firstValue.onResponse(RuntimeException(exceptionMessage), null)
        argumentCaptor.firstValue.onResponse(RuntimeException(exceptionMessage), null)

        apiDiscoveryClient.discoverEndpoint("https://localhost:8443", callback, discoverLinks)

        verify(endpointDiscoveryClientMock, times(2)).execute("https://localhost:8443")
        assertEquals(2, assertionsRan, "Did not run callback assertions - callback was never invoked")
    }
*/
}
