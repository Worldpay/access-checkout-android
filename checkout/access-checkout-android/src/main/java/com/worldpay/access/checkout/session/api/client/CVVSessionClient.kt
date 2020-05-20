package com.worldpay.access.checkout.session.api.client

import com.worldpay.access.checkout.BuildConfig
import com.worldpay.access.checkout.api.HttpClient
import com.worldpay.access.checkout.api.serialization.Deserializer
import com.worldpay.access.checkout.api.serialization.Serializer
import com.worldpay.access.checkout.session.api.request.SessionRequest
import com.worldpay.access.checkout.session.api.response.SessionResponse
import java.net.URL

/**
 * Client for a Payments CVC Session that retrieves a [SessionResponse] by making a post request
 * to Access Worldpay services.
 *
 * @property[deserializer] Used to deserialise the [SessionResponse]
 * @property[serializer] Used to serialise [SessionRequest]
 * @property[httpClient] Responsible for carrying out the HTTP request
 */
internal class CVVSessionClient(
    private val deserializer: Deserializer<SessionResponse>,
    private val serializer: Serializer<SessionRequest>,
    private val httpClient: HttpClient
) : SessionClient {

    /**
     * Uses the given httpClient to make a request to the API to retrieve a session response
     *
     * @param[url] the [URL] for the service
     * @param[request] [SessionRequest] containing session request info
     *
     * @return [SessionResponse] representation of response from the service
     */
    override fun getSessionResponse(url: URL, request: SessionRequest): SessionResponse? {
        val headers = HashMap<String, String>()
        headers[CONTENT_TYPE_HEADER] = SESSIONS_MEDIA_TYPE
        headers[ACCEPT_HEADER] = SESSIONS_MEDIA_TYPE
        headers[WP_SDK_PRODUCT_HEADER] = PRODUCT_NAME + BuildConfig.VERSION_NAME

        return httpClient.doPost(url, request, headers, serializer, deserializer)
    }

}