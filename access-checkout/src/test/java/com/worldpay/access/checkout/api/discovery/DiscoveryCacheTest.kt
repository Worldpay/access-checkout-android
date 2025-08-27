package com.worldpay.access.checkout.api.discovery

import java.net.URL
import org.junit.Assert.assertEquals
import org.junit.Assert.assertNull
import org.junit.Before
import org.junit.Test

class DiscoveryCacheTest {

    @Before
    fun setUp() {
        DiscoveryCache.results.clear()
        DiscoveryCache.responses.clear()
    }

    @Test
    fun `should be a singleton`() {
        val cache1 = DiscoveryCache
        val cache2 = DiscoveryCache
        assertEquals(cache1, cache2)
    }

    @Test
    fun `should store URL when saveResult is called`() {
        val expectedResult = URL("http://some-href")
        assertNull(DiscoveryCache.results["service:sessions,sessions:card"])

        DiscoveryCache.saveResult(DiscoverLinks.cardSessions, expectedResult)

        assertEquals(expectedResult, DiscoveryCache.results["service:sessions,sessions:card"])
    }

    @Test
    fun `should return URL when getResult is called and result is already in cache`() {
        val expectedResult = URL("http://some-href")
        DiscoveryCache.results["service:sessions,sessions:card"] = expectedResult

        assertEquals(expectedResult, DiscoveryCache.getResult(DiscoverLinks.cardSessions))
    }

    @Test
    fun `should return null when getResult is called and result is not in cache`() {
        assertNull(DiscoveryCache.getResult(DiscoverLinks.cardSessions))
    }

    @Test
    fun `should store response when saveResponse is called`() {
        val url = URL("http://localhost")
        val expectedResult = "some result"
        assertNull(DiscoveryCache.responses[url.toString()])

        DiscoveryCache.saveResponse(url, expectedResult)

        assertEquals(expectedResult, DiscoveryCache.responses[url.toString()])
    }

    @Test
    fun `should return response when getResponse is called and response is already in cache`() {
        val url = URL("http://localhost")
        val expectedResult = "some result"
        DiscoveryCache.responses[url.toString()] = expectedResult

        assertEquals(expectedResult, DiscoveryCache.getResponse(url))
    }

    @Test
    fun `should return null when getResponse is called and response is not in cache`() {
        val url = URL("http://localhost")

        assertNull(DiscoveryCache.getResponse(url))
    }
}
