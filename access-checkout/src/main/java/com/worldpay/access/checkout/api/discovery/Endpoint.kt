package com.worldpay.access.checkout.api.discovery

import com.worldpay.access.checkout.api.serialization.LinkDiscoveryDeserializer
import java.io.Serializable

internal class Endpoint(val key: String, val headers: Map<String, String> = mapOf()) : Serializable {
    fun getDeserializer(): LinkDiscoveryDeserializer {
        return LinkDiscoveryDeserializer(key)
    }
}
