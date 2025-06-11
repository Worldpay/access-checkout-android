package com.worldpay.access.checkout.session.api

import android.content.Intent
import androidx.localbroadcastmanager.content.LocalBroadcastManager
import com.worldpay.access.checkout.api.discovery.DiscoverLinks
import com.worldpay.access.checkout.client.api.exception.AccessCheckoutException
import com.worldpay.access.checkout.client.session.model.SessionType
import com.worldpay.access.checkout.session.ActivityLifecycleObserver.Companion.inLifeCycleState
import com.worldpay.access.checkout.session.ActivityLifecycleObserver.Companion.messageQueue
import com.worldpay.access.checkout.session.ActivityLifecycleObserver.Companion.processMessageQueue
import com.worldpay.access.checkout.session.api.SessionRequestService.Companion.REQUEST_KEY
import com.worldpay.access.checkout.session.api.request.CardSessionRequest
import com.worldpay.access.checkout.session.api.request.SessionRequestInfo
import com.worldpay.access.checkout.session.api.response.SessionResponse
import com.worldpay.access.checkout.session.api.response.SessionResponseInfo
import com.worldpay.access.checkout.session.broadcast.LocalBroadcastManagerFactory
import com.worldpay.access.checkout.session.broadcast.receivers.COMPLETED_SESSION_REQUEST
import com.worldpay.access.checkout.session.broadcast.receivers.SessionBroadcastReceiver.Companion.ERROR_KEY
import com.worldpay.access.checkout.session.broadcast.receivers.SessionBroadcastReceiver.Companion.RESPONSE_KEY
import com.worldpay.access.checkout.testutils.CoroutineTestRule
import java.net.URL
import kotlin.test.assertEquals
import kotlin.test.assertNotNull
import kotlin.test.assertNull
import kotlinx.coroutines.ExperimentalCoroutinesApi
import kotlinx.coroutines.test.runTest
import org.junit.After
import org.junit.Before
import org.junit.Rule
import org.junit.Test
import org.junit.runner.RunWith
import org.mockito.kotlin.*
import org.robolectric.RobolectricTestRunner

@RunWith(RobolectricTestRunner::class)
@ExperimentalCoroutinesApi
class SessionRequestServiceTest {

    private val factory = mock<Factory>()
    private val sessionRequestSender = mock<SessionRequestSender>()
    private val localBroadcastManagerFactory = mock<LocalBroadcastManagerFactory>()
    private val localBroadcastManager = mock<LocalBroadcastManager>()

    private val baseUrl = URL("https://base.url.com")

    private lateinit var sessionRequestService: SessionRequestService

    @get:Rule
    var coroutinesTestRule = CoroutineTestRule()

    @Before
    fun setup() {
        inLifeCycleState = false
        given(factory.getSessionRequestSender()).willReturn(sessionRequestSender)
        given(factory.getLocalBroadcastManagerFactory(any())).willReturn(localBroadcastManagerFactory)
        given(localBroadcastManagerFactory.createInstance()).willReturn(localBroadcastManager)
        sessionRequestService = spy(SessionRequestService(factory))
    }

    @After
    fun tearDown() {
        inLifeCycleState = false
    }

    @Test
    fun `should obtain an instance when using the default constructor`() {
        assertNotNull(SessionRequestService())
    }

    @Test
    fun `should return null when on bind method is called`() {
        assertNull(sessionRequestService.onBind(mock()))
    }

    @Test
    fun `should not send a session request when the intent is null`() {
        assertEquals(1, sessionRequestService.onStartCommand(null, 1, 1))

        verifyNoInteractions(sessionRequestSender)
    }

    @Test
    fun `should send a session request when the intent is not null and broadcast the response`() = runTest {
        val intent = mock<Intent>()

        val sessionRequestInfo = getSessionRequestInfo()
        val sessionResponseInfo = getSessionResponseInfo()

        given(intent.getSerializableExtra(REQUEST_KEY)).willReturn(sessionRequestInfo)
        given(sessionRequestSender.sendSessionRequest(sessionRequestInfo)).willReturn(sessionResponseInfo)

        assertEquals(1, sessionRequestService.onStartCommand(intent, 1, 1))

        val intentCaptor = argumentCaptor<Intent>()

        verify(sessionRequestSender).sendSessionRequest(sessionRequestInfo)
        verify(localBroadcastManager).sendBroadcast(intentCaptor.capture())

        val broadcastIntent = intentCaptor.firstValue

        assertNotNull(broadcastIntent)
        assertEquals(COMPLETED_SESSION_REQUEST, intentCaptor.firstValue.action)
        assertEquals(2, broadcastIntent.extras!!.size())
        assertEquals(sessionResponseInfo, broadcastIntent.extras!!.get(RESPONSE_KEY))
        assertEquals(null, broadcastIntent.extras!!.get(ERROR_KEY))

        verify(sessionRequestService).stopSelf()
    }

    @Test
    fun `should have exception in error key on broadcast when session request fails`() = runTest {
        val intent = mock<Intent>()

        val sessionRequestInfo = getSessionRequestInfo()
        val exception = RuntimeException("some message")

        given(intent.getSerializableExtra(REQUEST_KEY)).willReturn(sessionRequestInfo)
        given(sessionRequestSender.sendSessionRequest(sessionRequestInfo)).willThrow(exception)

        assertEquals(1, sessionRequestService.onStartCommand(intent, 1, 1))

        val intentCaptor = argumentCaptor<Intent>()

        verify(sessionRequestSender).sendSessionRequest(sessionRequestInfo)
        verify(localBroadcastManager).sendBroadcast(intentCaptor.capture())

        val broadcastIntent = intentCaptor.firstValue

        assertNotNull(broadcastIntent)
        assertEquals(COMPLETED_SESSION_REQUEST, intentCaptor.firstValue.action)
        assertEquals(2, broadcastIntent.extras!!.size())
        assertEquals(null, broadcastIntent.extras!!.get(RESPONSE_KEY))
        assertEquals(exception, broadcastIntent.extras!!.get(ERROR_KEY))

        verify(sessionRequestService).stopSelf()
    }

    @Test
    fun `should throw exception when no request key is found for session request in the intent`() {
        val intent = mock<Intent>()

        val exception = AccessCheckoutException("Failed to parse request key for sending the session request")

        given(intent.getSerializableExtra(REQUEST_KEY)).willReturn(null)

        assertEquals(1, sessionRequestService.onStartCommand(intent, 1, 1))

        val intentCaptor = argumentCaptor<Intent>()

        verifyNoInteractions(sessionRequestSender)
        verify(localBroadcastManager).sendBroadcast(intentCaptor.capture())

        val broadcastIntent = intentCaptor.firstValue

        assertNotNull(broadcastIntent)
        assertEquals(COMPLETED_SESSION_REQUEST, intentCaptor.firstValue.action)
        assertEquals(2, broadcastIntent.extras!!.size())
        assertEquals(null, broadcastIntent.extras!!.get(RESPONSE_KEY))
        assertEquals(exception, broadcastIntent.extras!!.get(ERROR_KEY))

        verify(sessionRequestService).stopSelf()
    }

    @Test
    fun `should post the broadcast function to the message queue when in lifecycle state`() = runTest {
        inLifeCycleState = true
        val intent = mock<Intent>()

        val sessionRequestInfo = getSessionRequestInfo()
        val sessionResponseInfo = getSessionResponseInfo()

        given(intent.getSerializableExtra(REQUEST_KEY)).willReturn(sessionRequestInfo)
        given(sessionRequestSender.sendSessionRequest(sessionRequestInfo)).willReturn(sessionResponseInfo)

        assertEquals(0, messageQueue.size)

        assertEquals(1, sessionRequestService.onStartCommand(intent, 1, 1))

        assertEquals(1, messageQueue.size)
        processMessageQueue()
        assertEquals(0, messageQueue.size)

        val intentCaptor = argumentCaptor<Intent>()

        verify(sessionRequestSender).sendSessionRequest(sessionRequestInfo)
        verify(localBroadcastManager).sendBroadcast(intentCaptor.capture())

        val broadcastIntent = intentCaptor.firstValue

        assertNotNull(broadcastIntent)
        assertEquals(COMPLETED_SESSION_REQUEST, intentCaptor.firstValue.action)
        assertEquals(2, broadcastIntent.extras!!.size())
        assertEquals(sessionResponseInfo, broadcastIntent.extras!!.get(RESPONSE_KEY))
        assertEquals(null, broadcastIntent.extras!!.get(ERROR_KEY))

        verify(sessionRequestService).stopSelf()
    }

    private fun getSessionRequestInfo(): SessionRequestInfo {
        val sessionRequest =
            CardSessionRequest(
                cardNumber = "111111",
                cardExpiryDate = CardSessionRequest.CardExpiryDate(
                    12,
                    21
                ),
                cvc = "123",
                identity = "checkout-id"
            )

        return SessionRequestInfo.Builder()
            .baseUrl(baseUrl)
            .requestBody(sessionRequest)
            .sessionType(SessionType.CARD)
            .discoverLinks(DiscoverLinks.cardSessions)
            .build()
    }

    private fun getSessionResponseInfo(): SessionResponseInfo {
        val sessionResponse =
            SessionResponse(
                SessionResponse.Links(
                    SessionResponse.Links.Endpoints(
                        "some link"
                    ),
                    emptyArray()
                )
            )

        return SessionResponseInfo.Builder()
            .responseBody(sessionResponse)
            .sessionType(SessionType.CARD)
            .build()
    }
}
