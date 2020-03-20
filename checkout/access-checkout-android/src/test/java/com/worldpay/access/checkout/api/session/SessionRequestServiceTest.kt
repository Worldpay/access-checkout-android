package com.worldpay.access.checkout.api.session

import android.content.Intent
import android.support.v4.content.LocalBroadcastManager
import com.nhaarman.mockitokotlin2.any
import com.nhaarman.mockitokotlin2.verify
import com.nhaarman.mockitokotlin2.verifyZeroInteractions
import com.worldpay.access.checkout.api.LocalBroadcastManagerFactory
import com.worldpay.access.checkout.api.discovery.DiscoverLinks
import org.junit.Assert.assertNull
import org.junit.Before
import org.junit.Test
import org.mockito.BDDMockito.given
import org.mockito.Mockito.mock
import kotlin.test.assertNotNull

class SessionRequestServiceTest {

    private lateinit var sessionRequestService: SessionRequestService
    private lateinit var localBroadcastManagerFactory: LocalBroadcastManagerFactory
    private lateinit var sessionRequestSender: SessionRequestSender

    @Before
    fun setup() {
        sessionRequestSender = mock(SessionRequestSender::class.java)
        localBroadcastManagerFactory = mock(LocalBroadcastManagerFactory::class.java)

        val mockFactory = mock(Factory::class.java)
        given(mockFactory.getSessionRequestSender(any())).willReturn(sessionRequestSender)
        given(mockFactory.getLocalBroadcastManagerFactory(any())).willReturn(
            localBroadcastManagerFactory
        )

        sessionRequestService = SessionRequestService(mockFactory)
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
    fun `should not send session request when intent is empty`() {
        sessionRequestService.onStartCommand(null, -1, 0)

        verifyZeroInteractions(sessionRequestSender)
    }

    @Test
    fun `should be able to send card session request when the intent has the appropriate information`() {
        val intent = mock(Intent::class.java)
        val sessionRequest = CardSessionRequest(
            cardNumber = "111111",
            cardExpiryDate = CardSessionRequest.CardExpiryDate(12, 21),
            cvv = "123",
            identity = "merchant-id"
        )

        val baseUrl = "http://localhost"

        given(intent.getSerializableExtra("request")).willReturn(sessionRequest)
        given(intent.getStringExtra("base_url")).willReturn(baseUrl)
        given(intent.getSerializableExtra("discover")).willReturn(DiscoverLinks.verifiedTokens)

        sessionRequestService.onStartCommand(intent, -1, 0)

        verify(sessionRequestSender).sendSessionRequest(sessionRequest, baseUrl, sessionRequestService, DiscoverLinks.verifiedTokens)
    }

    @Test
    fun `should be able to send cvv session request when the intent has the appropriate information`() {
        val intent = mock(Intent::class.java)
        val sessionRequest = CVVSessionRequest(
            cvv = "123",
            identity = "merchant-id"
        )

        val baseUrl = "http://localhost"

        given(intent.getSerializableExtra("request")).willReturn(sessionRequest)
        given(intent.getStringExtra("base_url")).willReturn(baseUrl)
        given(intent.getSerializableExtra("discover")).willReturn(DiscoverLinks.verifiedTokens)

        sessionRequestService.onStartCommand(intent, -1, 0)

        verify(sessionRequestSender).sendSessionRequest(sessionRequest, baseUrl, sessionRequestService, DiscoverLinks.verifiedTokens)
    }

    @Test
    fun `should be able to broadcast response to receivers once response is received`() {
        val sessionResponse =
            SessionResponse(
                SessionResponse.Links(
                    SessionResponse.Links.VerifiedTokensSession(
                        "some link"
                    ),
                    emptyArray()
                )
            )

        val localBroadcastManager = mock(LocalBroadcastManager::class.java)

        given(localBroadcastManagerFactory.createInstance()).willReturn(localBroadcastManager)

        sessionRequestService.onResponse(null, sessionResponse)

        verify(localBroadcastManager).sendBroadcast(any())
    }

}

