package com.worldpay.access.checkout.api.discovery

import com.worldpay.access.checkout.session.api.client.SESSIONS_MEDIA_TYPE
import com.worldpay.access.checkout.session.api.client.VERIFIED_TOKENS_MEDIA_TYPE
import org.junit.Test
import kotlin.test.assertEquals

class DiscoverLinksTest {

    @Test
    fun `discoverLinks sessions should return service and endpoint for sessions service`() {
        val expectedService = "service:sessions"
        val expectedEndpoint = "sessions:paymentsCvc"

        assertEquals(DiscoverLinks.sessions.endpoints[0].endpoint, expectedService)
        assertEquals(DiscoverLinks.sessions.endpoints[1].endpoint, expectedEndpoint)

    }

    @Test
    fun `discoverLinks verified tokens should return service and endpoint for verifiedTokens service`() {
        val expectedService = "service:verifiedTokens"
        val expectedEndpoint = "verifiedTokens:sessions"

        assertEquals(DiscoverLinks.verifiedTokens.endpoints[0].endpoint, expectedService)
        assertEquals(DiscoverLinks.verifiedTokens.endpoints[1].endpoint, expectedEndpoint)
    }

    @Test
    fun `discoverLinks sessions should return headers for sessions service`() {
        assertEquals(DiscoverLinks.sessions.endpoints[1].headers["Accept"], SESSIONS_MEDIA_TYPE)
        assertEquals(DiscoverLinks.sessions.endpoints[1].headers["Content-Type"], SESSIONS_MEDIA_TYPE)
    }

    @Test
    fun `discoverLinks verified tokens should return headers for verifiedTokens service`() {
        assertEquals(DiscoverLinks.verifiedTokens.endpoints[1].headers["Accept"], VERIFIED_TOKENS_MEDIA_TYPE)
        assertEquals(DiscoverLinks.verifiedTokens.endpoints[1].headers["Content-Type"], VERIFIED_TOKENS_MEDIA_TYPE)
    }
}
