package com.worldpay.access.checkout.session.api.response

import com.nhaarman.mockitokotlin2.mock
import com.worldpay.access.checkout.api.discovery.DiscoverLinks
import com.worldpay.access.checkout.client.session.model.SessionType.CARD
import com.worldpay.access.checkout.session.api.request.SessionRequest
import com.worldpay.access.checkout.session.api.request.SessionRequestInfo
import org.junit.Test
import kotlin.test.assertEquals

class SessionResponseInfoTest {

    @Test
    fun `should be able to build a session request info object`() {
        val requestBody = mock<SessionRequest>()

        val sessionRequestInfo = SessionRequestInfo.Builder()
            .baseUrl("base-url")
            .requestBody(requestBody)
            .sessionType(CARD)
            .discoverLinks(DiscoverLinks.verifiedTokens)
            .build()

        assertEquals("base-url", sessionRequestInfo.baseUrl)
        assertEquals(requestBody, sessionRequestInfo.requestBody)
        assertEquals(CARD, sessionRequestInfo.sessionType)
        assertEquals(DiscoverLinks.verifiedTokens, sessionRequestInfo.discoverLinks)
    }

}
