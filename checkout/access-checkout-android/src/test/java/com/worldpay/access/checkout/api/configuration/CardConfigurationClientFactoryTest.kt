package com.worldpay.access.checkout.api.configuration

import org.junit.Assert.*
import org.junit.Test

class CardConfigurationClientFactoryTest {

    @Test
    fun shouldCardConfigurationClientInstance() {
        val client = CardConfigurationClientFactory.createClient()

        assertNotNull(client)
    }
}