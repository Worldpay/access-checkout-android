package com.worldpay.access.checkout.api.discovery

import org.junit.Test
import kotlin.test.assertNotNull

class EndpointTest {
    @Test
    fun `should return an instance of LinkDiscoveryDeserializer when get deserializer method is called`() {
        val deserializer = Endpoint("some endpoint").getDeserializer()
        assertNotNull(deserializer)
    }
}