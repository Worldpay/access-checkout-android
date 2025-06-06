package com.worldpay.access.checkout.session.broadcast.receivers

import android.content.Context
import android.content.Intent
import com.worldpay.access.checkout.client.api.exception.AccessCheckoutException
import com.worldpay.access.checkout.client.session.listener.SessionResponseListener
import com.worldpay.access.checkout.client.session.model.SessionType
import com.worldpay.access.checkout.client.session.model.SessionType.CARD
import com.worldpay.access.checkout.client.session.model.SessionType.CVC
import com.worldpay.access.checkout.session.api.response.SessionResponse
import com.worldpay.access.checkout.session.api.response.SessionResponseInfo
import com.worldpay.access.checkout.session.broadcast.receivers.SessionBroadcastReceiver.Companion.NUMBER_OF_SESSION_TYPE_KEY
import com.worldpay.access.checkout.testutils.PlainRobolectricTestRunner
import org.junit.Before
import org.junit.Test
import org.junit.runner.RunWith
import org.mockito.BDDMockito.given
import org.mockito.Mockito.verifyNoInteractions
import org.mockito.kotlin.atMost
import org.mockito.kotlin.mock
import org.mockito.kotlin.verify
import org.mockito.kotlin.verifyNoMoreInteractions
import java.io.Serializable
import kotlin.test.assertEquals
import kotlin.test.assertFailsWith
import kotlin.test.assertFalse
import kotlin.test.assertTrue

@RunWith(PlainRobolectricTestRunner::class)
class SessionBroadcastReceiverTest {

    private lateinit var externalSessionResponseListener: SessionResponseListener
    private lateinit var sessionBroadcastReceiver: SessionBroadcastReceiver
    private lateinit var intent: Intent
    private lateinit var context: Context

    @Before
    fun setup() {
        externalSessionResponseListener = mock()
        intent = mock()
        context = mock()

        sessionBroadcastReceiver = SessionBroadcastReceiver(externalSessionResponseListener)
        SessionBroadcastDataStore.clear()
    }

    @Test
    fun `should throw exception when using dummy listener on success`() {
        broadcastNumSessionTypesRequested(1)

        given(intent.action).willReturn(COMPLETED_SESSION_REQUEST)
        given(intent.getSerializableExtra("response")).willReturn(
            createSessionResponse(
                "some reference",
                CARD
            )
        )
        given(intent.getSerializableExtra("error")).willReturn(null)

        val sessionBroadcastReceiver = SessionBroadcastReceiver()

        val ex = assertFailsWith(
            exceptionClass = AccessCheckoutException::class,
            block = { sessionBroadcastReceiver.onReceive(context, intent) }
        )

        assertEquals("You are required to implement your own SessionResponseListener.", ex.message)
    }

    @Test
    fun `should throw exception when using dummy listener on error`() {
        broadcastNumSessionTypesRequested(1)

        given(intent.action).willReturn(COMPLETED_SESSION_REQUEST)
        given(intent.getSerializableExtra("error")).willReturn(
            AccessCheckoutException(
                "some error"
            )
        )
        given(intent.getSerializableExtra("session_type")).willReturn(CARD)

        val sessionBroadcastReceiver = SessionBroadcastReceiver()

        val ex = assertFailsWith(
            exceptionClass = AccessCheckoutException::class,
            block = { sessionBroadcastReceiver.onReceive(context, intent) }
        )

        assertEquals("You are required to implement your own SessionResponseListener.", ex.message)
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
    fun `should not notify externalSessionResponseListener if the intent is not recognised`() {
        given(intent.action).willReturn("UNKNOWN_INTENT")

        sessionBroadcastReceiver.onReceive(context, intent)

        verifyNoInteractions(externalSessionResponseListener)
    }

    @Test
    fun `should not notify externalSessionResponseListener if the intent is recognised but no externalSessionResponseListener was set`() {
        given(intent.action).willReturn("COMPLETED_SESSION_REQUEST")

        sessionBroadcastReceiver.onReceive(context, intent)

        verifyNoInteractions(externalSessionResponseListener)
    }

    @Test
    fun `should return empty session and exception when session response is empty`() {
        broadcastNumSessionTypesRequested(1)

        given(intent.action).willReturn(COMPLETED_SESSION_REQUEST)
        given(intent.getSerializableExtra("error")).willReturn(
            AccessCheckoutException(
                "some error"
            )
        )
        given(intent.getSerializableExtra("session_type")).willReturn(CARD)

        sessionBroadcastReceiver.onReceive(context, intent)

        verify(externalSessionResponseListener, atMost(1))
            .onError(AccessCheckoutException("some error"))
    }

    @Test
    fun `should return AccessCheckoutException with unknown error when casting serializable to AccessCheckoutException fails`() {
        broadcastNumSessionTypesRequested(1)

        given(intent.action).willReturn(COMPLETED_SESSION_REQUEST)
        given(intent.getSerializableExtra("error")).willReturn(
            IllegalArgumentException(
                "some error"
            )
        )
        given(intent.getSerializableExtra("session_type")).willReturn(CARD)

        sessionBroadcastReceiver.onReceive(context, intent)

        verify(externalSessionResponseListener, atMost(1))
            .onError(AccessCheckoutException("Unknown error"))
    }


    @Test
    fun `should return href given a session response is received`() {
        broadcastNumSessionTypesRequested(1)

        given(intent.action).willReturn(COMPLETED_SESSION_REQUEST)
        given(intent.getSerializableExtra("response")).willReturn(
            createSessionResponse(
                "some reference",
                CARD
            )
        )
        given(intent.getSerializableExtra("error")).willReturn(null)

        sessionBroadcastReceiver.onReceive(context, intent)

        val response = mapOf(CARD to "some reference")

        verify(externalSessionResponseListener, atMost(1)).onSuccess(response)
    }

    @Test
    fun `should be able to keep request data for new instances of the receiver`() {
        broadcastNumSessionTypesRequested(2)

        given(intent.action).willReturn(COMPLETED_SESSION_REQUEST)
        given(intent.getSerializableExtra("response")).willReturn(
            createSessionResponse(
                "verified-token-session-url",
                CARD
            )
        )
        given(intent.getSerializableExtra("error")).willReturn(null)

        val sessionBroadcastReceiver = SessionBroadcastReceiver(externalSessionResponseListener)
        sessionBroadcastReceiver.onReceive(context, intent)

        given(intent.getSerializableExtra("response")).willReturn(
            createSessionResponse(
                "payments-cvc-session-url",
                CVC
            )
        )

        val sessionBroadcastReceiver2 = SessionBroadcastReceiver(externalSessionResponseListener)
        sessionBroadcastReceiver2.onReceive(context, intent)

        val response = mapOf(
            CVC to "payments-cvc-session-url",
            CARD to "verified-token-session-url"
        )

        verify(externalSessionResponseListener, atMost(1)).onSuccess(response)
    }

    @Test
    fun `should return all href given multiple session types are requested`() {
        broadcastNumSessionTypesRequested(2)

        given(intent.action).willReturn(COMPLETED_SESSION_REQUEST)
        given(intent.getSerializableExtra("response")).willReturn(
            createSessionResponse(
                "verified-token-session-url",
                CARD
            )
        )
        given(intent.getSerializableExtra("error")).willReturn(null)

        sessionBroadcastReceiver.onReceive(context, intent)

        given(intent.getSerializableExtra("response")).willReturn(
            createSessionResponse(
                "payments-cvc-session-url",
                CVC
            )
        )

        sessionBroadcastReceiver.onReceive(context, intent)

        val response = mapOf(
            CVC to "payments-cvc-session-url",
            CARD to "verified-token-session-url"
        )

        verify(externalSessionResponseListener, atMost(1)).onSuccess(response)
    }

    @Test
    fun `should return first exception given multiple session types are requested`() {
        broadcastNumSessionTypesRequested(2)

        val expectedEx: AccessCheckoutException = mock()
        given(intent.action).willReturn(COMPLETED_SESSION_REQUEST)
        given(intent.getSerializableExtra("response")).willReturn(null)
        given(intent.getSerializableExtra("error")).willReturn(expectedEx)

        sessionBroadcastReceiver.onReceive(context, intent)

        verify(externalSessionResponseListener, atMost(1)).onError(expectedEx)

        given(intent.getSerializableExtra("response")).willReturn(
            createSessionResponse(
                "payments-cvc-session-url",
                CVC
            )
        )

        sessionBroadcastReceiver.onReceive(context, intent)

        verifyNoMoreInteractions(externalSessionResponseListener)
    }

    @Test
    fun `should return null session given an error is received`() {
        broadcastNumSessionTypesRequested(2)

        val expectedEx: AccessCheckoutException = mock()
        given(intent.action).willReturn(COMPLETED_SESSION_REQUEST)
        given(intent.getSerializableExtra("error")).willReturn(expectedEx)
        given(intent.getSerializableExtra("response")).willReturn(
            createSessionResponse(
                "verified-token-session-url",
                CARD
            )
        )

        sessionBroadcastReceiver.onReceive(context, intent)

        verify(externalSessionResponseListener, atMost(1)).onError(expectedEx)
    }

    @Test
    fun `should notify with error once when a response that is not a session response is received`() {
        broadcastNumSessionTypesRequested(2)

        val expectedEx: AccessCheckoutException = mock()
        given(intent.action).willReturn(COMPLETED_SESSION_REQUEST)
        given(intent.getSerializableExtra("response")).willReturn(TestObject("something"))
        given(intent.getSerializableExtra("error")).willReturn(expectedEx)

        sessionBroadcastReceiver.onReceive(context, intent)

        verify(externalSessionResponseListener, atMost(1)).onError(expectedEx)
    }

    @Test
    fun `should notify with custom error when a response that is not a session response and error deserialize failure`() {
        broadcastNumSessionTypesRequested(2)

        val AccessCheckoutException = AccessCheckoutException("")
        val expectedEx =
            AccessCheckoutException("Unknown error", AccessCheckoutException)

        given(intent.action).willReturn(COMPLETED_SESSION_REQUEST)
        given(intent.getSerializableExtra("response")).willReturn(TestObject("something"))
        given(intent.getSerializableExtra("error")).willReturn(AccessCheckoutException)

        sessionBroadcastReceiver.onReceive(context, intent)

        verify(externalSessionResponseListener, atMost(1)).onError(expectedEx)
    }

    @Test
    fun `should be able to add and retrieve responses from the data store`() {
        assertTrue(SessionBroadcastDataStore.getResponses().isEmpty())

        SessionBroadcastDataStore.addResponse(CARD, "href")

        assertFalse(SessionBroadcastDataStore.getResponses().isEmpty())
        assertEquals("href", SessionBroadcastDataStore.getResponses()[CARD])
    }

    @Test
    fun `should be able to set the number of session types and check if all requests are completed`() {
        SessionBroadcastDataStore.setNumberOfSessionTypes(2)
        assertFalse(SessionBroadcastDataStore.allRequestsCompleted())

        SessionBroadcastDataStore.addResponse(CARD, "href")
        assertFalse(SessionBroadcastDataStore.allRequestsCompleted())

        SessionBroadcastDataStore.addResponse(CVC, "href")
        assertTrue(SessionBroadcastDataStore.allRequestsCompleted())
    }

    @Test
    fun `should be able to clear values in data store`() {
        SessionBroadcastDataStore.setNumberOfSessionTypes(2)
        SessionBroadcastDataStore.addResponse(CARD, "vt-href")
        SessionBroadcastDataStore.addResponse(CVC, "payments-cvc-href")

        assertEquals("vt-href", SessionBroadcastDataStore.getResponses()[CARD])
        assertEquals("payments-cvc-href", SessionBroadcastDataStore.getResponses()[CVC])
        assertTrue(SessionBroadcastDataStore.allRequestsCompleted())
        assertTrue(SessionBroadcastDataStore.isExpectingResponse())

        SessionBroadcastDataStore.clear()

        assertTrue(SessionBroadcastDataStore.getResponses().isEmpty())
        assertFalse(SessionBroadcastDataStore.isExpectingResponse())
        assertFalse(SessionBroadcastDataStore.allRequestsCompleted())
    }

    private fun createSessionResponse(href: String, sessionType: SessionType): SessionResponseInfo {
        val response =
            SessionResponse(
                SessionResponse.Links(
                    SessionResponse.Links.Endpoints(
                        href
                    ),
                    emptyArray()
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
