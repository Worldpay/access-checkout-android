package com.worldpay.access.checkout.api.serialization

import com.worldpay.access.checkout.api.SessionRequest
import org.json.JSONObject

internal class SessionRequestSerializer : Serializer<SessionRequest> {

    override fun serialize(instance: SessionRequest): String {
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