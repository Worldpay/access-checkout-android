package com.worldpay.access.checkout.session.request.broadcast.receivers

import android.content.Context
import android.content.Intent
import com.nhaarman.mockitokotlin2.mock
import com.worldpay.access.checkout.api.AccessCheckoutException.AccessCheckoutError
import com.worldpay.access.checkout.api.session.SessionResponse
import com.worldpay.access.checkout.api.session.SessionResponseInfo
import com.worldpay.access.checkout.client.SessionType
import com.worldpay.access.checkout.client.SessionType.PAYMENTS_CVC_SESSION
import com.worldpay.access.checkout.client.SessionType.VERIFIED_TOKEN_SESSION
import com.worldpay.access.checkout.session.request.broadcast.receivers.SessionBroadcastReceiver.Companion.NUMBER_OF_SESSION_TYPE_KEY
import com.worldpay.access.checkout.views.SessionResponseListener
import org.junit.Before
import org.junit.Test
import org.junit.runner.RunWith
import org.mockito.BDDMockito.given
import org.mockito.Mockito.*
import org.robolectric.RobolectricTestRunner
import java.io.Serializable
import kotlin.test.assertEquals

@RunWith(RobolectricTestRunner::class)
class SessionBroadcastReceiverTest {

    private lateinit var sessionResponseListener: SessionResponseListener
    private lateinit var sessionBroadcastReceiver: SessionBroadcastReceiver
    private lateinit var intent: Intent
    private lateinit var context: Context
    
    @Before
    fun setup() {
        sessionResponseListener = mock()
        intent = mock()
        context = mock()

        sessionBroadcastReceiver = SessionBroadcastReceiver(sessionResponseListener)
    }

    @Test
    fun `should return expected IntentFilter`() {
        val intentFilter = sessionBroadcastReceiver.getIntentFilter()

        assertEquals(NUM_OF_SESSION_TYPES_REQUESTED, intentFilter.getAction(0))
        assertEquals(SESSION_TYPE_REQUEST_COMPLETE, intentFilter.getAction(1))
        assertEquals(COMPLETED_SESSION_REQUEST, intentFilter.getAction(2))
        assertEquals(3, intentFilter.countActions())
    }

    @Test
    fun `should not notify if the intent is not recognised`() {
        given(intent.action).willReturn("UNKNOWN_INTENT")

        sessionBroadcastReceiver.onReceive(context, intent)

        verifyZeroInteractions(sessionResponseListener)
    }

    @Test
    fun `should return empty session and exception when session response is empty`() {
        given(intent.action).willReturn(COMPLETED_SESSION_REQUEST)
        given(intent.getSerializableExtra("error")).willReturn(AccessCheckoutError("some error"))
        given(intent.getSerializableExtra("session_type")).willReturn(VERIFIED_TOKEN_SESSION)

        sessionBroadcastReceiver.onReceive(context, intent)

        verify(sessionResponseListener, atMost(1))
            .onRequestFinished(null, AccessCheckoutError("some error"))
    }

    @Test
    fun `should return href given a session response is received`() {
        broadcastNumSessionTypesRequested(1)

        given(intent.action).willReturn(COMPLETED_SESSION_REQUEST)
        given(intent.getSerializableExtra("response")).willReturn(createSessionResponse("some reference", VERIFIED_TOKEN_SESSION))
        given(intent.getSerializableExtra("error")).willReturn(null)

        sessionBroadcastReceiver.onReceive(context, intent)

        val response = mapOf(VERIFIED_TOKEN_SESSION to "some reference")

        verify(sessionResponseListener).onRequestFinished(response, null)
    }

    @Test
    fun `should return all href given multiple session types are requested`() {
        broadcastNumSessionTypesRequested(2)

        given(intent.action).willReturn(COMPLETED_SESSION_REQUEST)
        given(intent.getSerializableExtra("response")).willReturn(createSessionResponse("verified-token-session-url", VERIFIED_TOKEN_SESSION))
        given(intent.getSerializableExtra("error")).willReturn(null)

        sessionBroadcastReceiver.onReceive(context, intent)

        given(intent.getSerializableExtra("response")).willReturn(createSessionResponse("payments-cvc-session-url", PAYMENTS_CVC_SESSION))

        sessionBroadcastReceiver.onReceive(context, intent)

        val response = mapOf(
            PAYMENTS_CVC_SESSION to "payments-cvc-session-url",
            VERIFIED_TOKEN_SESSION to "verified-token-session-url"
        )

        verify(sessionResponseListener, atMost(1)).onRequestFinished(response, null)
    }

    @Test
    fun `should return first exception given multiple session types are requested`() {
        broadcastNumSessionTypesRequested(2)

        val expectedEx: AccessCheckoutError = mock()
        given(intent.action).willReturn(COMPLETED_SESSION_REQUEST)
        given(intent.getSerializableExtra("response")).willReturn(createSessionResponse("verified-token-session-url", VERIFIED_TOKEN_SESSION))
        given(intent.getSerializableExtra("error")).willReturn(expectedEx)

        sessionBroadcastReceiver.onReceive(context, intent)

        given(intent.getSerializableExtra("response")).willReturn(createSessionResponse("payments-cvc-session-url", PAYMENTS_CVC_SESSION))

        sessionBroadcastReceiver.onReceive(context, intent)

        verify(sessionResponseListener, atMost(1)).onRequestFinished(null, expectedEx)
    }

    @Test
    fun `should return null session given an error is received`() {
        val expectedEx: AccessCheckoutError = mock()
        given(intent.action).willReturn(COMPLETED_SESSION_REQUEST)
        given(intent.getSerializableExtra("error")).willReturn(expectedEx)
        given(intent.getSerializableExtra("session_type")).willReturn(VERIFIED_TOKEN_SESSION)

        sessionBroadcastReceiver.onReceive(context, intent)

        verify(sessionResponseListener).onRequestFinished(null, expectedEx)
    }

    @Test
    fun `should notify with error once when a response that is not a session response is received`() {
        val expectedEx: AccessCheckoutError = mock()
        given(intent.action).willReturn(COMPLETED_SESSION_REQUEST)
        given(intent.getSerializableExtra("response")).willReturn(TestObject("something"))
        given(intent.getSerializableExtra("error")).willReturn(expectedEx)
        given(intent.getSerializableExtra("session_type")).willReturn(VERIFIED_TOKEN_SESSION)

        sessionBroadcastReceiver.onReceive(context, intent)

        verify(sessionResponseListener, atMost(1)).onRequestFinished(null, expectedEx)
    }

    @Test
    fun `should notify with custom error when a response that is not a session response and error deserialize failure`() {
        val expectedEx: AccessCheckoutError? = null

        given(intent.action).willReturn(COMPLETED_SESSION_REQUEST)
        given(intent.getSerializableExtra("response")).willReturn(TestObject("something"))
        given(intent.getSerializableExtra("error")).willReturn(null)
        given(intent.getSerializableExtra("session_type")).willReturn(VERIFIED_TOKEN_SESSION)

        sessionBroadcastReceiver.onReceive(context, intent)

        verify(sessionResponseListener, atMost(1)).onRequestFinished(null, expectedEx)
    }

    private fun createSessionResponse(href: String, sessionType: SessionType): SessionResponseInfo {
        val response = SessionResponse(
            SessionResponse.Links(
                SessionResponse.Links.Endpoints(href), emptyArray()
            )
        )

        return SessionResponseInfo.Builder()
            .responseBody(response)
            .sessionType(sessionType)
            .build()
    }

    private fun broadcastNumSessionTypesRequested(numSessionTypes: Int) {
        given(intent.action).willReturn(NUM_OF_SESSION_TYPES_REQUESTED)
        given(intent.getIntExtra(NUMBER_OF_SESSION_TYPE_KEY, 0)).willReturn(numSessionTypes)
        sessionBroadcastReceiver.onReceive(context, intent)
    }

}

data class TestObject(val property: String) : Serializable