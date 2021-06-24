package com.worldpay.access.checkout.api.discovery

import kotlin.test.assertNotEquals
import org.junit.Test

class ApiDiscoveryClientFactoryTest {

    @Test
    fun shouldNotConstructAccessCheckoutDiscoveryClientAsASingleton() {
        val client1 = ApiDiscoveryClientFactory.getClient()
        val client2 = ApiDiscoveryClientFactory.getClient()
        assertNotEquals(client1, client2)
    }
}
