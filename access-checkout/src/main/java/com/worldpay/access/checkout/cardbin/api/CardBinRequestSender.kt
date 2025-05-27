package com.worldpay.access.checkout.cardbin.api

import android.util.Log
import com.worldpay.access.checkout.cardbin.api.client.CardBinClient
import com.worldpay.access.checkout.cardbin.api.request.CardBinRequestInfo
import com.worldpay.access.checkout.cardbin.api.response.CardBinResponseInfo
import com.worldpay.access.checkout.client.api.exception.AccessCheckoutException

internal class CardBinRequestSender(
    private val cardBinClient: CardBinClient,
) {

    suspend fun sendCardBinRequest(
        cardBinRequestInfo: CardBinRequestInfo,
    ): CardBinResponseInfo {
        Log.d("CardBinRequestSender", "Making Card Bin api request")

        try {
            val responseBody = cardBinClient.getCardBinResponse(
                cardBinRequestInfo.url,
                cardBinRequestInfo.requestBody
            )
            return CardBinResponseInfo.Builder()
                .responseBody(responseBody)
                .build()
        } catch (ex: AccessCheckoutException) {
            Log.e("RequestDispatcher", "Received exception: $ex")
            throw ex
        }
    }
}
