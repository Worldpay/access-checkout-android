package com.worldpay.access.checkout.cardbin.api.service

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
import kotlinx.coroutines.Job
import kotlinx.coroutines.launch
import java.util.concurrent.ConcurrentHashMap

/**
 * Service for retrieving card brand schemes using the card BIN (Bank Identification Number).
 *
 * @property[checkoutId] The checkout session identifier used for API requests.
 * @property[baseUrl] The base URL for the card bin API endpoint.
 * @property[client] The client responsible for making card bin API requests.
 */

internal class CardBinService(
    private val checkoutId: String,
    private val baseUrl: String,
    private val client: CardBinClient = CardBinClient(
        baseUrl,
        HttpsClient(),
        CardBinResponseDeserializer(),
        CardBinRequestSerializer()
    )
) {

    // Coroutine scope for launching requests
    private val scope = CoroutineScope(Dispatchers.IO)

    // Holds the current in-flight request Job for cancellation
    internal var currentJob: Job? = null

    companion object {
        // only stores value in cache of required length (12 digits)
        private const val CACHE_KEY_LENGTH = 12

        // Creates concurrent hash map to store API response by card number prefix (12 digits)
        private val cache = ConcurrentHashMap<String, List<RemoteCardBrand>>()

        // Generates a cache key by extracting the first 12 digits of the provided PAN.
        fun getCacheKey(pan: String): String = pan.take(CACHE_KEY_LENGTH)

        // Function to manually clear the cache
        fun clearCache() {
            cache.clear()
        }
    }


    /**
     * Retrieves card brands based on the provided card PAN.
     *
     * This method first checks the cache for a response associated with the PAN prefix. If a cached response exists,
     * it transforms the response into a list of card brands and returns it.
     * If no cached response is found, it launches a coroutine to fetch the card brands from the API asynchronously.
     * Once a response is received it calls the callback with the additional card brands response.
     *
     * @param globalBrand The global card brand to be returned immediately if no cached response is found.
     * @param pan The card PAN (Primary Account Number) used to identify the card brand.
     * @param callback A callback function to receive additional card brands fetched from the API.
     */
    fun getCardBrands(
        globalBrand: RemoteCardBrand,
        pan: String,
        callback: ((List<RemoteCardBrand>) -> Unit)? = null
    ) {
        //Safe-guard: ensure pan has no spaces to be able to compute cache keys and request correctly
        val panValue = pan.replace(" ", "")

        // Generate the cache key using the first 12 digits of the PAN
        val cacheKey = getCacheKey(panValue)
        // Return cached response if available
        cache[cacheKey]?.let { cachedResponse ->
            callback?.invoke(cachedResponse)
            //Return if the cache is hit, so the coroutine (and thus the API call) is not launched.
            return
        }
        // Launch a coroutine to fetch the card brands from the API asynchronously
        launchCancellableCoroutineRequest(
            request = {
                val response = client.getCardBinResponse(request = CardBinRequest(panValue, checkoutId))
                // Transform the API response into a list of card brands
                val brands = transform(globalBrand, response)
                cache[cacheKey] = brands

                // Invoke the callback with the transformed card brands
                callback?.invoke(brands)
            }
        )
    }

    /**
     * Launches an cancellable coroutine to execute the provided suspendable request.
     *
     * This method ensures that any previous in-flight request is canceled before starting a new one.
     * It handles exceptions raised during the execution of the request and wraps them in an `AccessCheckoutException`.
     *
     * @param request A suspendable lambda representing the request to be executed.
     */
    private fun launchCancellableCoroutineRequest(
        request: suspend () -> Unit
    ) {
        if (currentJob != null) {
            println("Found in-flight request, will abort in-flight request.")
            // Cancel any previous in-flight request
            currentJob?.cancel()
        }
        // Launch a new coroutine to execute the request
        currentJob = scope.launch {
            try {
                // Execute the provided request
                request()
            } catch (exception: Exception) {
                // Wrap and rethrow the exception with additional context
                throw AccessCheckoutException(
                    "Could not perform request to card-bin API.",
                    exception
                )
            }
        }
    }

    private fun transform(
        globalBrand: RemoteCardBrand,
        response: CardBinResponse
    ): List<RemoteCardBrand> {
        // check that the response.brand isn't empty
        if (response.brand.isEmpty()) {
            return listOf(globalBrand)
        }

        // if response returns the same single brand, no transformation needed & checks if it matches globalBrand
        if (response.brand.size == 1 &&
            response.brand.first().equals(globalBrand.name, ignoreCase = true)
        ) {
            return listOf(globalBrand)
        }

        // map each brand name to a RemoteCardBrand object when there are multiple brands in response
        // or response brand is different from globalBrand
        // distinctBy ensure unique objects in the list
        return response.brand
            .map { brandName ->
                RemoteCardBrand(
                    name = brandName,
                    images = globalBrand.images,
                    cvc = globalBrand.cvc,
                    pan = globalBrand.pan
                )
            }
            .distinctBy { it.name.lowercase() }
    }
}

