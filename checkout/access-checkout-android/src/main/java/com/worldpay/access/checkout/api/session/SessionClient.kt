package com.worldpay.access.checkout.api.session

import com.worldpay.access.checkout.BuildConfig
import com.worldpay.access.checkout.api.HttpClient
import com.worldpay.access.checkout.api.SessionResponse
import com.worldpay.access.checkout.api.serialization.Deserializer
import com.worldpay.access.checkout.api.serialization.Serializer
import java.net.URL

internal interface SessionClient {
    fun getSessionResponse(url: URL, request: SessionRequest): SessionResponse?
}

internal class SessionClientImpl(
    private val deserializer: Deserializer<SessionResponse>,
    private val serializer: Serializer<SessionRequest>,
    private val httpClient: HttpClient
) : SessionClient {

    override fun getSessionResponse(url: URL, request: SessionRequest): SessionResponse? {
        val headers = HashMap<String, String>()
        headers[CONTENT_TYPE_HEADER] =
            VERIFIED_TOKENS_MEDIA_TYPE
        headers[ACCEPT_HEADER] =
            VERIFIED_TOKENS_MEDIA_TYPE
        headers[WP_SDK_PRODUCT_HEADER] = PRODUCT_NAME + BuildConfig.VERSION_NAME

        return httpClient.doPost(url, request, headers, serializer, deserializer)
    }

    companion object {
        private const val VERIFIED_TOKENS_MEDIA_TYPE = "application/vnd.worldpay.verified-tokens-v1.hal+json"
        private const val PRODUCT_NAME = "access-checkout-android/"
        private const val CONTENT_TYPE_HEADER = "Content-Type"
        private const val ACCEPT_HEADER = "Accept"
        private const val WP_SDK_PRODUCT_HEADER = "X-WP-SDK"
    }

}