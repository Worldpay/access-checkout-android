package com.worldpay.access.checkout.api.discovery

import com.worldpay.access.checkout.api.AccessCheckoutException
import com.worldpay.access.checkout.api.AccessCheckoutException.*
import com.worldpay.access.checkout.api.Callback
import com.worldpay.access.checkout.testutils.argumentCaptor
import com.worldpay.access.checkout.testutils.capture
import com.worldpay.access.checkout.testutils.mock
import com.worldpay.access.checkout.testutils.typeSafeAny
import org.junit.Rule
import org.junit.Test
import org.junit.rules.ExpectedException
import org.mockito.ArgumentCaptor
import org.mockito.BDDMockito.given
import org.mockito.Mockito.*
import kotlin.test.assertEquals
import kotlin.test.assertNotNull
import kotlin.test.assertNull
import kotlin.test.assertTrue

class AccessCheckoutDiscoveryClientTest {

    private val mockedAsyncTaskFactory: AccessCheckoutDiscoveryAsyncTaskFactory = mock()

    @get:Rule
    var expectedException: ExpectedException = ExpectedException.none()

    @Test
    fun givenEmptyBaseURL_ThenShouldThrowAnException() {
        expectedException.expect(AccessCheckoutDiscoveryException::class.java)
        expectedException.expectMessage("No URL supplied")

        val callback = object : Callback<String> {
            override fun onResponse(error: Exception?, response: String?) {
            }
        }

        AccessCheckoutDiscoveryClient(mockedAsyncTaskFactory).discover("", callback)
    }

    @Test
    fun givenValidBaseURLAndFirstTimeDiscoveryWithoutCallback_ThenShouldExecuteAsyncTask() {
        val accessCheckoutDiscoveryAsyncTask = mock<AccessCheckoutDiscoveryAsyncTask>()
        given(mockedAsyncTaskFactory.getAsyncTask(typeSafeAny())).willReturn(accessCheckoutDiscoveryAsyncTask)

        AccessCheckoutDiscoveryClient(mockedAsyncTaskFactory).discover("http://localhost")

        verify(accessCheckoutDiscoveryAsyncTask, times(1)).execute("http://localhost")
    }

    @Test
    fun givenValidBaseURLAndFirstTimeDiscoveryWithCallback_ThenShouldExecuteAsyncTaskAndCallbackReceivesSuccessWhenDone() {
        val sessionResponse = "session_response"
        val callback = object : Callback<String> {
            override fun onResponse(error: Exception?, response: String?) {
                assertNotNull(response)
                assertEquals(sessionResponse, response)
            }
        }
        val argumentCaptor: ArgumentCaptor<Callback<String>> = argumentCaptor()

        val accessCheckoutDiscoveryAsyncTask = mock<AccessCheckoutDiscoveryAsyncTask>()
        given(mockedAsyncTaskFactory.getAsyncTask(typeSafeAny())).willReturn(accessCheckoutDiscoveryAsyncTask)

        AccessCheckoutDiscoveryClient(mockedAsyncTaskFactory).discover("http://localhost", callback)
        verify(mockedAsyncTaskFactory).getAsyncTask(capture(argumentCaptor))
        argumentCaptor.value.onResponse(null, sessionResponse)

        verify(accessCheckoutDiscoveryAsyncTask, times(1)).execute("http://localhost")

    }

    @Test
    fun givenValidBaseURLAndFirstTimeDiscoveryWithCallback_ThenShouldExecuteAsyncTaskAndCallbackReceivesFailureWhenDone() {
        val exceptionMessage = "Some exception"
        val callback = object : Callback<String> {
            override fun onResponse(error: Exception?, response: String?) {
                assertNotNull(error)
                assertTrue(error is RuntimeException)
                assertEquals(exceptionMessage, error.message)
            }
        }
        val argumentCaptor: ArgumentCaptor<Callback<String>> = argumentCaptor()

        val accessCheckoutDiscoveryAsyncTask = mock<AccessCheckoutDiscoveryAsyncTask>()
        given(mockedAsyncTaskFactory.getAsyncTask(typeSafeAny())).willReturn(accessCheckoutDiscoveryAsyncTask)

        AccessCheckoutDiscoveryClient(mockedAsyncTaskFactory).discover("http://localhost", callback)
        verify(mockedAsyncTaskFactory).getAsyncTask(capture(argumentCaptor))
        argumentCaptor.value.onResponse(RuntimeException(exceptionMessage), null)

        verify(accessCheckoutDiscoveryAsyncTask, times(1)).execute("http://localhost")
    }

    @Test
    fun givenValidBaseURLAndDiscoveryAttemptedAlreadyAndSessionsURLIsAvailable_ThenShouldReturnURLToSessions() {
        val sessionResponse = "session_response"

        val secondCallback = object : Callback<String> {
            override fun onResponse(error: Exception?, response: String?) {
                assertNull(error)
                assertNotNull(response)
                assertEquals(sessionResponse, response)
            }
        }

        val accessCheckoutDiscoveryAsyncTask = mock(AccessCheckoutDiscoveryAsyncTask::class.java)
        val argumentCaptor: ArgumentCaptor<Callback<String>> = argumentCaptor()
        given(mockedAsyncTaskFactory.getAsyncTask(typeSafeAny())).willReturn(accessCheckoutDiscoveryAsyncTask)

        val accessCheckoutDiscoveryClient = AccessCheckoutDiscoveryClient(mockedAsyncTaskFactory)
        accessCheckoutDiscoveryClient.discover("http://localhost")
        verify(mockedAsyncTaskFactory).getAsyncTask(capture(argumentCaptor))

        argumentCaptor.value.onResponse(null, sessionResponse)
        accessCheckoutDiscoveryClient.discover("http://localhost", secondCallback)

        verify(accessCheckoutDiscoveryAsyncTask, times(1)).execute("http://localhost")
    }


    @Test
    fun givenValidBaseURLAndMaximumDiscoveryAttemptsHitAndExceptionWasTheLastResult_ThenShouldCallbackWithResult() {
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
        val argumentCaptor: ArgumentCaptor<Callback<String>> = argumentCaptor()
        given(mockedAsyncTaskFactory.getAsyncTask(typeSafeAny())).willReturn(accessCheckoutDiscoveryAsyncTask)

        val accessCheckoutDiscoveryClient = AccessCheckoutDiscoveryClient(mockedAsyncTaskFactory)
        accessCheckoutDiscoveryClient.discover("http://localhost")
        verify(mockedAsyncTaskFactory).getAsyncTask(capture(argumentCaptor))
        argumentCaptor.value.onResponse(RuntimeException(exceptionMessage), null)

        accessCheckoutDiscoveryClient.discover("http://localhost")
        verify(mockedAsyncTaskFactory, times(2)).getAsyncTask(capture(argumentCaptor))
        argumentCaptor.value.onResponse(RuntimeException(exceptionMessage), null)

        accessCheckoutDiscoveryClient.discover("http://localhost", callback)
        verify(mockedAsyncTaskFactory, times(2)).getAsyncTask(capture(argumentCaptor))
        argumentCaptor.value.onResponse(RuntimeException(exceptionMessage), null)

        verify(accessCheckoutDiscoveryAsyncTask, times(2)).execute("http://localhost")
    }

    @Test
    fun givenValidBaseURLAndMaximumDiscoveryAttemptsHitAndExceptionWasTheLastResultAndNoCallback_ThenShouldNotNotify() {
        val exceptionMessage = "Some exception message"

        val accessCheckoutDiscoveryAsyncTask = mock<AccessCheckoutDiscoveryAsyncTask>()
        val argumentCaptor: ArgumentCaptor<Callback<String>> = argumentCaptor()
        given(mockedAsyncTaskFactory.getAsyncTask(typeSafeAny())).willReturn(accessCheckoutDiscoveryAsyncTask)

        val accessCheckoutDiscoveryClient = AccessCheckoutDiscoveryClient(mockedAsyncTaskFactory)
        accessCheckoutDiscoveryClient.discover("http://localhost")
        verify(mockedAsyncTaskFactory).getAsyncTask(capture(argumentCaptor))
        argumentCaptor.value.onResponse(RuntimeException(exceptionMessage), null)

        accessCheckoutDiscoveryClient.discover("http://localhost")
        verify(mockedAsyncTaskFactory, times(2)).getAsyncTask(capture(argumentCaptor))
        argumentCaptor.value.onResponse(RuntimeException(exceptionMessage), null)

        accessCheckoutDiscoveryClient.discover("http://localhost")
        verify(mockedAsyncTaskFactory, times(2)).getAsyncTask(capture(argumentCaptor))
        argumentCaptor.value.onResponse(RuntimeException(exceptionMessage), null)

        verify(accessCheckoutDiscoveryAsyncTask, times(2)).execute("http://localhost")
    }

    @Test
    fun givenValidBaseURLAndDiscoveryAttemptedAlready_WhenDiscoverIsCalledAgainWithoutCallback_ThenNoOneIsNotified() {
        val sessionResponse = "session_response"

        val accessCheckoutDiscoveryAsyncTask = mock(AccessCheckoutDiscoveryAsyncTask::class.java)
        val argumentCaptor: ArgumentCaptor<Callback<String>> = argumentCaptor()
        given(mockedAsyncTaskFactory.getAsyncTask(typeSafeAny())).willReturn(accessCheckoutDiscoveryAsyncTask)

        val accessCheckoutDiscoveryClient = AccessCheckoutDiscoveryClient(mockedAsyncTaskFactory)
        accessCheckoutDiscoveryClient.discover("http://localhost")
        verify(mockedAsyncTaskFactory).getAsyncTask(capture(argumentCaptor))

        argumentCaptor.value.onResponse(null, sessionResponse)
        accessCheckoutDiscoveryClient.discover("http://localhost")

        verify(accessCheckoutDiscoveryAsyncTask, times(1)).execute("http://localhost")
    }

}