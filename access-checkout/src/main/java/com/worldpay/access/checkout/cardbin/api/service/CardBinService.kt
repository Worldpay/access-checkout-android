package com.worldpay.access.checkout.cardbin.api.service

import android.util.Log
import com.worldpay.access.checkout.api.configuration.DefaultCardRules.CVC_DEFAULTS
import com.worldpay.access.checkout.api.configuration.DefaultCardRules.PAN_DEFAULTS
import com.worldpay.access.checkout.api.configuration.RemoteCardBrand
import com.worldpay.access.checkout.cardbin.api.client.CardBinClient
import com.worldpay.access.checkout.cardbin.api.request.CardBinRequest
import com.worldpay.access.checkout.cardbin.api.response.CardBinResponse
import com.worldpay.access.checkout.validation.configuration.CardConfigurationProvider
import kotlinx.coroutines.CoroutineExceptionHandler
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.SupervisorJob
import kotlinx.coroutines.launch
import java.net.URL
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
        URL(baseUrl)
    ),
    private val scope: CoroutineScope = CoroutineScope(Dispatchers.IO + SupervisorJob())
) {

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
        globalBrand: RemoteCardBrand?,
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

        val coroutineExceptionHandler =
            CoroutineExceptionHandler { _, throwable ->
                Log.d(
                    this::class.java.simpleName,
                    "Could not retrieve card bin information using API client: ${throwable.message}"
                )
            }

        // Launch a coroutine to fetch the card brands from the API asynchronously
        launchCancellableCoroutineRequest(
            {
                val response =
                    client.fetchCardBinResponseWithRetry(request = CardBinRequest(panValue, checkoutId))
                // Transform the API response into a list of card brands
                val brands = transform(globalBrand, response)
                cache[cacheKey] = brands

                // Invoke the callback with the transformed card brands
                callback.invoke(brands)
            },
            coroutineExceptionHandler
        )
    }

    /**
     * Launches a cancellable coroutine to execute the provided suspendable request.
     *
     * This method ensures that any previous in-flight request is canceled before starting a new one.
     * It handles exceptions raised during the execution of the request and wraps them in an `AccessCheckoutException`.
     *
     * @param request A suspendable lambda representing the request to be executed.
     */
    private fun launchCancellableCoroutineRequest(
        request: suspend () -> Unit,
        coroutineExceptionHandler: CoroutineExceptionHandler
    ) {
        // Launch a new coroutine to execute the request
        scope.launch(coroutineExceptionHandler) {
            // Execute the provided request
            request()
        }
    }

    private fun transform(
        globalBrand: RemoteCardBrand?,
        response: CardBinResponse
    ): List<RemoteCardBrand> {
        // SAfe-guard: If globalBrand is null and response is empty, return an empty list
        if (globalBrand == null && response.brand.isEmpty()) {
            return emptyList()
        }

        // Initialize default validation rules for CVC and PAN
        var cvcValidationRule = CVC_DEFAULTS
        var panValidationRule = PAN_DEFAULTS

        // If a globalBrand is provided, override the default validation rules with those from the globalBrand
        if (globalBrand !== null) {
            cvcValidationRule = globalBrand.cvc
            panValidationRule = globalBrand.pan
        }

        // Transform the response brands into a list of RemoteCardBrand objects
        // For each brand name in the response, attempt to find an existing brand configuration by name
        // If no matching brand is found, create a new RemoteCardBrand using name but the current validation rules:
        // if globalBRands was available it will use those otherwise it would use the default rules
        val responseBrands = response.brand.map { brandName ->
            findBrandByName(brandName, globalBrand) ?: RemoteCardBrand(
                name = brandName, // Use the brand name from the response
                images = emptyList(), // No images are associated with the new/unknown brand
                cvc = cvcValidationRule, // Use the determined CVC validation rule
                pan = panValidationRule  // Use the determined PAN validation rule
            )
        }
        // Combine globalBrand (if not null) with response brands and deduplicate by name
        return (listOfNotNull(globalBrand) + responseBrands)
            .distinctBy { it.name.lowercase() }
    }

    fun findBrandByName(brandName: String, default: RemoteCardBrand?): RemoteCardBrand? {
        if (brandName === default?.name) {
            return default
        }
        var cardConfiguration = CardConfigurationProvider.getCardConfiguration()
        return cardConfiguration.brands.firstOrNull { it.name.equals(brandName, ignoreCase = true) }
    }

}
