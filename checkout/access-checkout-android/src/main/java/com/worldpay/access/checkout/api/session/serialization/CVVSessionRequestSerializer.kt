package com.worldpay.access.checkout.api.session.serialization

import com.worldpay.access.checkout.api.serialization.Serializer
import com.worldpay.access.checkout.api.session.CVVSessionRequest
import com.worldpay.access.checkout.api.session.SessionRequest
import org.json.JSONObject

internal class CVVSessionRequestSerializer :
    Serializer<SessionRequest> {

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