package com.worldpay.access.checkout.api.discovery

import java.net.URL
import java.util.concurrent.ConcurrentHashMap
import java.util.stream.Collectors

/**
 * This class is a cache representation for storing the results of attempted API discoveries
 */
internal object DiscoveryCache {

    val results: ConcurrentHashMap<String, URL> = ConcurrentHashMap()

    /**
     * Retrieves a stored [URL] endpoint with a [URL] generic type from the cache
     *
     * @param[discoverLinks] A [DiscoverLinks] object as the key for the stored result
     * @return [URL] endpoint that is stored in the cache
     */
    fun getResult(discoverLinks: DiscoverLinks): URL? {
        return results[convertToKey(discoverLinks)]
    }

    /**
     * Saves a new result into the cache, mapping the [discoverLinks] to the [result]
     *
     * @param[discoverLinks] A [DiscoverLinks] object as the key to be stored
     * @param[result] A [URL] generic type to be stored in the cache for the [discoverLinks] key representing the endpoint
     */
    fun saveResult(discoverLinks: DiscoverLinks, result: URL) {
        results[convertToKey(discoverLinks)] = result
    }

    /**
     * Clears a given [discoverLinks] key from the cache
     *
     * @param[discoverLinks] A [DiscoverLinks] object as the key to be cleared
     */
    fun clearResult(discoverLinks: DiscoverLinks) {
        results.remove(convertToKey(discoverLinks))
    }

    /**
     * Converts a DiscoverLinks into a comma-separate String made of each EndPoint endpoint
     * This String is used to store or lookup a result in the results Map
     */
    private fun convertToKey(discoverLinks: DiscoverLinks) =
        discoverLinks.endpoints.stream()
            .map { it.endpoint }
            .collect(Collectors.joining(","))
}
