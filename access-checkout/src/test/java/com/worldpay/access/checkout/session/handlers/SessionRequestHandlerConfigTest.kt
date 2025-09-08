package com.worldpay.access.checkout.session.handlers

import android.content.Context
import com.worldpay.access.checkout.client.api.exception.AccessCheckoutException
import com.worldpay.access.checkout.client.session.listener.SessionResponseListener
import org.junit.Test
import org.mockito.Mockito.mock
import kotlin.test.assertEquals
import kotlin.test.assertFailsWith

class SessionRequestHandlerConfigTest {

    @Test
    fun `should be able to create instance of session request config and persist the properties`() {
        val checkoutId = "checkout-id"
        val context = mock(Context::class.java)
        val externalSessionResponseListener = mock(SessionResponseListener::class.java)

        val sessionRequestHandlerConfig = SessionRequestHandlerConfig.Builder()
            .checkoutId(checkoutId)
            .context(context)
            .externalSessionResponseListener(externalSessionResponseListener)
            .build()

        assertEquals(checkoutId, sessionRequestHandlerConfig.getCheckoutId())
        assertEquals(context, sessionRequestHandlerConfig.getContext())
        assertEquals(
            externalSessionResponseListener,
            sessionRequestHandlerConfig.getExternalSessionResponseListener()
        )
    }

    @Test
    fun `should throw an AccessCheckoutException when no checkoutId is passed to builder`() {
        val exception = assertFailsWith<AccessCheckoutException> {
            SessionRequestHandlerConfig.Builder()
                .context(mock(Context::class.java))
                .externalSessionResponseListener(mock(SessionResponseListener::class.java))
                .build()
        }
        assertEquals("Expected merchant id to be provided but was not", exception.message)
    }

    @Test
    fun `should throw an AccessCheckoutException when no context is passed to builder`() {
        val exception = assertFailsWith<AccessCheckoutException> {
            SessionRequestHandlerConfig.Builder()
                .checkoutId("checkout-id")
                .externalSessionResponseListener(mock(SessionResponseListener::class.java))
                .build()
        }
        assertEquals("Expected context to be provided but was not", exception.message)
    }

    @Test
    fun `should throw an AccessCheckoutException when no external session response listener is passed to builder`() {
        val exception = assertFailsWith<AccessCheckoutException> {
            SessionRequestHandlerConfig.Builder()
                .checkoutId("checkout-id")
                .context(mock(Context::class.java))
                .build()
        }
        assertEquals(
            "Expected session response listener to be provided but was not",
            exception.message
        )
    }
}
