package com.worldpay.access.checkout.api.discovery

import com.nhaarman.mockitokotlin2.*
import com.worldpay.access.checkout.api.AccessCheckoutException
import com.worldpay.access.checkout.api.Callback
import org.junit.Rule
import org.junit.Test
import org.junit.rules.ExpectedException
import kotlin.test.assertEquals
import kotlin.test.assertNotNull
import kotlin.test.assertNull
import kotlin.test.assertTrue

class AccessCheckoutCVVOnlyDiscoveryClientTest {
    private val mockedAsyncTaskFactory: AccessCheckoutDiscoveryAsyncTaskFactory = mock()

    @get:Rule
    var expectedException: ExpectedException = ExpectedException.none()

    @Test
    fun `should execute async cvv task when discover is called with a valid url`() {
        val accessCheckoutDiscoveryAsyncTask = mock<AccessCheckoutDiscoveryAsyncTask>()
        given(mockedAsyncTaskFactory.getAsyncCVVTask(any())).willReturn(
            accessCheckoutDiscoveryAsyncTask
        )

        AccessCheckoutCVVOnlyDiscoveryClient(mockedAsyncTaskFactory).discover("http://localhost")

        verify(accessCheckoutDiscoveryAsyncTask, times(1)).execute("http://localhost")
    }

    @Test
    fun `should throw an exception when given an empty url`() {
        expectedException.expect(AccessCheckoutException.AccessCheckoutDiscoveryException::class.java)
        expectedException.expectMessage("No URL supplied")

        val callback = object : Callback<String> {
            override fun onResponse(error: Exception?, response: String?) {
            }
        }

        AccessCheckoutCVVOnlyDiscoveryClient(mockedAsyncTaskFactory).discover("", callback)
    }

    @Test
    fun `should call async cvv task and receive callback response when given a valid base url and discovery is successful`() {
        val sessionResponse = "session_response"
        val callback = object : Callback<String> {
            override fun onResponse(error: Exception?, response: String?) {
                assertNotNull(response)
                assertEquals(sessionResponse, response)
            }
        }
        val argumentCaptor = argumentCaptor<Callback<String>>()

        val accessCheckoutDiscoveryAsyncTask = mock<AccessCheckoutDiscoveryAsyncTask>()
        given(mockedAsyncTaskFactory.getAsyncCVVTask(any())).willReturn(accessCheckoutDiscoveryAsyncTask)

        AccessCheckoutCVVOnlyDiscoveryClient(mockedAsyncTaskFactory).discover("http://localhost", callback)
        verify(mockedAsyncTaskFactory).getAsyncCVVTask(argumentCaptor.capture())
        argumentCaptor.firstValue.onResponse(null, sessionResponse)

        verify(accessCheckoutDiscoveryAsyncTask, times(1)).execute("http://localhost")

    }

    @Test
    fun `should receive error message from async task when given a valid url but first time discovery fails`() {
        val exceptionMessage = "Some exception"
        val callback = object : Callback<String> {
            override fun onResponse(error: Exception?, response: String?) {
                assertNotNull(error)
                assertTrue(error is RuntimeException)
                assertEquals(exceptionMessage, error.message)
            }
        }
        val argumentCaptor = argumentCaptor<Callback<String>>()

        val accessCheckoutDiscoveryAsyncTask = mock<AccessCheckoutDiscoveryAsyncTask>()
        given(mockedAsyncTaskFactory.getAsyncCVVTask(any())).willReturn(accessCheckoutDiscoveryAsyncTask)

        AccessCheckoutCVVOnlyDiscoveryClient(mockedAsyncTaskFactory).discover("http://localhost", callback)
        verify(mockedAsyncTaskFactory).getAsyncCVVTask(argumentCaptor.capture())
        argumentCaptor.firstValue.onResponse(RuntimeException(exceptionMessage), null)

        verify(accessCheckoutDiscoveryAsyncTask, times(1)).execute("http://localhost")
    }

    @Test
    fun `should not re-execute async task when URL is available and has already been discovered`() {
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

        val accessCheckoutDiscoveryAsyncTask = mock<AccessCheckoutDiscoveryAsyncTask>()

        given(mockedAsyncTaskFactory.getAsyncCVVTask(any())).willReturn(accessCheckoutDiscoveryAsyncTask)

        val accessCheckoutCVVOnlyDiscoveryClient = AccessCheckoutCVVOnlyDiscoveryClient(mockedAsyncTaskFactory)
        accessCheckoutCVVOnlyDiscoveryClient.discover("http://localhost", callback)

        verify(mockedAsyncTaskFactory).getAsyncCVVTask(argumentCaptor.capture())
        argumentCaptor.firstValue.onResponse(null, sessionResponse)

        accessCheckoutCVVOnlyDiscoveryClient.discover("http://localhost", secondCallback)

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

        val accessCheckoutDiscoveryAsyncTask = mock<AccessCheckoutDiscoveryAsyncTask>()
        val argumentCaptor = argumentCaptor<Callback<String>>()
        given(mockedAsyncTaskFactory.getAsyncCVVTask(any())).willReturn(accessCheckoutDiscoveryAsyncTask)

        val accessCheckoutCVVOnlyDiscoveryClient = AccessCheckoutCVVOnlyDiscoveryClient(mockedAsyncTaskFactory)
        accessCheckoutCVVOnlyDiscoveryClient.discover("http://localhost", callback)
        verify(mockedAsyncTaskFactory).getAsyncCVVTask(argumentCaptor.capture())
        argumentCaptor.firstValue.onResponse(RuntimeException(exceptionMessage), null)

        accessCheckoutCVVOnlyDiscoveryClient.discover("http://localhost", callback)
        verify(mockedAsyncTaskFactory, times(2)).getAsyncCVVTask(argumentCaptor.capture())
        argumentCaptor.firstValue.onResponse(RuntimeException(exceptionMessage), null)

        accessCheckoutCVVOnlyDiscoveryClient.discover("http://localhost", callback)
        verify(mockedAsyncTaskFactory, times(2)).getAsyncCVVTask(argumentCaptor.capture())
        argumentCaptor.firstValue.onResponse(RuntimeException(exceptionMessage), null)

        verify(accessCheckoutDiscoveryAsyncTask, times(2)).execute("http://localhost")
    }

}