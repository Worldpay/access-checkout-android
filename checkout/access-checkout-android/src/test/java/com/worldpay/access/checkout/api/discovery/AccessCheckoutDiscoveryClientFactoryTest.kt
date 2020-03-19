package com.worldpay.access.checkout.api.discovery

import org.junit.Test
import kotlin.test.assertEquals
import kotlin.test.assertNotNull

class AccessCheckoutDiscoveryClientFactoryTest {

    @Test
    fun shouldConstructAccessCheckoutDiscoveryClientAsASingleton() {
        val client1 = AccessCheckoutDiscoveryClientFactory.getClient()
        val client2 = AccessCheckoutDiscoveryClientFactory.getClient()
        assertEquals(client1, client2)
    }

}