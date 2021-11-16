package com.worldpay.access.checkout.session.api.client

import com.worldpay.access.checkout.BuildConfig
import com.worldpay.access.checkout.api.HttpsClient
import com.worldpay.access.checkout.api.serialization.Deserializer
import com.worldpay.access.checkout.api.serialization.Serializer
import com.worldpay.access.checkout.session.api.request.SessionRequest
import com.worldpay.access.checkout.session.api.response.SessionResponse
import java.net.URL

/**
 * Retrieves session for card flow
 *
 * @property[deserializer] Used to deserialize the [SessionResponse]
 * @property[serializer] Used to serialise [SessionRequest]
 * @property[httpsClient] Responsible for carrying out the HTTPS request
 */
internal class CardSessionClient(
    private val deserializer: Deserializer<SessionResponse>,
    private val serializer: Serializer<SessionRequest>,
    private val httpsClient: HttpsClient
) : SessionClient {

    override suspend fun getSessionResponse(url: URL, request: SessionRequest): SessionResponse {
        val headers = HashMap<String, String>()
        headers[CONTENT_TYPE_HEADER] = VERIFIED_TOKENS_MEDIA_TYPE
        headers[ACCEPT_HEADER] = VERIFIED_TOKENS_MEDIA_TYPE
        headers[WP_SDK_PRODUCT_HEADER] = PRODUCT_NAME + BuildConfig.VERSION_NAME

        return httpsClient.doPost(url, request, headers, serializer, deserializer)
    }
}
