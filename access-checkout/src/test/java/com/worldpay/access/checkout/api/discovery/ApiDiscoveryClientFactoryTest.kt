package com.worldpay.access.checkout.api.discovery

import org.junit.Test
import kotlin.test.assertNotEquals

class ApiDiscoveryClientFactoryTest {

    @Test
    fun shouldNotConstructAccessCheckoutDiscoveryClientAsASingleton() {
        val client1 = ApiDiscoveryClientFactory.getClient()
        val client2 = ApiDiscoveryClientFactory.getClient()
        assertNotEquals(client1, client2)
    }

}