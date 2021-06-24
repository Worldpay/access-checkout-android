package com.worldpay.access.checkout.api.serialization

import org.json.JSONObject

internal class LinkDiscoveryDeserializer(private val namespace: String) : Deserializer<String>() {

    override fun deserialize(json: String): String {
        return super.deserialize(json) {
            val root = JSONObject(json)

            val links = fetchObject(root, "_links")

            val name = fetchObject(links, namespace)
            val href = toStringProperty(name, "href")

            href
        }
    }
}
