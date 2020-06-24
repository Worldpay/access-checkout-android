package com.worldpay.access.checkout.api.discovery

import com.worldpay.access.checkout.api.AsyncTaskResult
import java.util.concurrent.ConcurrentHashMap

/**
 * This class is a cache representation for storing the results of attempted API discoveries
 */
internal object DiscoveryCache {

    val results: ConcurrentHashMap<String, AsyncTaskResult<String>?> = ConcurrentHashMap()

    /**
     * Retrieves a stored [AsyncTaskResult] with a [String] generic type from the cache
     *
     * @param[discoverLinks] A [DiscoverLinks] object as the key for the stored result
     * @return [AsyncTaskResult] that is stored in the cache
     */
    fun getResult(discoverLinks: DiscoverLinks): AsyncTaskResult<String>? {
        return results[discoverLinks.endpoints[0].endpoint]
    }

    /**
     * Saves a new result into the cache, mapping the [discoverLinks] to the [result]
     *
     * @param[discoverLinks] A [DiscoverLinks] object as the key to be stored
     * @param[result] An [AsyncTaskResult] with a [String] generic type to be stored in the cache for the [discoverLinks] key
     */
    fun saveResult(discoverLinks: DiscoverLinks, result: AsyncTaskResult<String>?) {
        results[discoverLinks.endpoints[0].endpoint] = result
    }

    /**
     * Clears a given [discoverLinks] key from the cache
     *
     * @param[discoverLinks] A [DiscoverLinks] object as the key to be cleared
     */
    fun clearResult(discoverLinks: DiscoverLinks) {
        results.remove(discoverLinks.endpoints[0].endpoint)
    }

}