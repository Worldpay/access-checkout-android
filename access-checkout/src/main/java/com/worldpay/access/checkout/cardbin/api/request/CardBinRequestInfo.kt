package com.worldpay.access.checkout.cardbin.api.request

import java.io.Serializable
import java.net.URL

/**
 * This is a serializable class that contain all the necessary information to make a request to the card bin api.
 * It should only be created using the [CardBinRequestInfo.Builder]
 *
 * @param[url] [URL] representing the base url for Access Worldpay services
 * @param[requestBody] [CardBinRequest] with the card and merchant information
 */
internal class CardBinRequestInfo private constructor(
    val url: URL,
    val requestBody: CardBinRequest,
) : Serializable {

    /**
     * A builder for constructing a [CardBinRequestInfo]
     */
    internal data class Builder(
        private var url: URL? = null,
        private var requestBody: CardBinRequest? = null,
    ) {

        fun url(url: URL) = apply { this.url = url }

        fun requestBody(requestBody: CardBinRequest) = apply { this.requestBody = requestBody }
        

        fun build() =
            CardBinRequestInfo(
                url!!,
                requestBody!!
            )
    }
}
