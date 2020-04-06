package com.worldpay.access.checkout.api.discovery

import com.nhaarman.mockitokotlin2.given
import com.nhaarman.mockitokotlin2.mock
import com.worldpay.access.checkout.api.AccessCheckoutException.*
import com.worldpay.access.checkout.api.Callback
import com.worldpay.access.checkout.api.HttpClient
import com.worldpay.access.checkout.api.serialization.LinkDiscoveryDeserializer
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

    private val endpointMock: Endpoint = mock()
    private val secondEndpointMock: Endpoint = mock()

    private val endpointsMock = listOf<Endpoint>(endpointMock,secondEndpointMock)
    private val httpClientMock: HttpClient = mock()
    private val linkDiscoveryDeserializerMock: LinkDiscoveryDeserializer = mock()
    private val secondLinkDiscoveryDeserializerMock: LinkDiscoveryDeserializer = mock()
    private val thirdLinkDiscoveryDeserializerMock: LinkDiscoveryDeserializer = mock()

    @Before
    fun setUp() {
        given(endpointMock.getDeserializer()).willReturn(linkDiscoveryDeserializerMock)
        given(secondEndpointMock.getDeserializer()).willReturn(secondLinkDiscoveryDeserializerMock)
    }

    @Test
    fun `should throw an exception given a bad base url`() {
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
            endpointsMock,
            httpClientMock
        )

        accessCheckoutDiscoveryAsyncTask.execute("abcd")

        Awaitility.await().atMost(5, TimeUnit.SECONDS).until { asserted }
    }

    @Test
    fun `should throw an AccessCheckoutDiscoveryException when root service discovery throws an error`() {
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
        BDDMockito.given(httpClientMock.doGet(rootURL, linkDiscoveryDeserializerMock)).willThrow(
            AccessCheckoutHttpException(
                "Some message",
                AccessCheckoutDeserializationException("deserialization")
            )
        )

        val accessCheckoutDiscoveryAsyncTask = AccessCheckoutDiscoveryAsyncTask(
            callback,
            endpointsMock,
            httpClientMock
        )

        accessCheckoutDiscoveryAsyncTask.execute("http://localhost")

        Awaitility.await().atMost(5, TimeUnit.SECONDS).until { asserted }
    }

    @Test
    fun  `should throw an AccessCheckoutDiscoveryException when service discovery throws a server error`() {
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
        BDDMockito.given(httpClientMock.doGet(rootURL, linkDiscoveryDeserializerMock)).willThrow(
            AccessCheckoutError("Some message")
        )

        val accessCheckoutDiscoveryAsyncTask = AccessCheckoutDiscoveryAsyncTask(
            callback,
            endpointsMock,
            httpClientMock
        )

        accessCheckoutDiscoveryAsyncTask.execute("http://localhost")

        Awaitility.await().atMost(5, TimeUnit.SECONDS).until { asserted }
    }

    @Test
    fun `should throw an AccessCheckoutDiscoveryException when deserializer throws an exception`() {
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
        given(httpClientMock.doGet(rootURL, linkDiscoveryDeserializerMock)).willReturn(serviceUrl)
        given(httpClientMock.doGet(URL(serviceUrl), secondLinkDiscoveryDeserializerMock)).willThrow(
            AccessCheckoutHttpException(
                "Some message",
                AccessCheckoutDeserializationException("deserialization")
            )
        )

        val accessCheckoutDiscoveryAsyncTask = AccessCheckoutDiscoveryAsyncTask(
            callback,
            endpointsMock,
            httpClientMock
        )

        accessCheckoutDiscoveryAsyncTask.execute("http://localhost")

        Awaitility.await().atMost(5, TimeUnit.SECONDS).until { asserted }
    }

    @Test
    fun `should throw an exception when other exception thrown`() {
        var asserted = false

        val callback = object : Callback<String> {
            override fun onResponse(error: Exception?, response: String?) {
                assertTrue(error is IllegalStateException)
                assertEquals(error.message, "Some message")
                asserted = true
            }
        }

        val rootURL = URL("http://localhost")
        given(httpClientMock.doGet(rootURL, linkDiscoveryDeserializerMock)).willThrow(IllegalStateException("Some message"))

        val accessCheckoutDiscoveryAsyncTask = AccessCheckoutDiscoveryAsyncTask(
            callback,
            endpointsMock,
            httpClientMock
        )

        accessCheckoutDiscoveryAsyncTask.execute("http://localhost")

        Awaitility.await().atMost(5, TimeUnit.SECONDS).until { asserted }
    }

    @Test
    fun `should throw exception when given exception with no message`() {
        var asserted = false

        val callback = object : Callback<String> {
            override fun onResponse(error: Exception?, response: String?) {
                assertTrue(error is IllegalStateException)
                assertNull(error.message)
                asserted = true
            }
        }

        val rootURL = URL("http://localhost")
        given(httpClientMock.doGet(rootURL, linkDiscoveryDeserializerMock)).willThrow(IllegalStateException())

        val accessCheckoutDiscoveryAsyncTask = AccessCheckoutDiscoveryAsyncTask(
            callback,
            endpointsMock,
            httpClientMock
        )

        accessCheckoutDiscoveryAsyncTask.execute("http://localhost")

        Awaitility.await().atMost(5, TimeUnit.SECONDS).until { asserted }
    }

    @Test
    fun `should throw malformed url exception when given bad url`() {
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

        BDDMockito.given(httpClientMock.doGet(rootURL, linkDiscoveryDeserializerMock)).willReturn(rubbishResponse)

        val accessCheckoutDiscoveryAsyncTask = AccessCheckoutDiscoveryAsyncTask(
            callback,
            endpointsMock,
            httpClientMock
        )

        accessCheckoutDiscoveryAsyncTask.execute("http://localhost")

        Awaitility.await().atMost(5, TimeUnit.SECONDS).until { asserted }
    }


    @Test
    fun `should discover resource when given a valid url and two levels discovery required`() {

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

        BDDMockito.given(httpClientMock.doGet(rootURL, linkDiscoveryDeserializerMock)).willReturn("$baseURL/verifiedTokens")
        BDDMockito.given(httpClientMock.doGet(serviceUrl, secondLinkDiscoveryDeserializerMock)).willReturn("$baseURL/verifiedTokens/sessions")

        val accessCheckoutDiscoveryAsyncTask = AccessCheckoutDiscoveryAsyncTask(
            callback,
            endpointsMock,
            httpClientMock
        )

        accessCheckoutDiscoveryAsyncTask.execute(baseURL)

        Awaitility.await().atMost(5, TimeUnit.SECONDS).until { asserted }
    }

    @Test
    fun `should discover resource when given valid base url and one level discovery required`() {

        val baseURL = "http://localhost"
        var asserted = false
        val callback = object : Callback<String> {
            override fun onResponse(error: Exception?, response: String?) {
                assertEquals("$baseURL/verifiedTokens", response)
                assertNull(error)
                asserted = true
            }
        }

        val rootURL = URL(baseURL)
        val oneLevelEndpointsMock = listOf(endpointMock)
        given(endpointMock.getDeserializer()).willReturn(linkDiscoveryDeserializerMock)

        given(httpClientMock.doGet(rootURL, linkDiscoveryDeserializerMock)).willReturn("$baseURL/verifiedTokens")

        val accessCheckoutDiscoveryAsyncTask = AccessCheckoutDiscoveryAsyncTask(
            callback,
            oneLevelEndpointsMock,
            httpClientMock
        )

        accessCheckoutDiscoveryAsyncTask.execute(baseURL)

        Awaitility.await().atMost(5, TimeUnit.SECONDS).until { asserted }
    }

    fun `should discover resource when given valid base url and three levels of discovery required`() {

        val baseURL = "http://localhost"
        var asserted = false
        val callback = object : Callback<String> {
            override fun onResponse(error: Exception?, response: String?) {
                assertEquals("$baseURL/verifiedTokens", response)
                assertNull(error)
                asserted = true
            }
        }

        val rootURL = URL(baseURL)
        val thirdEndpointMock: Endpoint = mock()
        val threeLevelEndpointsMock = listOf(endpointMock,secondEndpointMock,thirdEndpointMock)

        given(endpointMock.getDeserializer()).willReturn(linkDiscoveryDeserializerMock)
        given(secondEndpointMock.getDeserializer()).willReturn(secondLinkDiscoveryDeserializerMock)
        given(thirdEndpointMock.getDeserializer()).willReturn(thirdLinkDiscoveryDeserializerMock)

        BDDMockito.given(httpClientMock.doGet(rootURL, linkDiscoveryDeserializerMock)).willReturn("$baseURL/first")
        BDDMockito.given(httpClientMock.doGet(rootURL, secondLinkDiscoveryDeserializerMock)).willReturn("$baseURL/first/second")
        BDDMockito.given(httpClientMock.doGet(rootURL, thirdLinkDiscoveryDeserializerMock)).willReturn("$baseURL/first/second/third")

        val accessCheckoutDiscoveryAsyncTask = AccessCheckoutDiscoveryAsyncTask(
            callback,
            threeLevelEndpointsMock,
            httpClientMock
        )

        accessCheckoutDiscoveryAsyncTask.execute(baseURL)

        Awaitility.await().atMost(5, TimeUnit.SECONDS).until { asserted }
    }
}
