package com.worldpay.access.checkout.api.discovery

import org.junit.Test
import kotlin.test.assertEquals

class DiscoverLinksTest {

    @Test
    fun `discoverLinks sessions should return service and endpoint for sessions service`() {
        val expected_service = "service:sessions"
        val expected_endpoint = "sessions:paymentsCvc"

        assertEquals(DiscoverLinks.sessions.endpoint, expected_endpoint)
        assertEquals(DiscoverLinks.sessions.service, expected_service)
    }

    @Test
    fun `discoverLinks verified tokens should return service and endpoint for verifiedTokens service`() {
        val expected_service = "service:verifiedTokens"
        val expected_endpoint = "verifiedTokens:sessions"

        assertEquals(DiscoverLinks.verifiedTokens.endpoint, expected_endpoint)
        assertEquals(DiscoverLinks.verifiedTokens.service, expected_service)
    }
}