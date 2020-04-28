package com.worldpay.access.checkout.session.request.broadcast

import android.content.Context
import android.content.Intent
import com.nhaarman.mockitokotlin2.mock
import com.worldpay.access.checkout.api.AccessCheckoutException.AccessCheckoutError
import com.worldpay.access.checkout.api.session.SessionResponse
import com.worldpay.access.checkout.session.request.broadcast.receivers.SessionBroadcastReceiver
import com.worldpay.access.checkout.views.SessionResponseListener
import org.junit.Before
import org.junit.Test
import org.mockito.BDDMockito.given
import org.mockito.Mockito.*
import java.io.Serializable

class SessionBroadcastReceiverTest {

    private lateinit var sessionResponseListener: SessionResponseListener
    private lateinit var sessionBroadcastReceiver: SessionBroadcastReceiver
    private lateinit var intent: Intent
    private lateinit var context: Context
    
    private val action = SessionBroadcastReceiver::class.java.name

    @Before
    fun setup() {
        sessionResponseListener = mock()
        intent = mock()
        context = mock()

        sessionBroadcastReceiver = SessionBroadcastReceiver(sessionResponseListener)
    }

    @Test
    fun `should not notify if the intent is not recognised`() {
        given(intent.action).willReturn("UNKNOWN_INTENT")

        sessionBroadcastReceiver.onReceive(context, intent)

        verifyZeroInteractions(sessionResponseListener)
    }

    @Test
    fun `should return empty session and exception when session response is empty`() {
        given(intent.action).willReturn(action)
        given(intent.getSerializableExtra("error")).willReturn(AccessCheckoutError("some error"))

        sessionBroadcastReceiver.onReceive(context, intent)

        verify(sessionResponseListener, atMost(1))
            .onRequestFinished(null, AccessCheckoutError("some error"))
    }


    @Test
    fun `should return href given a session response is received`() {
        given(intent.action).willReturn(action)
        given(intent.getSerializableExtra("response")).willReturn(
            SessionResponse(
                SessionResponse.Links(
                    SessionResponse.Links.Endpoints(
                        "some reference"
                    ), emptyArray()
                )
            )
        )
        given(intent.getSerializableExtra("error")).willReturn(AccessCheckoutError("some error"))

        sessionBroadcastReceiver.onReceive(context, intent)

        verify(sessionResponseListener).onRequestFinished("some reference", null)
    }

    @Test
    fun `should return null session given an error is received`() {
        val expectedEx: AccessCheckoutError = mock()
        given(intent.action).willReturn(action)
        given(intent.getSerializableExtra("error")).willReturn(expectedEx)

        sessionBroadcastReceiver.onReceive(context, intent)

        verify(sessionResponseListener).onRequestFinished(null, expectedEx)
    }

    @Test
    fun `should notify with error once when a response that is not a session response is received`() {
        val expectedEx: AccessCheckoutError = mock()
        given(intent.action).willReturn(action)
        given(intent.getSerializableExtra("response")).willReturn(
            TestObject(
                "something"
            )
        )
        given(intent.getSerializableExtra("error")).willReturn(expectedEx)

        sessionBroadcastReceiver.onReceive(context, intent)


        verify(sessionResponseListener, atMost(1))
            .onRequestFinished(null, expectedEx)
    }

    @Test
    fun `should notify with custom error when a response that is not a session response and error deserialize failure`() {
        val expectedEx: AccessCheckoutError? = null

        given(intent.action).willReturn(action)
        given(intent.getSerializableExtra("response")).willReturn(
            TestObject(
                "something"
            )
        )
        given(intent.getSerializableExtra("error")).willReturn(null)

        sessionBroadcastReceiver.onReceive(context, intent)


        verify(sessionResponseListener, atMost(1))
            .onRequestFinished(null, expectedEx)
    }

}

data class TestObject(val property: String) : Serializable