package com.worldpay.access.checkout.session.request.handlers

import android.content.Context
import com.worldpay.access.checkout.views.SessionResponseListener
import org.junit.Test
import org.mockito.Mockito.mock
import kotlin.test.assertEquals
import kotlin.test.assertFailsWith

class SessionRequestHandlerConfigTest {

    @Test
    fun `should be able to create instance of token config and persist the properties`() {
        val baseUrl = "base-url"
        val merchantId = "merchant-id"
        val context = mock(Context::class.java)
        val externalSessionResponseListener = mock(SessionResponseListener::class.java)

        val tokenRequestHandlerConfig = SessionRequestHandlerConfig.Builder()
            .baseUrl(baseUrl)
            .merchantId(merchantId)
            .context(context)
            .externalSessionResponseListener(externalSessionResponseListener)
            .build()

        assertEquals(baseUrl, tokenRequestHandlerConfig.getBaseUrl())
        assertEquals(merchantId, tokenRequestHandlerConfig.getMerchantId())
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
                .merchantId("merchant-id")
                .context(mock(Context::class.java))
                .externalSessionResponseListener(mock(SessionResponseListener::class.java))
                .build()
        }
        assertEquals("Expected base url to be provided but was not", exception.message)
    }

    @Test
    fun `should throw an illegal argument exception when no merchantId is passed to builder`() {
        val exception = assertFailsWith<IllegalArgumentException> {
            SessionRequestHandlerConfig.Builder()
                .baseUrl("base-url")
                .context(mock(Context::class.java))
                .externalSessionResponseListener(mock(SessionResponseListener::class.java))
                .build()
        }
        assertEquals("Expected merchant ID to be provided but was not", exception.message)
    }

    @Test
    fun `should throw an illegal argument exception when no context is passed to builder`() {
        val exception = assertFailsWith<IllegalArgumentException> {
            SessionRequestHandlerConfig.Builder()
                .baseUrl("base-url")
                .merchantId("merchant-id")
                .externalSessionResponseListener(mock(SessionResponseListener::class.java))
                .build()
        }
        assertEquals("Expected context to be provided but was not", exception.message)
    }

    @Test
    fun `should throw an illegal argument exception when no external session response listener is passed to builder`() {
        val exception = assertFailsWith<IllegalArgumentException> {
            SessionRequestHandlerConfig.Builder()
                .baseUrl("base-url")
                .merchantId("merchant-id")
                .context(mock(Context::class.java))
                .build()
        }
        assertEquals("Expected session response listener to be provided but was not", exception.message)
    }


}