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
        assertNull(DiscoveryCache.results["service:verifiedTokens"])

        DiscoveryCache.saveResult(DiscoverLinks.verifiedTokens, expectedResult)

        assertEquals(expectedResult, DiscoveryCache.results["service:verifiedTokens"])
    }

    @Test
    fun `should return result when getResult is called`() {
        val expectedResult = URL("http://some-href")
        DiscoveryCache.results["service:verifiedTokens"] = expectedResult

        assertEquals(expectedResult, DiscoveryCache.getResult(DiscoverLinks.verifiedTokens))
    }

    @Test
    fun `should remove entry from results when clearResult is called`() {
        val service = "service:verifiedTokens"
        DiscoveryCache.results[service] = URL("http://some-href")
        assertNotNull(DiscoveryCache.results[service])

        DiscoveryCache.clearResult(DiscoverLinks.verifiedTokens)
        assertNull(DiscoveryCache.results[service])
    }
}
