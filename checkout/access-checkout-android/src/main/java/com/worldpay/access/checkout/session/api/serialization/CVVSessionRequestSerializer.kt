package com.worldpay.access.checkout.session.api.serialization

import com.worldpay.access.checkout.api.serialization.Serializer
import com.worldpay.access.checkout.session.api.request.CVVSessionRequest
import com.worldpay.access.checkout.session.api.request.SessionRequest
import org.json.JSONObject

/**
 * This class is responsible for serialising the request data for a Payments CVC Session
 */
internal class CVVSessionRequestSerializer : Serializer<SessionRequest> {

    override fun serialize(instance: SessionRequest): String {
        if (instance !is CVVSessionRequest) {
            throw IllegalArgumentException("could not serialize session request")
        }

        val root = JSONObject()
        root.put("cvc", instance.cvv)
        root.put("identity", instance.identity)
        return root.toString()
    }
}