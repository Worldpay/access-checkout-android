package com.worldpay.access.checkout.session.handlers

import android.content.Context
import com.worldpay.access.checkout.client.session.listener.SessionResponseListener
import java.net.URL
import kotlin.test.assertEquals
import kotlin.test.assertTrue
import org.junit.Test
import org.mockito.Mockito

class SessionRequestHandlerFactoryTest {

    @Test
    fun `should return expected list of token request handlers`() {
        val config = SessionRequestHandlerConfig.Builder()
            .baseUrl(URL("http://base-url.com"))
            .checkoutId("checkout-id")
            .context(Mockito.mock(Context::class.java))
            .externalSessionResponseListener(Mockito.mock(SessionResponseListener::class.java))
            .build()

        val tokenRequestHandlerFactory =
            SessionRequestHandlerFactory(
                config
            )

        val handlers = tokenRequestHandlerFactory.getTokenHandlers()

        assertEquals(2, handlers.size)
        assertTrue(handlers[0] is CardSessionRequestHandler)
        assertTrue(handlers[1] is CvcSessionRequestHandler)
    }
}
