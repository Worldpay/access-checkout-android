package com.worldpay.access.checkout.cardbin.api.serialization

import com.worldpay.access.checkout.api.serialization.Deserializer
import com.worldpay.access.checkout.cardbin.api.response.CardBinResponse
import org.json.JSONObject

/**
 * This class is responsible for deserializing the response json from a Card Bin API request
 */

internal class CardBinResponseDeserializer : Deserializer<CardBinResponse>() {

    override fun deserialize(json: String): CardBinResponse {
        return super.deserialize(json) {
            val root = JSONObject(json)

            CardBinResponse(
                brand = fetchArray(root, "brand").let { array ->
                    (0 until array.length()).map { array.getString(it) }
                },
                fundingType = toStringProperty(root, "fundingType"),
                luhnCompliant = toProperty(root, "luhnCompliant",  Boolean::class)
            )
        }
    }
}
