package com.worldpay.access.checkout.api.serialization

import com.worldpay.access.checkout.api.SessionResponse
import com.worldpay.access.checkout.api.SessionResponse.Links
import com.worldpay.access.checkout.api.SessionResponse.Links.Curies
import com.worldpay.access.checkout.api.SessionResponse.Links.VerifiedTokensSession
import org.json.JSONObject

internal class SessionResponseDeserializer: Deserializer<SessionResponse>() {

    override fun deserialize(json: String): SessionResponse {
        return super.deserialize(json) {
            val root = JSONObject(json)

            val links = fetchObject(root, "_links")

            val verifiedTokenSession = fetchObject(links, "verifiedTokens:session")
            val href = toStringProperty(verifiedTokenSession, "href")

            val curiesRoot = fetchArray(links, "curies")
            val curies = curiesRoot.getJSONObject(0)
            val curiesHref = toStringProperty(curies, "href")
            val curiesName = toStringProperty(curies, "name")
            val curiesTemplated = toBooleanProperty(curies, "templated")

            SessionResponse(
                Links(
                    VerifiedTokensSession(href),
                    arrayOf(
                        Curies(curiesHref, curiesName, curiesTemplated)
                    )
                )
            )
        }
    }
}
