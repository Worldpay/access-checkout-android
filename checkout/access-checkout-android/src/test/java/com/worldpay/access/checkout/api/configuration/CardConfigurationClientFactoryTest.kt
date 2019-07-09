package com.worldpay.access.checkout.api.configuration

import org.hamcrest.Matchers.instanceOf
import org.junit.Assert.assertNotNull
import org.junit.Assert.assertThat
import org.junit.Test

class CardConfigurationClientFactoryTest {

    @Test
    fun shouldCardConfigurationClientInstance() {
        val client = CardConfigurationClientFactory.createClient()

        assertNotNull(client)
        assertThat(client, instanceOf(CardConfigurationClient::class.java))
    }
}