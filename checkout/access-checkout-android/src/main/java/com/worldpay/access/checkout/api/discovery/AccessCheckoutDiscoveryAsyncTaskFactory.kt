package com.worldpay.access.checkout.api.discovery

import com.worldpay.access.checkout.api.Callback
import com.worldpay.access.checkout.api.HttpClient

internal class AccessCheckoutDiscoveryAsyncTaskFactory {

    fun getAsyncTask(callback: Callback<String>, discoverLinks: DiscoverLinks): AccessCheckoutDiscoveryAsyncTask {

        return AccessCheckoutDiscoveryAsyncTask(
            callback,
            discoverLinks.endpoints,
            HttpClient()
        )
    }
}

