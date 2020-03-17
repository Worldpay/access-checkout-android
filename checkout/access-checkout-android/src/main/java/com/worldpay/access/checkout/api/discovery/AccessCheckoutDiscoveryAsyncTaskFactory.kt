package com.worldpay.access.checkout.api.discovery

import com.worldpay.access.checkout.api.Callback
import com.worldpay.access.checkout.api.HttpClient
import com.worldpay.access.checkout.api.serialization.LinkDiscoveryDeserializer

internal class AccessCheckoutDiscoveryAsyncTaskFactory() {

    fun getAsyncTask(callback: Callback<String>, discoverLinks: DiscoverLinks): AccessCheckoutDiscoveryAsyncTask {
        return AccessCheckoutDiscoveryAsyncTask(
            callback,
            LinkDiscoveryDeserializer(discoverLinks.service),
            LinkDiscoveryDeserializer(discoverLinks.endpoint),
            HttpClient())
    }

//    fun getAsyncCVVTask(callback: Callback<String>) : AccessCheckoutDiscoveryAsyncTask {
//        return AccessCheckoutDiscoveryAsyncTask(
//            callback,
//            LinkDiscoveryDeserializer(DiscoverLinks.sessions.service),
//            LinkDiscoveryDeserializer(DiscoverLinks.sessions.endpoint),
//            HttpClient())
//    }

}