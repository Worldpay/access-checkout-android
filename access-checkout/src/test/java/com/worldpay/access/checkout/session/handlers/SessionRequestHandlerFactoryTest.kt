package com.worldpay.access.checkout.session.handlers

import android.content.Context
import com.worldpay.access.checkout.client.session.listener.SessionResponseListener
import org.junit.Test
import org.mockito.Mockito
import kotlin.test.assertEquals
import kotlin.test.assertTrue

class SessionRequestHandlerFactoryTest {

    @Test
    fun `should return expected list of token request handlers`() {
        val config = SessionRequestHandlerConfig.Builder()
            .baseUrl("base-url")
            .merchantId("merchant-id")
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
