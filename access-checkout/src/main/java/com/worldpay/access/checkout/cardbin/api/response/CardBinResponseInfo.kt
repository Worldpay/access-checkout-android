package com.worldpay.access.checkout.cardbin.api.response

import java.io.Serializable

/**
 * This class holds the response body about the [CardBinResponse]]
 *
 * @property[responseBody] [CardBinResponse] representation of a card bin response data
 */
internal class CardBinResponseInfo private constructor(
    val responseBody: CardBinResponse,
) : Serializable {

    internal data class Builder(
        private var responseBody: CardBinResponse? = null,
    ) {

        fun responseBody(responseBody: CardBinResponse?) = apply { this.responseBody = responseBody }

        fun build() =
            CardBinResponseInfo(
                responseBody!!
            )
    }
}
