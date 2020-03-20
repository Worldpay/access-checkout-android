package com.worldpay.access.checkout.api.serialization

import com.worldpay.access.checkout.api.session.CardSessionRequest
import org.json.JSONObject

internal class CardSessionRequestSerializer : Serializer<CardSessionRequest> {

    override fun serialize(instance: CardSessionRequest): String {
        val root = JSONObject()

        val expiryDate = JSONObject()
        expiryDate.put("month", instance.cardExpiryDate.month)
        expiryDate.put("year", instance.cardExpiryDate.year)

        root.put("cardNumber", instance.cardNumber)
        root.put("cardExpiryDate", expiryDate)
        root.put("cvc", instance.cvv)
        root.put("identity", instance.identity)
        return root.toString()
    }
}