package com.worldpay.access.checkout.api.session

import com.nhaarman.mockitokotlin2.any
import com.nhaarman.mockitokotlin2.argumentCaptor
import com.nhaarman.mockitokotlin2.eq
import com.worldpay.access.checkout.api.AccessCheckoutException.AccessCheckoutDiscoveryException
import com.worldpay.access.checkout.api.Callback
import com.worldpay.access.checkout.api.discovery.ApiDiscoveryClient
import com.worldpay.access.checkout.api.discovery.DiscoverLinks
import com.worldpay.access.checkout.api.session.client.CardSessionClient
import com.worldpay.access.checkout.api.session.client.SessionClientFactory
import com.worldpay.access.checkout.api.session.request.RequestDispatcher
import com.worldpay.access.checkout.api.session.request.RequestDispatcherFactory
import com.worldpay.access.checkout.client.SessionType.VERIFIED_TOKEN_SESSION
import org.junit.Assert.assertEquals
import org.junit.Assert.assertTrue
import org.junit.Before
import org.junit.Test
import org.mockito.BDDMockito.given
import org.mockito.Mockito
import org.mockito.Mockito.mock
import org.mockito.Mockito.verify

class SessionRequestSenderTest {

    private lateinit var sessionClientFactory: SessionClientFactory
    private lateinit var requestDispatcherFactory: RequestDispatcherFactory
    private lateinit var apiDiscoveryClient: ApiDiscoveryClient
    private lateinit var sessionRequestSender: SessionRequestSender

    private val baseURL = "http://localhost"

    @Before
    fun setup() {
        sessionClientFactory = mock(SessionClientFactory::class.java)
        requestDispatcherFactory = mock(RequestDispatcherFactory::class.java)
        apiDiscoveryClient = mock(ApiDiscoveryClient::class.java)
        sessionRequestSender = SessionRequestSender(
            sessionClientFactory, requestDispatcherFactory, apiDiscoveryClient
        )
    }

    @Test
    fun `should execute request given that the discovery response is valid`() {
        val expectedSessionRequest = CardSessionRequest(
            cardNumber = "00001111222233334444",
            cardExpiryDate = CardSessionRequest.CardExpiryDate(1, 2020),
            cvv = "123",
            identity = ""
        )

        val sessionResponseCallback = object : Callback<SessionResponse> {
            override fun onResponse(error: Exception?, response: SessionResponse?) {
            }
        }

        val requestDispatcher = mock(RequestDispatcher::class.java)
        val sessionClient = mock(CardSessionClient::class.java)
        val path = "$baseURL/verifiedTokens/sessions"

        given(sessionClientFactory.createClient(expectedSessionRequest)).willReturn(sessionClient)
        given(requestDispatcherFactory.getInstance(path, sessionClient, sessionResponseCallback))
            .willReturn(requestDispatcher)

        sessionRequestSender.sendSessionRequest(
            expectedSessionRequest, VERIFIED_TOKEN_SESSION, baseURL, sessionResponseCallback, DiscoverLinks.verifiedTokens
        )

        val argumentCaptor = argumentCaptor<Callback<String>>()
        verify(apiDiscoveryClient).discover(eq(baseURL), argumentCaptor.capture(), any())
        argumentCaptor.firstValue.onResponse(null, path)

        verify(requestDispatcher).execute(expectedSessionRequest)
    }

    @Test
    fun `should error with exception given that the discovery response is invalid`() {
        val expectedSessionRequest = CardSessionRequest(
            cardNumber = "00001111222233334444",
            cardExpiryDate = CardSessionRequest.CardExpiryDate(1, 2020),
            cvv = "123",
            identity = ""
        )

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

        val requestDispatcher = mock(RequestDispatcher::class.java)
        val sessionClient = mock(CardSessionClient::class.java)
        val path = "$baseURL/verifiedTokens/sessions"

        given(sessionClientFactory.createClient(expectedSessionRequest)).willReturn(sessionClient)
        given(requestDispatcherFactory.getInstance(path, sessionClient, sessionResponseCallback))
            .willReturn(requestDispatcher)

        sessionRequestSender.sendSessionRequest(
            expectedSessionRequest,
            VERIFIED_TOKEN_SESSION,
            baseURL,
            sessionResponseCallback,
            DiscoverLinks.verifiedTokens
        )
        val argumentCaptor = argumentCaptor<Callback<String>>()
        verify(apiDiscoveryClient).discover(eq(baseURL), argumentCaptor.capture(), any())
        argumentCaptor.firstValue.onResponse(RuntimeException("Some exception"), null)

        Mockito.verifyZeroInteractions(requestDispatcher)
        assertTrue(assertResponse)
    }
}