package com.worldpay.access.checkout.token

import android.content.Context
import com.worldpay.access.checkout.token.handlers.SessionTokenRequestRequestHandler
import com.worldpay.access.checkout.token.handlers.VerifiedTokenRequestRequestHandler
import com.worldpay.access.checkout.views.SessionResponseListener
import org.junit.Test
import org.mockito.Mockito
import kotlin.test.assertEquals
import kotlin.test.assertTrue

class TokenRequestHandlerFactoryTest {

    @Test
    fun `should return expected list of token request handlers`() {
        val config = TokenRequestHandlerConfig.Builder()
            .baseUrl("base-url")
            .merchantId("merchant-id")
            .context(Mockito.mock(Context::class.java))
            .externalSessionResponseListener(Mockito.mock(SessionResponseListener::class.java))
            .build()

        val tokenRequestHandlerFactory = TokenRequestHandlerFactory(config)

        val handlers = tokenRequestHandlerFactory.getTokenHandlers()

        assertEquals(2, handlers.size)
        assertTrue(handlers[0] is VerifiedTokenRequestRequestHandler)
        assertTrue(handlers[1] is SessionTokenRequestRequestHandler)
    }


}