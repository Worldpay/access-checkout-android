package com.worldpay.access.checkout.session.api.serialization

import com.worldpay.access.checkout.api.serialization.Serializer
import com.worldpay.access.checkout.session.api.request.CvcSessionRequest
import com.worldpay.access.checkout.session.api.request.SessionRequest
import org.json.JSONObject

/**
 * This class is responsible for serialising the request data for a Payments CVC Session
 */
internal class CvcSessionRequestSerializer : Serializer<SessionRequest> {

    override fun serialize(instance: SessionRequest): String {
        if (instance !is CvcSessionRequest) {
            throw IllegalArgumentException("could not serialize session request")
        }

        val root = JSONObject()
        root.put("cvc", instance.cvc)
        root.put("identity", instance.identity)
        return root.toString()
    }
}
