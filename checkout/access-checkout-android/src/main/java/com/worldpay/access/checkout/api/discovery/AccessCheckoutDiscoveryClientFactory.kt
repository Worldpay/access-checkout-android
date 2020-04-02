package com.worldpay.access.checkout.api.discovery

internal object AccessCheckoutDiscoveryClientFactory {
    fun getClient(): AccessCheckoutDiscoveryClient = AccessCheckoutDiscoveryClient((AccessCheckoutDiscoveryAsyncTaskFactory()))
}