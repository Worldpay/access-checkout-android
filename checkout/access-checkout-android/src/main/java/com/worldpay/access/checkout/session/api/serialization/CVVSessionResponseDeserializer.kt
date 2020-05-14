package com.worldpay.access.checkout.session.api.serialization

import com.worldpay.access.checkout.api.serialization.Deserializer
import com.worldpay.access.checkout.session.api.response.SessionResponse
import com.worldpay.access.checkout.session.api.response.SessionResponse.Links
import org.json.JSONObject

/**
 * [CVVSessionRequestDeserializer] is responsible for deserialising the response json from a request for a Payments CVC Session
 */
internal class CVVSessionResponseDeserializer : Deserializer<SessionResponse>() {

    /**
     * Method returns a [SessionResponse] containing deserialised response data from Payments CVC session request
     *
     * @param json - json response from Payments CVC Session request
     */
    override fun deserialize(json: String): SessionResponse {
        return super.deserialize(json) {
            val root = JSONObject(json)

            val links = fetchObject(root, "_links")

            val verifiedTokenSession = fetchObject(links, "sessions:session")
            val href = toStringProperty(verifiedTokenSession, "href")

            val curiesRoot = fetchArray(links, "curies")
            val curies = curiesRoot.getJSONObject(0)
            val curiesHref = toStringProperty(curies, "href")
            val curiesName = toStringProperty(curies, "name")
            val curiesTemplated = toProperty(curies, "templated", Boolean::class)

            SessionResponse(
                Links(
                    Links.Endpoints(href),
                    arrayOf(
                        Links.Curies(curiesHref, curiesName, curiesTemplated)
                    )
                )
            )
        }
    }
}
