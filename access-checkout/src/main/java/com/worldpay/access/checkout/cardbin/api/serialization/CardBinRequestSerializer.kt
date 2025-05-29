package com.worldpay.access.checkout.cardbin.api.serialization

import com.worldpay.access.checkout.api.serialization.Serializer
import com.worldpay.access.checkout.cardbin.api.request.CardBinRequest
import org.json.JSONObject

/**
 * This class is responsible for serializing the request data for a Card Bin API request
 */
internal class CardBinRequestSerializer : Serializer<CardBinRequest> {

    override fun serialize(instance: CardBinRequest): String {
        val root = JSONObject()
        root.put("cardNumber", instance.cardNumber)
        root.put("checkoutId", instance.checkoutId)
        return root.toString()
    }
}