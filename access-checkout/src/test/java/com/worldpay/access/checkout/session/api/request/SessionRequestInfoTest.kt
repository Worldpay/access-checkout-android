package com.worldpay.access.checkout.session.api.request

import com.nhaarman.mockitokotlin2.mock
import com.worldpay.access.checkout.client.session.model.SessionType.CARD
import com.worldpay.access.checkout.session.api.response.SessionResponse
import com.worldpay.access.checkout.session.api.response.SessionResponseInfo
import org.junit.Test
import kotlin.test.assertEquals

class SessionRequestInfoTest {

    @Test
    fun `should be able to build a session response info object`() {
        val responseBody = mock<SessionResponse>()

        val sessionRequestInfo = SessionResponseInfo.Builder()
            .responseBody(responseBody)
            .sessionType(CARD)
            .build()

        assertEquals(responseBody, sessionRequestInfo.responseBody)
        assertEquals(CARD, sessionRequestInfo.sessionType)
    }

}
