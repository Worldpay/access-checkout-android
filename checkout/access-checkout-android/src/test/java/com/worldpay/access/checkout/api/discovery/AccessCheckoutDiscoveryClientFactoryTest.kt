package com.worldpay.access.checkout.api.discovery

import org.junit.Test
import kotlin.test.assertEquals

class AccessCheckoutDiscoveryClientFactoryTest {

    @Test
    fun shouldConstructAccessCheckoutDiscoveryClientAsSingleton() {
        val client1 = AccessCheckoutDiscoveryClientFactory.getClient()
        val client2 = AccessCheckoutDiscoveryClientFactory.getClient()

        assertEquals(client1, client2)
    }

    @Test
    fun shouldConstructAccessCheckoutCVVOnlyDiscoveryClientAsSingleton() {
        val client1 = AccessCheckoutDiscoveryClientFactory.getCVVOnlyClient()
        val client2 = AccessCheckoutDiscoveryClientFactory.getCVVOnlyClient()

        assertEquals(client1, client2)
    }
}