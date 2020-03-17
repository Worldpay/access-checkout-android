package com.worldpay.access.checkout.api.discovery

internal object AccessCheckoutDiscoveryClientFactory {

    fun getClient(discoverLinks: DiscoverLinks): AccessCheckoutDiscoveryClient = AccessCheckoutDiscoveryClient(discoverLinks)

}