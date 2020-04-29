package com.worldpay.access.checkout.api.discovery

internal object ApiDiscoveryClientFactory {
    fun getClient(): ApiDiscoveryClient = ApiDiscoveryClient((ApiDiscoveryAsyncTaskFactory()))
}