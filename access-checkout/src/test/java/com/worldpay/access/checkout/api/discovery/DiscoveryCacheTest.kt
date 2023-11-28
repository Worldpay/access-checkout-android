package com.worldpay.access.checkout.api.discovery

import java.net.URL
import org.junit.Assert.assertEquals
import org.junit.Assert.assertNotNull
import org.junit.Assert.assertNull
import org.junit.Before
import org.junit.Test

class DiscoveryCacheTest {

    @Before
    fun setUp() {
        DiscoveryCache.results.clear()
    }

    @Test
    fun `should be a singleton`() {
        val cache1 = DiscoveryCache
        val cache2 = DiscoveryCache
        assertEquals(cache1, cache2)
    }

    @Test
    fun `should set value when setResult is called`() {
        val expectedResult = URL("http://some-href")
        assertNull(DiscoveryCache.results["service:sessions,sessions:card"])

        DiscoveryCache.saveResult(DiscoverLinks.cardSessions, expectedResult)

        assertEquals(expectedResult, DiscoveryCache.results["service:sessions,sessions:card"])
    }

    @Test
    fun `should return result when getResult is called`() {
        val expectedResult = URL("http://some-href")
        DiscoveryCache.results["service:sessions,sessions:card"] = expectedResult

        assertEquals(expectedResult, DiscoveryCache.getResult(DiscoverLinks.cardSessions))
    }

    @Test
    fun `should remove entry from results when clearResult is called`() {
        val key = "service:sessions,sessions:card"
        DiscoveryCache.results[key] = URL("http://some-href")
        assertNotNull(DiscoveryCache.results[key])

        DiscoveryCache.clearResult(DiscoverLinks.cardSessions)
        assertNull(DiscoveryCache.results[key])
    }
}
