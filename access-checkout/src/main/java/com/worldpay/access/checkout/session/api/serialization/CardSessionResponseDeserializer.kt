package com.worldpay.access.checkout.session.api.serialization

import com.worldpay.access.checkout.api.serialization.Deserializer
import com.worldpay.access.checkout.session.api.response.SessionResponse
import com.worldpay.access.checkout.session.api.response.SessionResponse.Links
import com.worldpay.access.checkout.session.api.response.SessionResponse.Links.Curies
import com.worldpay.access.checkout.session.api.response.SessionResponse.Links.Endpoints
import org.json.JSONObject

/**
 * This class is responsible for deserialising the response json from a request for a Card Session
 */
internal class CardSessionResponseDeserializer : Deserializer<SessionResponse>() {

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
            val curiesTemplated = toProperty(curies, "templated", Boolean::class)

            SessionResponse(
                Links(
                    Endpoints(href),
                    arrayOf(
                        Curies(curiesHref, curiesName, curiesTemplated)
                    )
                )
            )
        }
    }
}
