package com.worldpay.access.checkout.api

import android.content.Context
import android.content.Intent
import com.nhaarman.mockitokotlin2.mock
import com.worldpay.access.checkout.api.AccessCheckoutException.AccessCheckoutError
import com.worldpay.access.checkout.views.SessionResponseListener
import org.junit.Before
import org.junit.Test
import org.mockito.BDDMockito.given
import org.mockito.Mockito.*
import java.io.Serializable

class SessionReceiverTest {

    private lateinit var sessionResponseListener: SessionResponseListener
    private lateinit var sessionReceiver: SessionReceiver
    private lateinit var intent: Intent
    private lateinit var context: Context

    @Before
    fun setup() {
        sessionResponseListener = mock()
        intent = mock()
        context = mock()

        sessionReceiver = SessionReceiver(sessionResponseListener)
    }

    @Test
    fun `given an unknown intent, then session response listener is not notified`() {

        given(intent.action).willReturn("UNKNOWN_INTENT")

        sessionReceiver.onReceive(context, intent)

        verifyZeroInteractions(sessionResponseListener)
    }

    @Test
    fun `given empty session response and empty error is received then session response listener is notified with AccessCheckout custom exception`() {

        val expectedException = AccessCheckoutError("some error")
        given(intent.action).willReturn("com.worldpay.access.checkout.api.action.GET_SESSION")
        given(intent.getSerializableExtra("error")).willReturn(AccessCheckoutError("some error"))

        sessionReceiver.onReceive(context, intent)

        verify(sessionResponseListener, atMost(1))
            .onRequestFinished(null, expectedException)
    }


    @Test
    fun `given a session response is received then session response listener is notified with href`() {

        given(intent.action).willReturn("com.worldpay.access.checkout.api.action.GET_SESSION")
        given(intent.getSerializableExtra("response")).willReturn(
            SessionResponse(
                SessionResponse.Links(
                    SessionResponse.Links.VerifiedTokensSession(
                        "some reference"
                    ), emptyArray()
                )
            )
        )
        given(intent.getSerializableExtra("error")).willReturn(AccessCheckoutError("some error"))

        sessionReceiver.onReceive(context, intent)

        verify(sessionResponseListener).onRequestFinished("some reference", null)
    }

    @Test
    fun `given an error is received then session response listener is notified with null`() {

        val expectedEx: AccessCheckoutError = mock()
        given(intent.action).willReturn("com.worldpay.access.checkout.api.action.GET_SESSION")
        given(intent.getSerializableExtra("error")).willReturn(expectedEx)

        sessionReceiver.onReceive(context, intent)

        verify(sessionResponseListener).onRequestFinished(null, expectedEx)
    }

    @Test
    fun `given a response that is not a session response is received then session response listener is notified with error once`() {

        val expectedEx: AccessCheckoutError = mock()
        given(intent.action).willReturn("com.worldpay.access.checkout.api.action.GET_SESSION")
        given(intent.getSerializableExtra("response")).willReturn(TestObject("something"))
        given(intent.getSerializableExtra("error")).willReturn(expectedEx)

        sessionReceiver.onReceive(context, intent)


        verify(sessionResponseListener, atMost(1))
            .onRequestFinished(null, expectedEx)
    }

    @Test
    fun `given a response that is not a session response and error deserialize failure then session response listener is notified with custom error`() {

        val expectedEx: AccessCheckoutError? = null

        given(intent.action).willReturn("com.worldpay.access.checkout.api.action.GET_SESSION")
        given(intent.getSerializableExtra("response")).willReturn(TestObject("something"))
        given(intent.getSerializableExtra("error")).willReturn(null)

        sessionReceiver.onReceive(context, intent)


        verify(sessionResponseListener, atMost(1))
            .onRequestFinished(null, expectedEx)
    }

}

data class TestObject(val property: String) : Serializable