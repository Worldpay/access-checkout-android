package com.worldpay.access.checkout.cardbin.api.client

import com.worldpay.access.checkout.api.HttpsClient
import com.worldpay.access.checkout.api.URLFactory
import com.worldpay.access.checkout.api.URLFactoryImpl
import com.worldpay.access.checkout.api.serialization.Deserializer
import com.worldpay.access.checkout.api.serialization.Serializer
import com.worldpay.access.checkout.cardbin.api.request.CardBinRequest
import com.worldpay.access.checkout.cardbin.api.response.CardBinResponse

/**
 * Client for retrieving card scheme details using a card BIN.
 *
 * @property[baseUrl] Used to determine which env to send request to
 * @property[httpsClient] Responsible for carrying out the HTTPS request
 * @property[deserializer] Used to deserialize the [CardBinResponse]
 * @property[serializer] Used to serialise [CardBinRequest]
 */
internal class CardBinClient(
    private val baseUrl: String,
    private val httpsClient: HttpsClient,
    private val deserializer: Deserializer<CardBinResponse>,
    private val serializer: Serializer<CardBinRequest>
) {

    internal companion object {
        private const val CARD_BIN_ENDPOINT = "public/card/bindetails"

        // Header constants
        internal const val WP_API_VERSION = "WP-Api-Version"
        internal const val WP_API_VERSION_VALUE = "1"
        internal const val WP_CALLER_ID = "WP-CallerId"
        internal const val WP_CALLER_ID_VALUE = "checkoutandroid"
        internal const val WP_CONTENT_TYPE = "Content-Type"
        internal const val WP_CONTENT_TYPE_VALUE = "application/json"
    }

    private val urlFactory: URLFactory = URLFactoryImpl()
    private val cardBinUrl = urlFactory.getURL("$baseUrl/$CARD_BIN_ENDPOINT")

    suspend fun getCardBinResponse(request: CardBinRequest): CardBinResponse {
        val headers = hashMapOf(
            WP_API_VERSION to WP_API_VERSION_VALUE,
            WP_CALLER_ID to WP_CALLER_ID_VALUE,
            WP_CONTENT_TYPE to WP_CONTENT_TYPE_VALUE
        )

        return httpsClient.doPost(cardBinUrl, request, headers, serializer, deserializer)
    }
}
