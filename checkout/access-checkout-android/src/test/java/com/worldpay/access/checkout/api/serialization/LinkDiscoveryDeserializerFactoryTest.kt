package com.worldpay.access.checkout.api.serialization

import org.junit.Test
import kotlin.test.assertEquals
import kotlin.test.assertNotNull

class LinkDiscoveryDeserializerFactoryTest {
    @Test
    fun shouldConstructLinkDiscoveryDeserializer() {
        val deserializer = LinkDiscoveryDeserializerFactory().getDeserializer("test")
        assertNotNull(deserializer)
    }
}