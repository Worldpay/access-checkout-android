package com.worldpay.access.checkout.session.api.client

import com.worldpay.access.checkout.BuildConfig
import com.worldpay.access.checkout.api.HttpClient
import com.worldpay.access.checkout.api.serialization.Deserializer
import com.worldpay.access.checkout.api.serialization.Serializer
import com.worldpay.access.checkout.session.api.request.SessionRequest
import com.worldpay.access.checkout.session.api.response.SessionResponse
import java.net.URL

/**
 * [CardSessionClient] is the client for a Verified Token Session
 *
 * @param deserializer - [Deserializer] - passed to the [HttpClient] to deserialise [SessionResponse]
 * @param serializer - [Serializer] - passed to the [HttpClient] to serialise [SessionRequest]
 * @param httpClient - [HttpClient] - responsible for carrying out the HTTP request
 */
internal class CardSessionClient(
    private val deserializer: Deserializer<SessionResponse>,
    private val serializer: Serializer<SessionRequest>,
    private val httpClient: HttpClient
) : SessionClient {

    /**
     * Method for requesting a session from the VT service, returns a [SessionResponse]
     *
     * @param url - the URL for the service
     * @param request - [SessionRequest] containing session request info
     */
    override fun getSessionResponse(url: URL, request: SessionRequest): SessionResponse? {
        val headers = HashMap<String, String>()
        headers[CONTENT_TYPE_HEADER] = VERIFIED_TOKENS_MEDIA_TYPE
        headers[ACCEPT_HEADER] = VERIFIED_TOKENS_MEDIA_TYPE
        headers[WP_SDK_PRODUCT_HEADER] = PRODUCT_NAME + BuildConfig.VERSION_NAME

        return httpClient.doPost(url, request, headers, serializer, deserializer)
    }

}