package com.worldpay.access.checkout.api.discovery

import org.junit.Test
import kotlin.test.assertEquals
import kotlin.test.assertNotNull

class AccessCheckoutDiscoveryClientFactoryTest {

    @Test
    fun shouldConstructAccessCheckoutDiscoveryClient() {
        val client1 = AccessCheckoutDiscoveryClientFactory.getClient(DiscoverLinks.verifiedTokens)
        assertNotNull(client1)
    }

}