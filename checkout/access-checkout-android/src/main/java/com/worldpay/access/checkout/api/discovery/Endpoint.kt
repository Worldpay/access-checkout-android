package com.worldpay.access.checkout.api.discovery

import com.worldpay.access.checkout.api.serialization.LinkDiscoveryDeserializer

internal class Endpoint(val endpoint: String) {
    fun getDeserializer(): LinkDiscoveryDeserializer {
        return LinkDiscoveryDeserializer(endpoint)
    }
}