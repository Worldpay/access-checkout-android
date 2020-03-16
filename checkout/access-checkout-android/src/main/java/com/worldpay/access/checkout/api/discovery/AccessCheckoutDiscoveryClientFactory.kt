package com.worldpay.access.checkout.api.discovery

internal object AccessCheckoutDiscoveryClientFactory {

    private val accessCheckoutDiscoveryClient: AccessCheckoutDiscoveryClient =
        AccessCheckoutDiscoveryClient(AccessCheckoutDiscoveryAsyncTaskFactory())

    private val accessCheckoutCVVOnlyDiscoveryClient: AccessCheckoutCVVOnlyDiscoveryClient =
        AccessCheckoutCVVOnlyDiscoveryClient((AccessCheckoutDiscoveryAsyncTaskFactory()))

    fun getClient(): AccessCheckoutDiscoveryClient = accessCheckoutDiscoveryClient

    fun getCVVOnlyClient(): AccessCheckoutCVVOnlyDiscoveryClient = accessCheckoutCVVOnlyDiscoveryClient

}