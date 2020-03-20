package com.worldpay.access.checkout.api

import android.content.Context
import android.content.Intent
import android.support.v4.content.LocalBroadcastManager
import com.nhaarman.mockitokotlin2.any
import com.nhaarman.mockitokotlin2.mock
import com.worldpay.access.checkout.api.discovery.AccessCheckoutDiscoveryClient
import com.worldpay.access.checkout.api.discovery.AccessCheckoutDiscoveryClientFactory
import com.worldpay.access.checkout.api.discovery.DiscoverLinks
import org.junit.Assert.assertNull
import org.junit.Before
import org.junit.Test
import org.mockito.BDDMockito.given
import org.mockito.Mockito
import org.mockito.Mockito.mock
import kotlin.test.assertNotNull

class CardSessionRequestServiceTest {

    private lateinit var sessionRequestService: SessionRequestService
    private lateinit var requestDispatcherFactory: RequestDispatcherFactory
    private lateinit var accessCheckoutDiscoveryClientFactory: AccessCheckoutDiscoveryClientFactory
    private lateinit var localBroadcastManagerFactory: LocalBroadcastManagerFactory
    private lateinit var sessionRequestSender: SessionRequestSender
    private lateinit var localBroadcastManager: LocalBroadcastManager
    private lateinit var accessCheckoutDiscoveryClient: AccessCheckoutDiscoveryClient
    private lateinit var discoverLinks: DiscoverLinks

    @Before
    fun setup() {
        sessionRequestSender = mock(SessionRequestSender::class.java)
        requestDispatcherFactory = mock(RequestDispatcherFactory::class.java)
        accessCheckoutDiscoveryClientFactory = mock(AccessCheckoutDiscoveryClientFactory::class.java)
        accessCheckoutDiscoveryClient = mock()
        localBroadcastManagerFactory = mock(LocalBroadcastManagerFactory::class.java)
        localBroadcastManager = mock(LocalBroadcastManager::class.java)
        discoverLinks = mock()
        given(localBroadcastManagerFactory.createInstance()).willReturn(localBroadcastManager)
        sessionRequestService = SessionRequestService(MockedFactory())
    }

    @Test
    fun `assert service is instantiated with default factory`() {
        assertNotNull(SessionRequestService())
    }

    @Test
    fun `assert that started service is never bound`() {
        assertNull(sessionRequestService.onBind(mock(Intent::class.java)))
    }

    @Test
    fun `given empty intent, then service does not trigger sending of session request`() {
        sessionRequestService.onStartCommand(null, -1, 0)

        Mockito.verifyZeroInteractions(sessionRequestSender)
    }

    @Test
    fun `given an intent with the request information is sent, then service can extract information and send request`() {
        val intent = mock(Intent::class.java)
        val sessionRequest = CardSessionRequest("111111", CardSessionRequest.CardExpiryDate(12, 21), "123", "merchant-id")
        given(intent.getSerializableExtra("request")).willReturn(sessionRequest)
        val baseUrl = "http://localhost"
        given(intent.getStringExtra("base_url")).willReturn(baseUrl)

        sessionRequestService.onStartCommand(intent, -1, 0)

        sessionRequestSender.sendSessionRequest(sessionRequest, baseUrl, sessionRequestService)
    }

    @Test
    fun `given a response is received, then response is broad casted to receivers`() {
        val sessionResponse = SessionResponse(
            SessionResponse.Links(
                SessionResponse.Links.VerifiedTokensSession("some link"),
                emptyArray()
            )
        )

        sessionRequestService.onResponse(null, sessionResponse)

        Mockito.verify(localBroadcastManager).sendBroadcast(any())
    }

    internal inner class MockedFactory: Factory {
        override fun getLocalBroadcastManagerFactory(context: Context) = localBroadcastManagerFactory

        override fun getSessionRequestSender(context: Context) = sessionRequestSender
    }
}

