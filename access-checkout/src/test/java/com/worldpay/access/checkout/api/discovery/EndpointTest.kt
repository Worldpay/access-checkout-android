package com.worldpay.access.checkout.api.discovery

import kotlin.test.assertEquals
import kotlin.test.assertNotNull
import org.junit.Test

class EndpointTest {

    private val expectedEndpoint = "some-endpoint"
    private val endpoint = Endpoint(expectedEndpoint)

    @Test
    fun `should return an instance of LinkDiscoveryDeserializer when get deserializer method is called`() {
        val deserializer = endpoint.getDeserializer()
        assertNotNull(deserializer)
    }

    @Test
    fun `should return a string value when getting value for key`() {
        assertEquals(endpoint.key, expectedEndpoint)
    }
}
