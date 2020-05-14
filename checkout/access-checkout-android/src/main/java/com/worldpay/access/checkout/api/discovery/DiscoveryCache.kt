package com.worldpay.access.checkout.api.discovery

import com.worldpay.access.checkout.api.AsyncTaskResult
import java.util.concurrent.ConcurrentHashMap

/**
 * [DiscoveryCache] is a cache for storing the results of attempted API discoveries
 */

internal object DiscoveryCache {

    val results: ConcurrentHashMap<String, AsyncTaskResult<String>?> = ConcurrentHashMap()

    fun getResult(discoverLinks: DiscoverLinks): AsyncTaskResult<String>? {
        return results[discoverLinks.endpoints[0].endpoint]
    }

    fun saveResult(discoverLinks: DiscoverLinks, result: AsyncTaskResult<String>?) {
        results[discoverLinks.endpoints[0].endpoint] = result
    }

    fun clearResult(discoverLinks: DiscoverLinks) {
        results.remove(discoverLinks.endpoints[0].endpoint)
    }
}