package com.worldpay.access.checkout.api.discovery

import com.nhaarman.mockitokotlin2.any
import com.nhaarman.mockitokotlin2.argumentCaptor
import com.nhaarman.mockitokotlin2.mock
import com.worldpay.access.checkout.api.Callback
import com.worldpay.access.checkout.client.api.exception.AccessCheckoutException
import org.junit.Before
import org.junit.Test
import org.mockito.BDDMockito.given
import org.mockito.Mockito.times
import org.mockito.Mockito.verify
import kotlin.test.*

class ApiDiscoveryClientTest {

    private val apiDiscoveryAsyncTaskFactoryMock: ApiDiscoveryAsyncTaskFactory = mock()
    private var discoverLinks: DiscoverLinks = DiscoverLinks(listOf(Endpoint("some-service"), Endpoint("some-endpoint")))
    private val apiDiscoveryAsyncTaskMock: ApiDiscoveryAsyncTask = mock()

    @Before
    fun setUp() {
        DiscoveryCache.results.clear()
    }

    @Test
    fun `should throw an exception when base url is empty`() {
        val exception = assertFailsWith<AccessCheckoutException> {
            ApiDiscoveryClient(apiDiscoveryAsyncTaskFactoryMock).discover("", getEmptyCallback(), discoverLinks)
        }

        assertEquals("No URL supplied", exception.message)
    }

    @Test
    fun `should execute async task when valid url is provided without callback for first time discovery`() {
        val accessCheckoutDiscoveryAsyncTask = mock<ApiDiscoveryAsyncTask>()

        given(apiDiscoveryAsyncTaskFactoryMock.getAsyncTask(any(),any())).willReturn(accessCheckoutDiscoveryAsyncTask)

        val serviceDiscoveryClient = ApiDiscoveryClient(apiDiscoveryAsyncTaskFactoryMock)

        serviceDiscoveryClient.discover("http://localhost", getEmptyCallback(), discoverLinks)

        verify(accessCheckoutDiscoveryAsyncTask, times(1)).execute("http://localhost")
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


        val serviceDiscoveryClient = ApiDiscoveryClient(apiDiscoveryAsyncTaskFactoryMock)
        given(apiDiscoveryAsyncTaskFactoryMock.getAsyncTask(any(), any())).willReturn(apiDiscoveryAsyncTaskMock)

        serviceDiscoveryClient.discover("http://localhost", callback, discoverLinks)

        val argumentCaptor = argumentCaptor<Callback<String>>()
        verify(apiDiscoveryAsyncTaskFactoryMock).getAsyncTask(argumentCaptor.capture(), any())
        argumentCaptor.firstValue.onResponse(null, sessionResponse)

        verify(apiDiscoveryAsyncTaskMock, times(1)).execute("http://localhost")
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

        val serviceDiscoveryClient = ApiDiscoveryClient(apiDiscoveryAsyncTaskFactoryMock)
        given(apiDiscoveryAsyncTaskFactoryMock.getAsyncTask(any(), any())).willReturn(apiDiscoveryAsyncTaskMock)

        serviceDiscoveryClient.discover("http://localhost", callback, discoverLinks)

        val argumentCaptor = argumentCaptor<Callback<String>>()
        verify(apiDiscoveryAsyncTaskFactoryMock).getAsyncTask(argumentCaptor.capture(), any())
        argumentCaptor.firstValue.onResponse(null, sessionResponse)

        verify(apiDiscoveryAsyncTaskMock, times(1)).execute("http://localhost")
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

        val serviceDiscoveryClient = ApiDiscoveryClient(apiDiscoveryAsyncTaskFactoryMock)
        given(apiDiscoveryAsyncTaskFactoryMock.getAsyncTask(any(), any())).willReturn(apiDiscoveryAsyncTaskMock)

        serviceDiscoveryClient.discover("http://localhost", callback, discoverLinks)
        verify(apiDiscoveryAsyncTaskFactoryMock).getAsyncTask(argumentCaptor.capture(), any())

        argumentCaptor.firstValue.onResponse(null, sessionResponse)
        serviceDiscoveryClient.discover("http://localhost", secondCallback, discoverLinks)

        verify(apiDiscoveryAsyncTaskMock, times(1)).execute("http://localhost")

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
        given(apiDiscoveryAsyncTaskFactoryMock.getAsyncTask(argumentCaptor.capture(), any())).willReturn(apiDiscoveryAsyncTaskMock)

        val serviceDiscoveryClient = ApiDiscoveryClient(apiDiscoveryAsyncTaskFactoryMock)
        serviceDiscoveryClient.discover("http://localhost", callback, discoverLinks)

        verify(apiDiscoveryAsyncTaskFactoryMock).getAsyncTask(argumentCaptor.capture(), any())
        argumentCaptor.firstValue.onResponse(RuntimeException(exceptionMessage), null)
        argumentCaptor.firstValue.onResponse(RuntimeException(exceptionMessage), null)

        verify(apiDiscoveryAsyncTaskMock, times(2)).execute("http://localhost")
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
        given(apiDiscoveryAsyncTaskFactoryMock.getAsyncTask(argumentCaptor.capture(), any())).willReturn(apiDiscoveryAsyncTaskMock)

        val serviceDiscoveryClient = ApiDiscoveryClient(apiDiscoveryAsyncTaskFactoryMock)

        serviceDiscoveryClient.discover("http://localhost", callback, discoverLinks)

        verify(apiDiscoveryAsyncTaskFactoryMock).getAsyncTask(argumentCaptor.capture(), any())
        argumentCaptor.firstValue.onResponse(RuntimeException(exceptionMessage), null)
        argumentCaptor.firstValue.onResponse(RuntimeException(exceptionMessage), null)

        serviceDiscoveryClient.discover("http://localhost", callback, discoverLinks)

        verify(apiDiscoveryAsyncTaskMock, times(2)).execute("http://localhost")
        assertEquals(2, assertionsRan, "Did not run callback assertions - callback was never invoked")
    }

    private fun getEmptyCallback(): Callback<String> {
        return object : Callback<String> {
            override fun onResponse(error: Exception?, response: String?) {
            }
        }
    }

}
