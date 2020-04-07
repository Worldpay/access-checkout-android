package com.worldpay.access.checkout.api.discovery

import org.junit.Test
import kotlin.test.assertEquals
import kotlin.test.assertNotNull

class EndpointTest {

    private val expectedEndpoint = "some-endpoint"
    private val endpoint = Endpoint(expectedEndpoint)

    @Test
    fun `should return an instance of LinkDiscoveryDeserializer when get deserializer method is called`() {
        val deserializer = endpoint.getDeserializer()
        assertNotNull(deserializer)
    }

    @Test
    fun `should be able to return a string value for endpoint`() {
        assertEquals(endpoint.endpoint, expectedEndpoint)
    }

}