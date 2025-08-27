package com.worldpay.access.checkout.api.serialization

internal object PlainResponseDeserializer : Deserializer<String>() {

    override fun deserialize(json: String): String {
        return json
    }
}
