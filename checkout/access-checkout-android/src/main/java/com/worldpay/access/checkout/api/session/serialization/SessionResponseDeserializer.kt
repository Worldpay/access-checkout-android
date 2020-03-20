package com.worldpay.access.checkout.api.session.serialization

import com.worldpay.access.checkout.api.serialization.Deserializer
import com.worldpay.access.checkout.api.session.SessionResponse
import com.worldpay.access.checkout.api.session.SessionResponse.Links
import com.worldpay.access.checkout.api.session.SessionResponse.Links.Curies
import com.worldpay.access.checkout.api.session.SessionResponse.Links.VerifiedTokensSession
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
