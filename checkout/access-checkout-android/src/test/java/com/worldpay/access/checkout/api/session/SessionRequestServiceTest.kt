package com.worldpay.access.checkout.api.session

import android.content.Intent
import androidx.localbroadcastmanager.content.LocalBroadcastManager
import com.nhaarman.mockitokotlin2.any
import com.nhaarman.mockitokotlin2.verify
import com.nhaarman.mockitokotlin2.verifyZeroInteractions
import com.worldpay.access.checkout.api.discovery.DiscoverLinks
import com.worldpay.access.checkout.session.request.broadcast.LocalBroadcastManagerFactory
import com.worldpay.access.checkout.session.request.broadcast.receivers.GET_REQUESTED_SESSION
import com.worldpay.access.checkout.session.request.broadcast.receivers.SessionBroadcastReceiver.Companion.ERROR_KEY
import com.worldpay.access.checkout.session.request.broadcast.receivers.SessionBroadcastReceiver.Companion.RESPONSE_KEY
import org.junit.Assert.assertNull
import org.junit.Before
import org.junit.Test
import org.junit.runner.RunWith
import org.mockito.ArgumentCaptor
import org.mockito.BDDMockito.given
import org.mockito.Mockito.mock
import org.robolectric.RobolectricTestRunner
import kotlin.test.assertEquals
import kotlin.test.assertNotNull

@RunWith(RobolectricTestRunner::class)
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
                    SessionResponse.Links.Endpoints(
                        "some link"
                    ),
                    emptyArray()
                )
            )

        val localBroadcastManager = mock(LocalBroadcastManager::class.java)

        given(localBroadcastManagerFactory.createInstance()).willReturn(localBroadcastManager)

        sessionRequestService.onResponse(null, sessionResponse)

        val argument = ArgumentCaptor.forClass(Intent::class.java)

        verify(localBroadcastManager).sendBroadcast(argument.capture())

        assertEquals(sessionResponse, argument.value.getSerializableExtra(RESPONSE_KEY))
        assertNull(argument.value.getSerializableExtra(ERROR_KEY))
        assertEquals(GET_REQUESTED_SESSION, argument.value.action)
    }

}

