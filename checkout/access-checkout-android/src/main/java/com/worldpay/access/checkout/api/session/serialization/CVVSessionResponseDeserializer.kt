package com.worldpay.access.checkout.api.session.serialization

import com.worldpay.access.checkout.api.serialization.Deserializer
import com.worldpay.access.checkout.api.session.SessionResponse
import com.worldpay.access.checkout.api.session.SessionResponse.Links
import org.json.JSONObject

internal class CVVSessionResponseDeserializer : Deserializer<SessionResponse>() {

    override fun deserialize(json: String): SessionResponse {
        return super.deserialize(json) {
            val root = JSONObject(json)

            val links = fetchObject(root, "_links")

            val sessions = fetchObject(links, "sessions:session")
            val href = toStringProperty(sessions, "href")

            SessionResponse(
                Links(
                    Links.VerifiedTokensSession(href),
                    emptyArray()
                )
            )
        }
    }
}
