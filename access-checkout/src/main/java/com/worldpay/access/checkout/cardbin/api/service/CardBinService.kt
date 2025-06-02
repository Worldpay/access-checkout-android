package com.worldpay.access.checkout.cardbin.api.service

import com.worldpay.access.checkout.api.configuration.RemoteCardBrand
import com.worldpay.access.checkout.cardbin.api.client.CardBinClient
import com.worldpay.access.checkout.cardbin.api.request.CardBinRequest
import com.worldpay.access.checkout.cardbin.api.response.CardBinResponse
import java.net.URL
import java.util.concurrent.ConcurrentHashMap

internal class CardBinService(private val checkoutId: String) {

    private val cache = ConcurrentHashMap<String, CardBinResponse>()
    val URL = URL("1234")
    // create instance of CardBinClient
    private val client = CardBinClient(URL)

    suspend fun getCardBrands(newCardBrand: RemoteCardBrand?, pan: String): List<RemoteCardBrand> {
        if (newCardBrand == null) {
            return emptyList()
        }
        // create list of card brands to return
        val listToReturn = ArrayList<RemoteCardBrand>()
        // add the initial brand that we already have (VISA)
        listToReturn.add(newCardBrand)

        if (cache.contains(pan)) {
            var response = cache[pan]
            transform(newCardBrand, response, listToReturn)
            return listToReturn
        }

        // build request to send to card bin api
        val cardBinRequest = CardBinRequest(pan, checkoutId)

        // request to card bin api
        val response = client.getCardBinResponse(cardBinRequest)

        cache[pan] = response

        transform(newCardBrand, response, listToReturn)

        return listToReturn.toList()
    }

    private fun transform (
        newCardBrand: RemoteCardBrand,
        response: CardBinResponse?,
        listToReturn: ArrayList<RemoteCardBrand>
    ): List<RemoteCardBrand> {
        // check that the response.brand isn't empty
        if (response != null) {
            if (response.brand.isNotEmpty() || response.brand.count() > 1) {
                // if its not empty or brand count is over 1
                response.brand.forEach { brand ->
                    val newRemoteCardBrand =
                        RemoteCardBrand(
                            brand,
                            newCardBrand.images,
                            newCardBrand.cvc,
                            newCardBrand.pan
                        )
                    listToReturn.add(newRemoteCardBrand)
                }
            }
        }
        return listToReturn
}
}