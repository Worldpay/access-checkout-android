package com.worldpay.access.checkout.api.discovery

import com.worldpay.access.checkout.api.AsyncTaskResult

internal object DiscoveryCache {

    val results: MutableMap<String, AsyncTaskResult<String>?> = mutableMapOf()

    fun getResult(discoverLinks: DiscoverLinks): AsyncTaskResult<String>? {
        return results[discoverLinks.endpoints[0].endpoint]
    }

    fun setResult(result: AsyncTaskResult<String>?, discoverLinks: DiscoverLinks) {
        results[discoverLinks.endpoints[0].endpoint] = result
    }
}