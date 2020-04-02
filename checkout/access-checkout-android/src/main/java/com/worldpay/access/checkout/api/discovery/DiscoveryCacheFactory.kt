package com.worldpay.access.checkout.api.discovery

internal object DiscoveryCacheFactory {
    private val discoveryCache: DiscoveryCache  = DiscoveryCache()

    fun getCache(): DiscoveryCache {
        return discoveryCache
    }
}