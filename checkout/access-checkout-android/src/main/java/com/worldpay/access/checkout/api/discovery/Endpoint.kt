package com.worldpay.access.checkout.api.discovery

import com.worldpay.access.checkout.api.serialization.LinkDiscoveryDeserializer
import java.io.Serializable

internal class Endpoint(val endpoint: String) : Serializable {
    fun getDeserializer(): LinkDiscoveryDeserializer {
        return LinkDiscoveryDeserializer(endpoint)
    }
}