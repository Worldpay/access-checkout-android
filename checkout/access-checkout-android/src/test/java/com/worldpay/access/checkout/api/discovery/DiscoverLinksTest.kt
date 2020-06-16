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

    @Test
    fun `discoverLinks sessions should return headers for sessions service`() {
        val expectedAcceptHeader = "application/vnd.worldpay.sessions-v1.hal+json"
        val expectedContentHeader = "application/vnd.worldpay.sessions-v1.hal+json"

        assertEquals(DiscoverLinks.sessions.headers["Accept"], expectedAcceptHeader)
        assertEquals(DiscoverLinks.sessions.headers["Content-Type"], expectedContentHeader)

    }

    @Test
    fun `discoverLinks verified tokens should return headers for verifiedTokens service`() {
        val expectedAcceptHeader = "application/vnd.worldpay.verified-tokens-v1.hal+json"
        val expectedContentHeader = "application/vnd.worldpay.verified-tokens-v1.hal+json"

        assertEquals(DiscoverLinks.verifiedTokens.headers["Accept"], expectedAcceptHeader)
        assertEquals(DiscoverLinks.verifiedTokens.headers["Content-Type"], expectedContentHeader)
    }
}
