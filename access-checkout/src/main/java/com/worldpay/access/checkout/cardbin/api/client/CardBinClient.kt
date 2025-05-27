package com.worldpay.access.checkout.cardbin.api.client

import com.worldpay.access.checkout.api.HttpsClient
import com.worldpay.access.checkout.api.serialization.Deserializer
import com.worldpay.access.checkout.api.serialization.Serializer
import com.worldpay.access.checkout.cardbin.api.request.CardBinRequest
import com.worldpay.access.checkout.cardbin.api.response.CardBinResponse
import java.net.URL
import java.util.UUID

/**
 * Retrieves session for card flow
 *
 * @property[deserializer] Used to deserialize the [CardBinResponse]
 * @property[serializer] Used to serialise [CardBinRequest]
 * @property[httpsClient] Responsible for carrying out the HTTPS request
 */
internal class CardBinClient(
    private val deserializer: Deserializer<CardBinResponse>,
    private val serializer: Serializer<CardBinRequest>,
    private val httpsClient: HttpsClient
) {

    suspend fun getCardBinResponse(url: URL, request: CardBinRequest): CardBinResponse {
        val headers = HashMap<String, String>()
        headers[WP_API_VERSION] = WP_API_VERSION_VALUE
        headers[WP_CALLER_ID] = WP_CALLER_ID_VALUE
        // Change this correlation ID creation as it's hard coded
        headers[WP_CORRELATION_ID] = UUID.randomUUID().toString()
        return httpsClient.doPost(url, request, headers, serializer, deserializer)
    }
}

