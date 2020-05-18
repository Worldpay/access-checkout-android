package com.worldpay.access.checkout.session.api.serialization

import com.worldpay.access.checkout.api.serialization.Serializer
import com.worldpay.access.checkout.session.api.request.CardSessionRequest
import com.worldpay.access.checkout.session.api.request.SessionRequest
import org.json.JSONObject

/**
 * [CardSessionRequestSerializer] is responsible for serialising the request data for a Verified Token Session
 */
internal class CardSessionRequestSerializer :
    Serializer<SessionRequest> {

    /**
     * @param[instance] - [SessionRequest] a representation of a request for a VT session
     * @return a serialised [String] representation of request for a VT session
     */
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