package com.worldpay.access.checkout.api

import com.worldpay.access.checkout.api.AccessCheckoutException.*
import com.worldpay.access.checkout.api.discovery.AccessCheckoutDiscoveryClient
import com.worldpay.access.checkout.testutils.argumentCaptor
import com.worldpay.access.checkout.testutils.capture
import com.worldpay.access.checkout.testutils.mock
import com.worldpay.access.checkout.testutils.typeSafeEq
import org.junit.Assert.assertEquals
import org.junit.Assert.assertTrue
import org.junit.Before
import org.junit.Test
import org.mockito.BDDMockito
import org.mockito.Mockito
import org.mockito.Mockito.verify

class SessionRequestSenderTest {

    private lateinit var requestDispatcherFactory: RequestDispatcherFactory
    private lateinit var accessCheckoutDiscoveryClient: AccessCheckoutDiscoveryClient
    private lateinit var sessionRequestSender: SessionRequestSender

    private val baseURL = "http://localhost"

    @Before
    fun setup() {
        requestDispatcherFactory = mock()
        accessCheckoutDiscoveryClient = mock()
        sessionRequestSender = SessionRequestSender(requestDispatcherFactory, accessCheckoutDiscoveryClient)
    }

    @Test
    fun givenLinkDiscoveryReturnsValidResponse_ThenShouldDispatchRequestToEndpoint() {
        val expectedSessionRequest =
            SessionRequest("00001111222233334444", SessionRequest.CardExpiryDate(1, 2020), "123", "")

        val sessionResponseCallback = object : Callback<SessionResponse> {
            override fun onResponse(error: Exception?, response: SessionResponse?) {
            }
        }

        val requestDispatcher = Mockito.mock(RequestDispatcher::class.java)
        val path = "$baseURL/verifiedTokens/sessions"
        BDDMockito.given(requestDispatcherFactory.getInstance(path, sessionResponseCallback))
            .willReturn(requestDispatcher)

        sessionRequestSender.sendSessionRequest(expectedSessionRequest, baseURL, sessionResponseCallback)
        val argumentCaptor = argumentCaptor<Callback<String>>()
        verify(accessCheckoutDiscoveryClient).discover(typeSafeEq(baseURL), capture(argumentCaptor))
        argumentCaptor.value.onResponse(null, path)

        Mockito.verify(requestDispatcher).execute(expectedSessionRequest)
    }

    @Test
    fun givenLinkDiscoveryReturnsError_ThenCallbackWithErrorIfNotSuccessful() {
        val expectedSessionRequest =
            SessionRequest("00001111222233334444", SessionRequest.CardExpiryDate(1, 2020), "123", "")

        var assertResponse = false

        val sessionResponseCallback = object : Callback<SessionResponse> {
            override fun onResponse(error: Exception?, response: SessionResponse?) {
                assertTrue(error is AccessCheckoutDiscoveryException)
                assertEquals("Could not discover URL", error?.message)
                assertTrue((error as AccessCheckoutDiscoveryException).cause is RuntimeException)
                assertEquals("Some exception", error.cause?.message)
                assertResponse = true
            }
        }

        val requestDispatcher = Mockito.mock(RequestDispatcher::class.java)
        val path = "$baseURL/verifiedTokens/sessions"
        BDDMockito.given(requestDispatcherFactory.getInstance(path, sessionResponseCallback))
            .willReturn(requestDispatcher)

        sessionRequestSender.sendSessionRequest(expectedSessionRequest, baseURL, sessionResponseCallback)
        val argumentCaptor = argumentCaptor<Callback<String>>()
        verify(accessCheckoutDiscoveryClient).discover(typeSafeEq(baseURL), capture(argumentCaptor))
        argumentCaptor.value.onResponse(RuntimeException("Some exception"), null)

        Mockito.verifyZeroInteractions(requestDispatcher)
        assertTrue(assertResponse)
    }
}