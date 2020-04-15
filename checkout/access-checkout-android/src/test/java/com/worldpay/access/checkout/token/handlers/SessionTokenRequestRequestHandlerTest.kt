package com.worldpay.access.checkout.token.handlers

import com.worldpay.access.checkout.client.token.TokenRequest.SESSION_TOKEN
import com.worldpay.access.checkout.client.token.TokenRequest.VERIFIED_TOKEN
import org.junit.Test
import kotlin.test.assertFalse
import kotlin.test.assertTrue

class SessionTokenRequestRequestHandlerTest {

    @Test
    fun `should be able to handle a session token request`() {
        val handler = SessionTokenRequestRequestHandler()
        assertTrue { handler.canHandle(listOf(SESSION_TOKEN)) }
    }

    @Test
    fun `should not be able to handle a verified token request`() {
        val handler = SessionTokenRequestRequestHandler()
        assertFalse { handler.canHandle(listOf(VERIFIED_TOKEN)) }
    }

}