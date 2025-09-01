package com.worldpay.access.checkout.api.discovery

import com.worldpay.access.checkout.session.api.client.SESSIONS_MEDIA_TYPE
import org.junit.Test
import kotlin.test.assertEquals

class DiscoverLinksTest {

    @Test
    fun `discoverLinks cvcSessions should return keys used to discover service and endpoint for cvc sessions`() {
        val expectedKeySessionsService = "service:sessions"
        val expectedKeyCvcSessionsEndpoint = "sessions:paymentsCvc"

        assertEquals(2, DiscoverLinks.cvcSessions.endpoints.size)
        assertEquals(DiscoverLinks.cvcSessions.endpoints[0].key, expectedKeySessionsService)
        assertEquals(DiscoverLinks.cvcSessions.endpoints[1].key, expectedKeyCvcSessionsEndpoint)
    }

    @Test
    fun `discoverLinks cvcSessions should return headers for sessions service`() {
        assertEquals(DiscoverLinks.cvcSessions.endpoints[1].headers["Accept"], SESSIONS_MEDIA_TYPE)
        assertEquals(
            DiscoverLinks.cvcSessions.endpoints[1].headers["Content-Type"],
            SESSIONS_MEDIA_TYPE
        )
    }

    @Test
    fun `discoverLinks cardSessions should return keys used to discover service and endpoint for card sessions`() {
        val expectedKeySessionsService = "service:sessions"
        val expectedCardSessionsEndpoint = "sessions:card"

        assertEquals(2, DiscoverLinks.cvcSessions.endpoints.size)
        assertEquals(DiscoverLinks.cardSessions.endpoints[0].key, expectedKeySessionsService)
        assertEquals(DiscoverLinks.cardSessions.endpoints[1].key, expectedCardSessionsEndpoint)
    }

    @Test
    fun `discoverLinks cardSessions should return headers for sessions service`() {
        assertEquals(DiscoverLinks.cardSessions.endpoints[1].headers["Accept"], SESSIONS_MEDIA_TYPE)
        assertEquals(
            DiscoverLinks.cardSessions.endpoints[1].headers["Content-Type"],
            SESSIONS_MEDIA_TYPE
        )
    }

    @Test
    fun `discoverLinks cardBinDetails should return keys used to discover card bin details endpoint`() {
        val expectedKeyCardBinDetailsEndpoint = "cardBinPublic:binDetails"

        assertEquals(1, DiscoverLinks.cardBinDetails.endpoints.size)
        assertEquals(
            DiscoverLinks.cardBinDetails.endpoints[0].key,
            expectedKeyCardBinDetailsEndpoint
        )
    }
}
