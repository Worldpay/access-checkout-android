package com.worldpay.access.checkout.api.discovery

import org.junit.Test
import kotlin.test.assertEquals
import kotlin.test.assertNotEquals
import kotlin.test.assertNotNull

class AccessCheckoutDiscoveryClientFactoryTest {

    @Test
    fun shouldNotConstructAccessCheckoutDiscoveryClientAsASingleton() {
        val client1 = AccessCheckoutDiscoveryClientFactory.getClient()
        val client2 = AccessCheckoutDiscoveryClientFactory.getClient()
        assertNotEquals(client1, client2)
    }

}