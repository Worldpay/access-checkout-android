package com.worldpay.access.checkout.api.serialization

internal class LinkDiscoveryDeserializerFactory() {
    fun getDeserializer(namespace: String): LinkDiscoveryDeserializer {
        return LinkDiscoveryDeserializer(namespace)
    }
}
