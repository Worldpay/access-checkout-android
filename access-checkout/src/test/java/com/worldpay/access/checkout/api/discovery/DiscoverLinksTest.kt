package com.worldpay.access.checkout.api.discovery

import com.worldpay.access.checkout.session.api.client.SESSIONS_MEDIA_TYPE
import kotlin.test.assertEquals
import org.junit.Test

class DiscoverLinksTest {

    @Test
    fun `discoverLinks cvcSessions should return keys used to discover service and endpoint for cvc sessions`() {
        val expectedService = "service:sessions"
        val expectedEndpoint = "sessions:paymentsCvc"

        assertEquals(DiscoverLinks.cvcSessions.endpoints[0].key, expectedService)
        assertEquals(DiscoverLinks.cvcSessions.endpoints[1].key, expectedEndpoint)
    }

    @Test
    fun `discoverLinks cvcSessions should return headers for sessions service`() {
        assertEquals(DiscoverLinks.cvcSessions.endpoints[1].headers["Accept"], SESSIONS_MEDIA_TYPE)
        assertEquals(DiscoverLinks.cvcSessions.endpoints[1].headers["Content-Type"], SESSIONS_MEDIA_TYPE)
    }

    @Test
    fun `discoverLinks cardSessions should return keys used to discover service and endpoint for card sessions`() {
        val expectedService = "service:sessions"
        val expectedEndpoint = "sessions:card"

        assertEquals(DiscoverLinks.cardSessions.endpoints[0].key, expectedService)
        assertEquals(DiscoverLinks.cardSessions.endpoints[1].key, expectedEndpoint)
    }

    @Test
    fun `discoverLinks cardSessions should return headers for sessions service`() {
        assertEquals(DiscoverLinks.cardSessions.endpoints[1].headers["Accept"], SESSIONS_MEDIA_TYPE)
        assertEquals(DiscoverLinks.cardSessions.endpoints[1].headers["Content-Type"], SESSIONS_MEDIA_TYPE)
    }
}
