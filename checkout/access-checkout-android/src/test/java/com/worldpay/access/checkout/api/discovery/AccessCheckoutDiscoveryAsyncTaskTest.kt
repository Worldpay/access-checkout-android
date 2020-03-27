package com.worldpay.access.checkout.api.discovery

import com.nhaarman.mockitokotlin2.given
import com.nhaarman.mockitokotlin2.mock
import com.worldpay.access.checkout.api.AccessCheckoutException.*
import com.worldpay.access.checkout.api.Callback
import com.worldpay.access.checkout.api.HttpClient
import com.worldpay.access.checkout.api.serialization.Deserializer
import com.worldpay.access.checkout.api.serialization.LinkDiscoveryDeserializer
import com.worldpay.access.checkout.api.serialization.LinkDiscoveryDeserializerFactory
import org.awaitility.Awaitility
import org.junit.Before
import org.junit.Test
import org.junit.runner.RunWith
import org.mockito.BDDMockito
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
class AccessCheckoutDiscoveryAsyncTaskTest {

    private val mockedResources = listOf<String>("some","endpoint")
    private val mockedHttpClient: HttpClient = mock()
    private val mockedLinkDiscoveryDeserializerFactory: LinkDiscoveryDeserializerFactory = mock()
    private val mockedLinkDiscoveryDeserializer: LinkDiscoveryDeserializer = mock()
    private val mockedSecondLinkDiscoveryDeserializer: LinkDiscoveryDeserializer = mock()

    @Before
    fun setUp() {
        given(mockedLinkDiscoveryDeserializerFactory.getDeserializer(mockedResources[0])).willReturn(mockedLinkDiscoveryDeserializer)
        given(mockedLinkDiscoveryDeserializerFactory.getDeserializer(mockedResources[1])).willReturn(mockedSecondLinkDiscoveryDeserializer)
    }

    @Test
    fun givenBadBaseURL_ThenShouldThrowAnException() {
        var asserted = false

        val callback = object : Callback<String> {
            override fun onResponse(error: Exception?, response: String?) {
                assertTrue(error is AccessCheckoutDiscoveryException)
                assertEquals("Invalid URL supplied: abcd", error.message)
                asserted = true
            }
        }

        val accessCheckoutDiscoveryAsyncTask = AccessCheckoutDiscoveryAsyncTask(
            callback,
            mockedResources,
            mockedHttpClient,
            mockedLinkDiscoveryDeserializerFactory
        )

        accessCheckoutDiscoveryAsyncTask.execute("abcd")

        Awaitility.await().atMost(5, TimeUnit.SECONDS).until { asserted }
    }

    @Test
    fun givenRootServiceDiscoveryThrowsAnException_ThenShouldThrowAnAccessCheckoutDiscoveryException() {
        var asserted = false

        val callback = object : Callback<String> {
            override fun onResponse(error: Exception?, response: String?) {
                assertTrue(error is AccessCheckoutDiscoveryException)
                assertTrue(error.cause is AccessCheckoutHttpException)
                assertEquals(error.message, "An error was thrown when trying to make a connection to the service")
                asserted = true
            }
        }

        val rootURL = URL("http://localhost")
        BDDMockito.given(mockedHttpClient.doGet(rootURL, mockedLinkDiscoveryDeserializer)).willThrow(
            AccessCheckoutHttpException(
                "Some message",
                AccessCheckoutDeserializationException("deserialization")
            )
        )

        val accessCheckoutDiscoveryAsyncTask = AccessCheckoutDiscoveryAsyncTask(
            callback,
            mockedResources,
            mockedHttpClient,
            mockedLinkDiscoveryDeserializerFactory
        )

        accessCheckoutDiscoveryAsyncTask.execute("http://localhost")

        Awaitility.await().atMost(5, TimeUnit.SECONDS).until { asserted }
    }

    @Test
    fun givenRootServiceDiscoveryThrowsAServerError_ThenShouldThrowAnAccessCheckoutDiscoveryException() {
        var asserted = false

        val callback = object : Callback<String> {
            override fun onResponse(error: Exception?, response: String?) {
                assertTrue(error is AccessCheckoutDiscoveryException)
                assertTrue(error.cause is AccessCheckoutError)
                assertEquals(error.message, "An error was thrown when trying to make a connection to the service")
                asserted = true
            }
        }

        val rootURL = URL("http://localhost")
        BDDMockito.given(mockedHttpClient.doGet(rootURL, mockedLinkDiscoveryDeserializer)).willThrow(
            AccessCheckoutError("Some message")
        )

        val accessCheckoutDiscoveryAsyncTask = AccessCheckoutDiscoveryAsyncTask(
            callback,
            mockedResources,
            mockedHttpClient,
            mockedLinkDiscoveryDeserializerFactory
        )

        accessCheckoutDiscoveryAsyncTask.execute("http://localhost")

        Awaitility.await().atMost(5, TimeUnit.SECONDS).until { asserted }
    }

    @Test
    fun givenVTSServiceRootDeserializerThrowsAnException_ThenShouldThrowAnAccessCheckoutDiscoveryException() {
        var asserted = false

        val callback = object : Callback<String> {
            override fun onResponse(error: Exception?, response: String?) {
                assertTrue(error is AccessCheckoutDiscoveryException)
                assertTrue(error.cause is AccessCheckoutHttpException)
                assertEquals(error.message, "An error was thrown when trying to make a connection to the service")
                asserted = true
            }
        }

        val serviceUrl = "http://localhost/verifiedTokens"
        val rootURL = URL("http://localhost")
        BDDMockito.given(mockedHttpClient.doGet(rootURL, mockedLinkDiscoveryDeserializer)).willReturn(serviceUrl)
        BDDMockito.given(mockedHttpClient.doGet(URL(serviceUrl), mockedSecondLinkDiscoveryDeserializer)).willThrow(
            AccessCheckoutHttpException(
                "Some message",
                AccessCheckoutDeserializationException("deserialization")
            )
        )

        val accessCheckoutDiscoveryAsyncTask = AccessCheckoutDiscoveryAsyncTask(
            callback,
            mockedResources,
            mockedHttpClient,
            mockedLinkDiscoveryDeserializerFactory
        )

        accessCheckoutDiscoveryAsyncTask.execute("http://localhost")

        Awaitility.await().atMost(5, TimeUnit.SECONDS).until { asserted }
    }

    @Test
    fun givenOtherExceptionThrown_ThenShouldThrowException() {
        var asserted = false

        val callback = object : Callback<String> {
            override fun onResponse(error: Exception?, response: String?) {
                assertTrue(error is IllegalStateException)
                assertEquals(error.message, "Some message")
                asserted = true
            }
        }

        val rootURL = URL("http://localhost")
        BDDMockito.given(mockedHttpClient.doGet(rootURL, mockedLinkDiscoveryDeserializer))
            .willThrow(IllegalStateException("Some message"))

        val accessCheckoutDiscoveryAsyncTask = AccessCheckoutDiscoveryAsyncTask(
            callback,
            mockedResources,
            mockedHttpClient,
            mockedLinkDiscoveryDeserializerFactory
        )

        accessCheckoutDiscoveryAsyncTask.execute("http://localhost")

        Awaitility.await().atMost(5, TimeUnit.SECONDS).until { asserted }
    }

    @Test
    fun givenOtherExceptionThrownWithNoMessage_ThenShouldThrowException() {
        var asserted = false

        val callback = object : Callback<String> {
            override fun onResponse(error: Exception?, response: String?) {
                assertTrue(error is IllegalStateException)
                assertNull(error.message)
                asserted = true
            }
        }

        val rootURL = URL("http://localhost")
        BDDMockito.given(mockedHttpClient.doGet(rootURL, mockedLinkDiscoveryDeserializer)).willThrow(IllegalStateException())

        val accessCheckoutDiscoveryAsyncTask = AccessCheckoutDiscoveryAsyncTask(
            callback,
            mockedResources,
            mockedHttpClient,
            mockedLinkDiscoveryDeserializerFactory
        )

        accessCheckoutDiscoveryAsyncTask.execute("http://localhost")

        Awaitility.await().atMost(5, TimeUnit.SECONDS).until { asserted }
    }

    @Test
    fun givenInvalidURLReturnedFromService_ThenShouldThrowMalformedUrlException() {
        val rubbishResponse = "somerubbishurl"

        var asserted = false

        val callback = object : Callback<String> {
            override fun onResponse(error: Exception?, response: String?) {
                assertTrue(error is AccessCheckoutDiscoveryException)
                assertNotNull(error.cause)
                assertEquals(MalformedURLException::class.java, error.cause!!::class.java)
                assertEquals("Invalid URL supplied: $rubbishResponse", error.message)
                asserted = true
            }
        }

        val rootURL = URL("http://localhost")

        BDDMockito.given(mockedHttpClient.doGet(rootURL, mockedLinkDiscoveryDeserializer)).willReturn(rubbishResponse)

        val accessCheckoutDiscoveryAsyncTask = AccessCheckoutDiscoveryAsyncTask(
            callback,
            mockedResources,
            mockedHttpClient,
            mockedLinkDiscoveryDeserializerFactory
        )

        accessCheckoutDiscoveryAsyncTask.execute("http://localhost")

        Awaitility.await().atMost(5, TimeUnit.SECONDS).until { asserted }
    }


    @Test
    fun givenValidBaseURL_ThenShouldDiscoverVTSResource() {

        val baseURL = "http://localhost"

        var asserted = false

        val callback = object : Callback<String> {
            override fun onResponse(error: Exception?, response: String?) {
                assertEquals("$baseURL/verifiedTokens/sessions", response)
                assertNull(error)
                asserted = true
            }
        }

        val rootURL = URL(baseURL)
        val serviceUrl = URL("$baseURL/verifiedTokens")

        BDDMockito.given(mockedHttpClient.doGet(rootURL, mockedLinkDiscoveryDeserializer)).willReturn("$baseURL/verifiedTokens")
        BDDMockito.given(mockedHttpClient.doGet(serviceUrl, mockedSecondLinkDiscoveryDeserializer)).willReturn("$baseURL/verifiedTokens/sessions")

        val accessCheckoutDiscoveryAsyncTask = AccessCheckoutDiscoveryAsyncTask(
            callback,
            mockedResources,
            mockedHttpClient,
            mockedLinkDiscoveryDeserializerFactory
        )

        accessCheckoutDiscoveryAsyncTask.execute(baseURL)

        Awaitility.await().atMost(5, TimeUnit.SECONDS).until { asserted }
    }
}
