package com.worldpay.access.checkout.api.discovery

internal object AccessCheckoutDiscoveryClientFactory {

    private val accessCheckoutDiscoveryClient: AccessCheckoutDiscoveryClient =
        AccessCheckoutDiscoveryClient(AccessCheckoutDiscoveryAsyncTaskFactory())

    fun getClient(): AccessCheckoutDiscoveryClient = accessCheckoutDiscoveryClient

}