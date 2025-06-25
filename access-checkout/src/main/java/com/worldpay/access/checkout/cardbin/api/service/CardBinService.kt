package com.worldpay.access.checkout.cardbin.api.service

import com.worldpay.access.checkout.api.configuration.RemoteCardBrand
import com.worldpay.access.checkout.cardbin.api.client.CardBinClient
import com.worldpay.access.checkout.cardbin.api.request.CardBinRequest
import com.worldpay.access.checkout.client.api.exception.AccessCheckoutException
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.Job
import kotlinx.coroutines.launch
import java.net.URL
import java.util.concurrent.ConcurrentHashMap
import kotlin.text.set

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
        URL(baseUrl)
    ),
    private val scope: CoroutineScope = CoroutineScope(Dispatchers.IO)
) {

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
        callback: ((List<RemoteCardBrand>) -> Unit)
    ) {
        //Safe-guard: ensure pan has no spaces to be able to compute cache keys and request correctly
        val panValue = pan.replace(" ", "")
        // Generate the cache key using the first 12 digits of the PAN
        val cacheKey = getCacheKey(panValue)
        // Return cached response if available
        cache[cacheKey]?.let { cachedResponse ->
            callback.invoke(cachedResponse)
            //Return if the cache is hit, so the coroutine (and thus the API call) is not launched.
            return
        }

        // Launch a coroutine to fetch the card brands from the API asynchronously
        launchCancellableCoroutineRequest {
            val cardBinRequest = CardBinRequest(panValue, checkoutId)
            val brands = transform(cardBinRequest, globalBrand, maxAttempts = 3)
            cache[cacheKey] = brands
            callback.invoke(brands)
        }
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
        currentJob?.let {
            println("Found in-flight request, will abort in-flight request.")
            it.cancel() // Cancel the in-flight request
            currentJob = null // Reset the job reference
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
            } finally {
                currentJob = null
            }
        }

    }

    private suspend fun transform(
        cardBinRequest: CardBinRequest,
        globalBrand: RemoteCardBrand,
        maxAttempts: Int = 3
    ): List<RemoteCardBrand> {
        val response = client.fetchCardBinResponseWithRetry(cardBinRequest, maxAttempts)
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
