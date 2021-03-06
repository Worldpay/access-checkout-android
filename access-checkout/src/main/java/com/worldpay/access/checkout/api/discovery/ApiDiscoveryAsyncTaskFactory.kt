package com.worldpay.access.checkout.api.discovery

import com.worldpay.access.checkout.api.Callback
import com.worldpay.access.checkout.api.HttpsClient

internal class ApiDiscoveryAsyncTaskFactory {

    fun getAsyncTask(callback: Callback<String>, discoverLinks: DiscoverLinks): ApiDiscoveryAsyncTask {
        return ApiDiscoveryAsyncTask(
            callback,
            discoverLinks.endpoints,
            HttpsClient()
        )
    }
}
