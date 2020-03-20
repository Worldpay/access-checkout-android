package com.worldpay.access.checkout.api.session.serialization

import com.worldpay.access.checkout.api.serialization.Serializer
import com.worldpay.access.checkout.api.session.CardSessionRequest
import com.worldpay.access.checkout.api.session.SessionRequest
import org.json.JSONObject

internal class CardSessionRequestSerializer :
    Serializer<SessionRequest> {

    override fun serialize(instance: SessionRequest): String {
        if (instance !is CardSessionRequest) {
            throw IllegalArgumentException("could not serialize session request")
        }

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