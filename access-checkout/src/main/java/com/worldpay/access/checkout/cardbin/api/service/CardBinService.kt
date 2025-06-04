package com.worldpay.access.checkout.cardbin.api.service

import android.util.Log
import com.worldpay.access.checkout.api.configuration.RemoteCardBrand
import com.worldpay.access.checkout.cardbin.api.client.CardBinClient
import com.worldpay.access.checkout.cardbin.api.request.CardBinRequest
import com.worldpay.access.checkout.cardbin.api.response.CardBinResponse
import com.worldpay.access.checkout.client.api.exception.AccessCheckoutException
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.SupervisorJob
import kotlinx.coroutines.cancel
import kotlinx.coroutines.launch
import java.net.URL
import java.util.concurrent.ConcurrentHashMap

internal class CardBinService(
    private val checkoutId: String,
    private val baseUrl: URL,
    private val client: CardBinClient = CardBinClient(baseUrl),
    private val coroutineScope: CoroutineScope = CoroutineScope(Dispatchers.IO + SupervisorJob())
)
{
    companion object {
        // only stores value in cache of required length (12 digits)
        // stops duplication of same card number in cache of 12+ digits
        private const val CACHE_KEY_LENGTH = 12
    }
    //TODO: Make a mapping function to pass in the baseURL to CardBinService

    // creates concurrent hash map to store API response by card number prefix (12 digits)
    private val cache = ConcurrentHashMap<String, CardBinResponse?>()

    // pass callback into the parameters
    fun getCardBrands(
        initialCardBrand: RemoteCardBrand?,
        pan: String,
        onAdditionalBrandsReceived: ((List<RemoteCardBrand>) -> Unit)? = null
    ): List<RemoteCardBrand> {
        if (initialCardBrand == null || pan.length < 12) {
            return emptyList()
        }

        // take first 12 digits of pan
        val cacheKey = pan.take(CACHE_KEY_LENGTH)
        // check if cache has matched value for these 12 digits
        val cachedResponse = cache[cacheKey]

        if (cachedResponse != null) {
            // transform the brands into correct response object
            val brands = transform(initialCardBrand, cachedResponse)
            return brands
        }

        // callback invoked when additional brands are fetched from API
        launchCoroutineRequest(initialCardBrand, pan, onAdditionalBrandsReceived)

        // returns initialCardBrand immediately
        // coroutine will return when the response has been received (launch & forget)
        return listOf(initialCardBrand)
    }

    private fun launchCoroutineRequest(
        initialCardBrand: RemoteCardBrand,
        pan: String,
        callback: ((List<RemoteCardBrand>) -> Unit)?
    ) {
        coroutineScope.launch {
            try {
                // builds the request to send to card bin api
                val cardBinRequest = CardBinRequest(pan, checkoutId)
                // request to card bin api
                val response = client.getCardBinResponse(cardBinRequest)

                // caches response in concurrent hash map
                cache[pan.take(CACHE_KEY_LENGTH)] = response

                val brands = transform(initialCardBrand, response)

                // callback to returns card brands when there is a response
                callback?.invoke(brands)

            } catch (e: AccessCheckoutException) {
                 //catch the exception from HttpClient and swallow it
                Log.e("Card Bin API", "Unable to retrieve Card Bin details")
            }
        }
    }

    private fun transform(
        initialCardBrand: RemoteCardBrand,
        response: CardBinResponse
    ): List<RemoteCardBrand> {
        // check that the response.brand isn't empty
        if (response.brand.isEmpty()) {
            return listOf(initialCardBrand)
        }

        // if response returns the same single brand, no transformation needed & checks if it matches initialCardBrand
        if (response.brand.size == 1 &&
            response.brand.first().equals(initialCardBrand.name, ignoreCase = true)) {
            return listOf(initialCardBrand)
        }

        // map each brand name to a RemoteCardBrand object when there are multiple brands in response
        // or response brand is different from initialCardBrand
        // distinctBy ensure unique objects in the list
        return response.brand
            .map { brandName ->
                RemoteCardBrand(
                    name = brandName,
                    images = initialCardBrand.images,
                    cvc = initialCardBrand.cvc,

                    pan = initialCardBrand.pan
                )
            }
            .distinctBy { it.name.lowercase() }
    }

    fun destroy() {
        coroutineScope.cancel()
    }
}

