package com.worldpay.access.checkout.api.discovery

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
}
