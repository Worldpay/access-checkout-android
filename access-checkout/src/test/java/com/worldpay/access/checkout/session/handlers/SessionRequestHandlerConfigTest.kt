package com.worldpay.access.checkout.session.handlers

import android.content.Context
import com.worldpay.access.checkout.client.session.listener.SessionResponseListener
import java.net.MalformedURLException
import java.net.URL
import kotlin.test.assertEquals
import kotlin.test.assertFailsWith
import org.junit.Test
import org.mockito.Mockito.mock

class SessionRequestHandlerConfigTest {

    @Test
    fun `should be able to create instance of token config and persist the properties`() {
        val baseUrl = URL("http://base-url.com")
        val checkoutId = "checkout-id"
        val context = mock(Context::class.java)
        val externalSessionResponseListener = mock(SessionResponseListener::class.java)

        val tokenRequestHandlerConfig = SessionRequestHandlerConfig.Builder()
            .baseUrl(baseUrl)
            .checkoutId(checkoutId)
            .context(context)
            .externalSessionResponseListener(externalSessionResponseListener)
            .build()

        assertEquals(baseUrl, tokenRequestHandlerConfig.getBaseUrl())
        assertEquals(checkoutId, tokenRequestHandlerConfig.getCheckoutId())
        assertEquals(context, tokenRequestHandlerConfig.getContext())
        assertEquals(
            externalSessionResponseListener,
            tokenRequestHandlerConfig.getExternalSessionResponseListener()
        )
    }

    @Test
    fun `should throw an illegal argument exception when no baseUrl is passed to builder`() {
        val exception = assertFailsWith<IllegalArgumentException> {
            SessionRequestHandlerConfig.Builder()
                .checkoutId("checkout-id")
                .context(mock(Context::class.java))
                .externalSessionResponseListener(mock(SessionResponseListener::class.java))
                .build()
        }
        assertEquals("Expected base url to be provided but was not", exception.message)
    }

    @Test
    fun `should throw an illegal argument exception when no checkoutId is passed to builder`() {
        val exception = assertFailsWith<IllegalArgumentException> {
            SessionRequestHandlerConfig.Builder()
                .baseUrl(URL("http://base-url.com"))
                .context(mock(Context::class.java))
                .externalSessionResponseListener(mock(SessionResponseListener::class.java))
                .build()
        }
        assertEquals("Expected merchant id to be provided but was not", exception.message)
    }

    @Test
    fun `should throw an illegal argument exception when no context is passed to builder`() {
        val exception = assertFailsWith<IllegalArgumentException> {
            SessionRequestHandlerConfig.Builder()
                .baseUrl(URL("http://base-url.com"))
                .checkoutId("checkout-id")
                .externalSessionResponseListener(mock(SessionResponseListener::class.java))
                .build()
        }
        assertEquals("Expected context to be provided but was not", exception.message)
    }

    @Test
    fun `should throw an illegal argument exception when no external session response listener is passed to builder`() {
        val exception = assertFailsWith<IllegalArgumentException> {
            SessionRequestHandlerConfig.Builder()
                .baseUrl(URL("http://base-url.com"))
                .checkoutId("checkout-id")
                .context(mock(Context::class.java))
                .build()
        }
        assertEquals("Expected session response listener to be provided but was not", exception.message)
    }

    @Test
    fun `should throw an malformed url exception when malformed url is passed to builder`() {
        val exception = assertFailsWith<MalformedURLException> {
            SessionRequestHandlerConfig.Builder()
                .baseUrl(URL("malformed-url"))
                .checkoutId("checkout-id")
                .externalSessionResponseListener(mock(SessionResponseListener::class.java))
                .context(mock(Context::class.java))
                .build()
        }
        assertEquals("no protocol: malformed-url", exception.message)
    }
}
