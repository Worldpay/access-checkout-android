package com.worldpay.access.checkout.cardbin.api.client

import com.worldpay.access.checkout.api.HttpsClient
import com.worldpay.access.checkout.api.URLFactory
import com.worldpay.access.checkout.api.URLFactoryImpl
import com.worldpay.access.checkout.api.serialization.Deserializer
import com.worldpay.access.checkout.api.serialization.Serializer
import com.worldpay.access.checkout.cardbin.api.request.CardBinRequest
import com.worldpay.access.checkout.cardbin.api.response.CardBinResponse
import com.worldpay.access.checkout.cardbin.api.serialization.CardBinRequestSerializer
import com.worldpay.access.checkout.cardbin.api.serialization.CardBinResponseDeserializer
import java.net.URL

/**
 * Retrieves the card schemes
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
    }

    private val urlFactory: URLFactory = URLFactoryImpl()
    private val cardBinUrl = urlFactory.getURL("$baseUrl/$CARD_BIN_ENDPOINT")

    suspend fun getCardBinResponse(request: CardBinRequest): CardBinResponse {
        val headers = HashMap<String, String>()
        headers[WP_API_VERSION] = WP_API_VERSION_VALUE
        headers[WP_CALLER_ID] = WP_CALLER_ID_VALUE
        headers[WP_CONTENT_TYPE] = WP_CONTENT_TYPE_VALUE
        return httpsClient.doPost(cardBinUrl, request, headers, serializer, deserializer)
    }
}
