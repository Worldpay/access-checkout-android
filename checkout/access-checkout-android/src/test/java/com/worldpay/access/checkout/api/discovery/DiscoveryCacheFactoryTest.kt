package com.worldpay.access.checkout.api.discovery

import org.junit.Test
import kotlin.test.assertEquals

class DiscoveryCacheFactoryTest {
    @Test
    fun `should create discovery cache as a singleton`() {
        val cache1 = DiscoveryCacheFactory.getCache()
        val cache2 = DiscoveryCacheFactory.getCache()
        assertEquals(cache1, cache2)
    }
}