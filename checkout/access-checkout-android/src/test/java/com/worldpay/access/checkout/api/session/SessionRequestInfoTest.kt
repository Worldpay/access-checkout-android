package com.worldpay.access.checkout.api.session

import com.nhaarman.mockitokotlin2.mock
import com.worldpay.access.checkout.client.SessionType.VERIFIED_TOKEN_SESSION
import org.junit.Test
import kotlin.test.assertEquals

class SessionRequestInfoTest {

    @Test
    fun `should be able to build a session response info object`() {
        val responseBody = mock<SessionResponse>()

        val sessionRequestInfo = SessionResponseInfo.Builder()
            .responseBody(responseBody)
            .sessionType(VERIFIED_TOKEN_SESSION)
            .build()

        assertEquals(responseBody, sessionRequestInfo.responseBody)
        assertEquals(VERIFIED_TOKEN_SESSION, sessionRequestInfo.sessionType)
    }

}