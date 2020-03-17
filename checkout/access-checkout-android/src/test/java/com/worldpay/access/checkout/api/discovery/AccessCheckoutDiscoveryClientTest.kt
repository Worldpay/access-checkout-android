package com.worldpay.access.checkout.api.discovery

import com.nhaarman.mockitokotlin2.any
import com.nhaarman.mockitokotlin2.argumentCaptor
import com.nhaarman.mockitokotlin2.mock
import com.worldpay.access.checkout.AccessCheckoutClient
import com.worldpay.access.checkout.api.AccessCheckoutException.AccessCheckoutDiscoveryException
import com.worldpay.access.checkout.api.Callback
import org.junit.Before
import org.junit.Rule
import org.junit.Test
import org.junit.rules.ExpectedException
import org.mockito.BDDMockito.given
import org.mockito.Mockito.*
import kotlin.test.assertEquals
import kotlin.test.assertNotNull
import kotlin.test.assertNull
import kotlin.test.assertTrue

class AccessCheckoutDiscoveryClientTest {

    private val mockedAsyncTaskFactory: AccessCheckoutDiscoveryAsyncTaskFactory = mock()
    private val discoverLinks: DiscoverLinks = DiscoverLinks("some-service", "some-endpoint")
    private val accessCheckoutDiscoveryAsyncTask: AccessCheckoutDiscoveryAsyncTask = mock()

    @get:Rule
    var expectedException: ExpectedException = ExpectedException.none()

    @Test
    fun `should throw an exception when base url is empty`() {
        expectedException.expect(AccessCheckoutDiscoveryException::class.java)
        expectedException.expectMessage("No URL supplied")

        val callback = object : Callback<String> {
            override fun onResponse(error: Exception?, response: String?) {
            }
        }

        AccessCheckoutDiscoveryClient(mockedAsyncTaskFactory).discover("", callback, discoverLinks)
    }

    @Test
    fun `should execute async task when valid url is provided without callback for first time discovery`() {
        val accessCheckoutDiscoveryAsyncTask = mock<AccessCheckoutDiscoveryAsyncTask>()

        given(mockedAsyncTaskFactory.getAsyncTask(any(),any())).willReturn(accessCheckoutDiscoveryAsyncTask)

        val client =  AccessCheckoutDiscoveryClient(mockedAsyncTaskFactory)

        client.discover("http://localhost", discoverLinks = discoverLinks)

        verify(accessCheckoutDiscoveryAsyncTask, times(1)).execute("http://localhost")
    }

    @Test
    fun `should execute async task and receive success callback when valid url and callback provided`() {
        val sessionResponse = "session_response"

        val callback = object : Callback<String> {
            override fun onResponse(error: Exception?, response: String?) {
                assertNotNull(response)
                assertEquals(sessionResponse, response)
            }
        }


        val client = AccessCheckoutDiscoveryClient(mockedAsyncTaskFactory)
        given(mockedAsyncTaskFactory.getAsyncTask(any(), any())).willReturn(accessCheckoutDiscoveryAsyncTask)

        client.discover("http://localhost", callback, discoverLinks)

        val argumentCaptor = argumentCaptor<Callback<String>>()
        verify(mockedAsyncTaskFactory).getAsyncTask(argumentCaptor.capture(), any())
        argumentCaptor.firstValue.onResponse(null, sessionResponse)

        verify(accessCheckoutDiscoveryAsyncTask, times(1)).execute("http://localhost")

    }

    @Test

    fun `should execute async task and callback receives error message when given a valid base url but discovery is unsuccessful`() {
        val exceptionMessage = "Some exception"
        val callback = object : Callback<String> {
            override fun onResponse(error: Exception?, response: String?) {
                assertNotNull(error)
                assertTrue(error is RuntimeException)
                assertEquals(exceptionMessage, error.message)
            }
        }
        val argumentCaptor = argumentCaptor<Callback<String>>()

        given(mockedAsyncTaskFactory.getAsyncTask(any(),any())).willReturn(accessCheckoutDiscoveryAsyncTask)

        AccessCheckoutDiscoveryClient(mockedAsyncTaskFactory).discover("http://localhost", callback, discoverLinks)
        verify(mockedAsyncTaskFactory).getAsyncTask(argumentCaptor.capture(), any())
        argumentCaptor.firstValue.onResponse(RuntimeException(exceptionMessage), null)

        verify(accessCheckoutDiscoveryAsyncTask, times(1)).execute("http://localhost")
    }

    @Test
    fun `should return url to sessions and not re-execute async task when URL is available and has already been discovered`() {
        val sessionResponse = "session_response"

        val callback = object : Callback<String> {
            override fun onResponse(error: Exception?, response: String?) {
                assertNotNull(response)
                assertEquals(sessionResponse, response)
            }
        }


        val secondCallback = object : Callback<String> {
            override fun onResponse(error: Exception?, response: String?) {
                assertNull(error)
                assertNotNull(response)
                assertEquals(sessionResponse, response)
            }
        }

        val argumentCaptor = argumentCaptor<Callback<String>>()

        val accessCheckoutDiscoveryClient = AccessCheckoutDiscoveryClient(mockedAsyncTaskFactory)
        given(mockedAsyncTaskFactory.getAsyncTask(any(), any())).willReturn(accessCheckoutDiscoveryAsyncTask)

        accessCheckoutDiscoveryClient.discover("http://localhost", callback, discoverLinks)
        verify(mockedAsyncTaskFactory).getAsyncTask(argumentCaptor.capture(), any())

        argumentCaptor.firstValue.onResponse(null, sessionResponse)
        accessCheckoutDiscoveryClient.discover("http://localhost", secondCallback, discoverLinks)

        verify(accessCheckoutDiscoveryAsyncTask, times(1)).execute("http://localhost")
    }


    @Test
    fun `should return error message if maximum number attempts have been made and an error was the last response`() {
        val exceptionMessage = "Some exception message"
        val callback = object : Callback<String> {
            override fun onResponse(error: Exception?, response: String?) {
                assertNull(response)
                assertNotNull(error)
                assertTrue(error is RuntimeException)
                assertEquals(error.message, exceptionMessage)
            }
        }

        val argumentCaptor = argumentCaptor<Callback<String>>()
        given(mockedAsyncTaskFactory.getAsyncTask(any(),any())).willReturn(accessCheckoutDiscoveryAsyncTask)

        val accessCheckoutDiscoveryClient = AccessCheckoutDiscoveryClient(mockedAsyncTaskFactory)
        accessCheckoutDiscoveryClient.discover("http://localhost", callback, discoverLinks)
        verify(mockedAsyncTaskFactory).getAsyncTask(argumentCaptor.capture(), any())
        argumentCaptor.firstValue.onResponse(RuntimeException(exceptionMessage), null)

        accessCheckoutDiscoveryClient.discover("http://localhost", callback, discoverLinks)
        verify(mockedAsyncTaskFactory, times(2)).getAsyncTask(argumentCaptor.capture(),any())
        argumentCaptor.firstValue.onResponse(RuntimeException(exceptionMessage), null)

        accessCheckoutDiscoveryClient.discover("http://localhost", callback, discoverLinks)
        verify(mockedAsyncTaskFactory, times(2)).getAsyncTask(argumentCaptor.capture(),any())
        argumentCaptor.firstValue.onResponse(RuntimeException(exceptionMessage), null)


        verify(accessCheckoutDiscoveryAsyncTask, times(2)).execute("http://localhost")
    }

    @Test
    fun `should not notify when maximum number of attempts have been hit if there is no callback and the last result was an exception`() {
        val exceptionMessage = "Some exception message"

        val accessCheckoutDiscoveryAsyncTask = mock<AccessCheckoutDiscoveryAsyncTask>()
        val argumentCaptor = argumentCaptor<Callback<String>>()
        given(mockedAsyncTaskFactory.getAsyncTask(any(),any())).willReturn(accessCheckoutDiscoveryAsyncTask)

        val accessCheckoutDiscoveryClient = AccessCheckoutDiscoveryClient(mockedAsyncTaskFactory)
        accessCheckoutDiscoveryClient.discover("http://localhost", discoverLinks = discoverLinks)
        verify(mockedAsyncTaskFactory).getAsyncTask(argumentCaptor.capture(),any())
        argumentCaptor.firstValue.onResponse(RuntimeException(exceptionMessage), null)

        accessCheckoutDiscoveryClient.discover("http://localhost", discoverLinks = discoverLinks)
        verify(mockedAsyncTaskFactory, times(2)).getAsyncTask(argumentCaptor.capture(),any())
        argumentCaptor.firstValue.onResponse(RuntimeException(exceptionMessage), null)

        accessCheckoutDiscoveryClient.discover("http://localhost", discoverLinks = discoverLinks)
        verify(mockedAsyncTaskFactory, times(2)).getAsyncTask(argumentCaptor.capture(),any())
        argumentCaptor.firstValue.onResponse(RuntimeException(exceptionMessage), null)

        verify(accessCheckoutDiscoveryAsyncTask, times(2)).execute("http://localhost")
    }

    @Test
    fun `should not notify if discovery is called a second time with no callback`() {
        val sessionResponse = "session_response"

        val accessCheckoutDiscoveryAsyncTask = mock(AccessCheckoutDiscoveryAsyncTask::class.java)
        val argumentCaptor = argumentCaptor<Callback<String>>()
        given(mockedAsyncTaskFactory.getAsyncTask(any(),any())).willReturn(accessCheckoutDiscoveryAsyncTask)

        val accessCheckoutDiscoveryClient = AccessCheckoutDiscoveryClient(mockedAsyncTaskFactory)
        accessCheckoutDiscoveryClient.discover("http://localhost", discoverLinks = discoverLinks)
        verify(mockedAsyncTaskFactory).getAsyncTask(argumentCaptor.capture(),any())

        argumentCaptor.firstValue.onResponse(null, sessionResponse)
        accessCheckoutDiscoveryClient.discover("http://localhost", discoverLinks = discoverLinks)

        verify(accessCheckoutDiscoveryAsyncTask, times(1)).execute("http://localhost")
    }

}