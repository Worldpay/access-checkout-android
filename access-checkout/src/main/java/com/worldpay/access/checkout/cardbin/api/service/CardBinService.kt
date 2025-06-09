package com.worldpay.access.checkout.cardbin.api.service

import android.util.Log
import com.worldpay.access.checkout.api.HttpsClient
import com.worldpay.access.checkout.api.configuration.RemoteCardBrand
import com.worldpay.access.checkout.cardbin.api.client.CardBinClient
import com.worldpay.access.checkout.cardbin.api.request.CardBinRequest
import com.worldpay.access.checkout.cardbin.api.response.CardBinResponse
import com.worldpay.access.checkout.cardbin.api.serialization.CardBinRequestSerializer
import com.worldpay.access.checkout.cardbin.api.serialization.CardBinResponseDeserializer
import com.worldpay.access.checkout.client.api.exception.AccessCheckoutException
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.SupervisorJob
import kotlinx.coroutines.cancel
import kotlinx.coroutines.launch
import java.util.concurrent.ConcurrentHashMap

/**
 * Service for retrieving card brand schemes using the card BIN (Bank Identification Number).
 *
 * @property checkoutId The checkout session identifier used for API requests.
 * @property baseUrl The base URL for the card bin API endpoint.
 * @property client The client responsible for making card bin API requests.
 * @property coroutineScope The coroutine scope used for asynchronous operations.
 */
internal class CardBinService(
    private val checkoutId: String,
    private val baseUrl: String,
    private val client: CardBinClient = CardBinClient(
        baseUrl,
        HttpsClient(),
        CardBinResponseDeserializer(),
        CardBinRequestSerializer()
    ),
    private val coroutineScope: CoroutineScope = CoroutineScope(Dispatchers.IO + SupervisorJob())
) {
    //TODO: Make a mapping function to pass in the baseURL to CardBinService
    companion object {
        // only stores value in cache of required length (12 digits)
        private const val CACHE_KEY_LENGTH = 12
    }

    // creates concurrent hash map to store API response by card number prefix (12 digits)
    private val cache = ConcurrentHashMap<String, CardBinResponse?>()

    // pass callback into the parameters
    fun getCardBrands(
        initialCardBrand: RemoteCardBrand?,
        pan: String,
        onAdditionalBrandsReceived: ((List<RemoteCardBrand>) -> Unit)? = null
    ): List<RemoteCardBrand> {
        if (initialCardBrand == null) {
            return emptyList()
        }

        // take first 12 digits of pan
        val cachedResponse = if (pan.length >= CACHE_KEY_LENGTH) {
            val cacheKey = pan.take(CACHE_KEY_LENGTH)
            cache[cacheKey]
        } else {
            return listOf(initialCardBrand)
        }

        if (cachedResponse != null) {
            // Transform the brands into correct response object
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
                // Builds the request to send to card bin api
                val cardBinRequest = CardBinRequest(pan, checkoutId)
                // Request to card bin api
                val response = client.getCardBinResponse(cardBinRequest)

                // Caches response in concurrent hash map
                cache[pan.take(CACHE_KEY_LENGTH)] = response

                val brands = transform(initialCardBrand, response)

                // Callback to returns card brands when there is a response
                callback?.invoke(brands)


            } catch (e: AccessCheckoutException) {
                // Catch the exception from HttpClient and swallow it
                Log.e("Card Bin API", "Unable to retrieve Card Bin details")
            }
        }
    }

    fun transform(
        initialCardBrand: RemoteCardBrand,
        response: CardBinResponse
    ): List<RemoteCardBrand> {
        // check that the response.brand isn't empty
        if (response.brand.isEmpty()) {
            return listOf(initialCardBrand)
        }

        // if response returns the same single brand, no transformation needed & checks if it matches initialCardBrand
        if (response.brand.size == 1 &&
            response.brand.first().equals(initialCardBrand.name, ignoreCase = true)
        ) {
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
