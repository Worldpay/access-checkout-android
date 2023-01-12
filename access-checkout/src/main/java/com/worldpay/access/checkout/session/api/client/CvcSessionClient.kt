package com.worldpay.access.checkout.session.api.client

import com.worldpay.access.checkout.api.HttpsClient
import com.worldpay.access.checkout.api.serialization.Deserializer
import com.worldpay.access.checkout.api.serialization.Serializer
import com.worldpay.access.checkout.session.api.request.SessionRequest
import com.worldpay.access.checkout.session.api.response.SessionResponse
import java.net.URL

/**
 * Retrieves session for cvc only flow
 *
 * @property[deserializer] Used to deserialise the [SessionResponse]
 * @property[serializer] Used to serialise [SessionRequest]
 * @property[httpsClient] Responsible for carrying out the HTTPS request
 */
internal class CvcSessionClient(
    private val deserializer: Deserializer<SessionResponse>,
    private val serializer: Serializer<SessionRequest>,
    private val httpsClient: HttpsClient
) : SessionClient {

    override suspend fun getSessionResponse(url: URL, request: SessionRequest): SessionResponse {
        val headers = HashMap<String, String>()
        headers[CONTENT_TYPE_HEADER] = SESSIONS_MEDIA_TYPE
        headers[ACCEPT_HEADER] = SESSIONS_MEDIA_TYPE
        headers[WpSdkHeader.name] = WpSdkHeader.value

        return httpsClient.doPost(url, request, headers, serializer, deserializer)
    }
}
