package com.worldpay.access.checkout.session.api.response

import com.worldpay.access.checkout.api.discovery.DiscoverLinks
import com.worldpay.access.checkout.client.session.model.SessionType.CARD
import com.worldpay.access.checkout.session.api.request.SessionRequest
import com.worldpay.access.checkout.session.api.request.SessionRequestInfo
import org.junit.Test
import org.mockito.kotlin.mock
import kotlin.test.assertEquals

class SessionResponseInfoTest {

    @Test
    fun `should be able to build a session request info object`() {
        val requestBody = mock<SessionRequest>()

        val sessionRequestInfo = SessionRequestInfo.Builder()
            .requestBody(requestBody)
            .sessionType(CARD)
            .discoverLinks(DiscoverLinks.cardSessions)
            .build()

        assertEquals(requestBody, sessionRequestInfo.requestBody)
        assertEquals(CARD, sessionRequestInfo.sessionType)
        assertEquals(DiscoverLinks.cardSessions, sessionRequestInfo.discoverLinks)
    }
}
