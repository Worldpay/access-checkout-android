package com.worldpay.access.checkout.session.api

import android.content.Intent
import androidx.localbroadcastmanager.content.LocalBroadcastManager
import com.nhaarman.mockitokotlin2.any
import com.nhaarman.mockitokotlin2.verify
import com.nhaarman.mockitokotlin2.verifyZeroInteractions
import com.worldpay.access.checkout.api.discovery.DiscoverLinks
import com.worldpay.access.checkout.client.api.exception.AccessCheckoutException
import com.worldpay.access.checkout.client.session.model.SessionType.CARD
import com.worldpay.access.checkout.session.ActivityLifecycleObserver.Companion.inLifeCycleState
import com.worldpay.access.checkout.session.api.request.CardSessionRequest
import com.worldpay.access.checkout.session.api.request.CvcSessionRequest
import com.worldpay.access.checkout.session.api.request.SessionRequestInfo
import com.worldpay.access.checkout.session.api.response.SessionResponse
import com.worldpay.access.checkout.session.api.response.SessionResponseInfo
import com.worldpay.access.checkout.session.broadcast.LocalBroadcastManagerFactory
import com.worldpay.access.checkout.session.broadcast.receivers.COMPLETED_SESSION_REQUEST
import com.worldpay.access.checkout.session.broadcast.receivers.SessionBroadcastReceiver.Companion.ERROR_KEY
import com.worldpay.access.checkout.session.broadcast.receivers.SessionBroadcastReceiver.Companion.RESPONSE_KEY
import kotlin.test.assertEquals
import kotlin.test.assertNotNull
import org.junit.After
import org.junit.Assert.assertNull
import org.junit.Before
import org.junit.Test
import org.junit.runner.RunWith
import org.mockito.ArgumentCaptor
import org.mockito.BDDMockito.given
import org.mockito.Mockito.mock
import org.robolectric.RobolectricTestRunner
import org.robolectric.shadows.ShadowLooper

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

        sessionRequestService =
            SessionRequestService(
                mockFactory
            )
    }

    @After
    fun tearDown() {
        inLifeCycleState = false
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
        val sessionRequest =
            CardSessionRequest(
                cardNumber = "111111",
                cardExpiryDate = CardSessionRequest.CardExpiryDate(
                    12,
                    21
                ),
                cvc = "123",
                identity = "merchant-id"
            )

        val sessionRequestInfo = SessionRequestInfo.Builder()
            .baseUrl("https://localhost:8443")
            .requestBody(sessionRequest)
            .sessionType(CARD)
            .discoverLinks(DiscoverLinks.verifiedTokens)
            .build()

        given(intent.getSerializableExtra("request")).willReturn(sessionRequestInfo)

        sessionRequestService.onStartCommand(intent, -1, 0)

        verify(sessionRequestSender).sendSessionRequest(sessionRequestInfo, sessionRequestService)
    }

    @Test
    fun `should be able to send cvc session request when the intent has the appropriate information`() {
        val intent = mock(Intent::class.java)
        val sessionRequest =
            CvcSessionRequest(
                cvc = "123",
                identity = "merchant-id"
            )

        val sessionRequestInfo = SessionRequestInfo.Builder()
            .baseUrl("https://localhost:8443")
            .requestBody(sessionRequest)
            .sessionType(CARD)
            .discoverLinks(DiscoverLinks.verifiedTokens)
            .build()

        given(intent.getSerializableExtra("request")).willReturn(sessionRequestInfo)

        sessionRequestService.onStartCommand(intent, -1, 0)

        verify(sessionRequestSender).sendSessionRequest(sessionRequestInfo, sessionRequestService)
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

        val sessionResponseInfo = SessionResponseInfo.Builder()
            .responseBody(sessionResponse)
            .sessionType(CARD)
            .build()

        val localBroadcastManager = mock(LocalBroadcastManager::class.java)

        given(localBroadcastManagerFactory.createInstance()).willReturn(localBroadcastManager)

        sessionRequestService.onResponse(null, sessionResponseInfo)

        val argument = ArgumentCaptor.forClass(Intent::class.java)

        verify(localBroadcastManager).sendBroadcast(argument.capture())

        assertEquals(sessionResponseInfo, argument.value.getSerializableExtra(RESPONSE_KEY))

        assertNull(argument.value.getSerializableExtra(ERROR_KEY))
        assertEquals(2, argument.value.extras?.size())

        assertEquals(COMPLETED_SESSION_REQUEST, argument.value.action)
    }

    @Test
    fun `should be able to broadcast error to receivers once error is received`() {
        val localBroadcastManager = mock(LocalBroadcastManager::class.java)
        given(localBroadcastManagerFactory.createInstance()).willReturn(localBroadcastManager)

        val exception = AccessCheckoutException("some error")
        sessionRequestService.onResponse(exception, null)

        val argument = ArgumentCaptor.forClass(Intent::class.java)

        verify(localBroadcastManager).sendBroadcast(argument.capture())

        assertNull(argument.value.getSerializableExtra(RESPONSE_KEY))
        assertEquals(exception, argument.value.getSerializableExtra(ERROR_KEY))
        assertEquals(2, argument.value.extras?.size())

        assertEquals(COMPLETED_SESSION_REQUEST, argument.value.action)
    }

    @Test
    fun `should delay broadcast when in a lifecycle state`() {
        val localBroadcastManager = mock(LocalBroadcastManager::class.java)
        given(localBroadcastManagerFactory.createInstance()).willReturn(localBroadcastManager)

        val exception = AccessCheckoutException("some error")

        inLifeCycleState = true

        sessionRequestService.onResponse(exception, null)

        verifyZeroInteractions(localBroadcastManager)

        val argument = ArgumentCaptor.forClass(Intent::class.java)

        inLifeCycleState = false

        ShadowLooper.runUiThreadTasksIncludingDelayedTasks()

        verify(localBroadcastManager).sendBroadcast(argument.capture())

        assertNull(argument.value.getSerializableExtra(RESPONSE_KEY))
        assertEquals(exception, argument.value.getSerializableExtra(ERROR_KEY))
        assertEquals(2, argument.value.extras?.size())

        assertEquals(COMPLETED_SESSION_REQUEST, argument.value.action)
    }

    @Test
    fun `should delay broadcast when in a lifecycle state and does not return`() {
        val localBroadcastManager = mock(LocalBroadcastManager::class.java)
        given(localBroadcastManagerFactory.createInstance()).willReturn(localBroadcastManager)

        val exception = AccessCheckoutException("some error")

        inLifeCycleState = true

        sessionRequestService.onResponse(exception, null)

        verifyZeroInteractions(localBroadcastManager)

        val argument = ArgumentCaptor.forClass(Intent::class.java)

        // keep calling this so we can test the recursive code
        ShadowLooper.runUiThreadTasksIncludingDelayedTasks()
        ShadowLooper.runUiThreadTasksIncludingDelayedTasks()
        ShadowLooper.runUiThreadTasksIncludingDelayedTasks()
        ShadowLooper.runUiThreadTasksIncludingDelayedTasks()
        ShadowLooper.runUiThreadTasksIncludingDelayedTasks()
        ShadowLooper.runUiThreadTasksIncludingDelayedTasks()

        verify(localBroadcastManager).sendBroadcast(argument.capture())

        assertNull(argument.value.getSerializableExtra(RESPONSE_KEY))
        assertEquals(exception, argument.value.getSerializableExtra(ERROR_KEY))
        assertEquals(2, argument.value.extras?.size())

        assertEquals(COMPLETED_SESSION_REQUEST, argument.value.action)
    }
}
