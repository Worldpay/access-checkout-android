package com.worldpay.access.checkout.cardbin.api.service

import android.util.Log
import com.worldpay.access.checkout.api.configuration.DefaultCardRules.CVC_DEFAULTS
import com.worldpay.access.checkout.api.configuration.DefaultCardRules.PAN_DEFAULTS
import com.worldpay.access.checkout.api.configuration.RemoteCardBrand
import com.worldpay.access.checkout.cardbin.api.client.CardBinClient
import com.worldpay.access.checkout.cardbin.api.request.CardBinRequest
import com.worldpay.access.checkout.cardbin.api.response.CardBinResponse
import com.worldpay.access.checkout.util.coroutine.DispatchersProvider
import com.worldpay.access.checkout.util.coroutine.IDispatchersProvider
import com.worldpay.access.checkout.validation.configuration.CardConfigurationProvider
import kotlinx.coroutines.CancellationException
import kotlinx.coroutines.CoroutineExceptionHandler
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Job
import kotlinx.coroutines.SupervisorJob
import kotlinx.coroutines.launch
import kotlinx.coroutines.withContext
import java.net.URL

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
    private val client: CardBinClient = CardBinClient(URL(baseUrl)),
    private val dispatcherProvider: IDispatchersProvider = DispatchersProvider.instance
) {
    private val scope = CoroutineScope(SupervisorJob() + dispatcherProvider.main)
    internal var currentJob: Job? = null

    /**
     * Retrieves card brands based on the provided card PAN.
     *
     * This method cancels any in-flight request before starting a new one.
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
        // Safe-guard: ensure pan has no spaces to be able to compute cache keys and request correctly
        val panValue = pan.replace(" ", "")

        // Cancel any previous in-flight request before starting a new one
        currentJob?.let {
            if (it.isActive) {
                it.cancel()
            }
        }

        // Launch a new coroutine to fetch the card brands from the API asynchronously
        currentJob = scope.launch() {
            try {
                val response =
                    withContext(dispatcherProvider.io) {
                        client.fetchCardBinResponseWithRetry(
                            request = CardBinRequest(
                                panValue.take(12),
                                checkoutId
                            )
                        )
                    }
                // Transform the API response into a list of card brands
                val brands = transform(globalBrand, response)

                // Invoke the callback with the transformed card brands
                callback.invoke(brands)

            } catch (_: CancellationException) {
                Log.d(
                    javaClass.simpleName,
                    "Coroutine was cancelled cleanly"
                )
            } catch (e: Exception) {
                Log.d(
                    this::class.java.simpleName,
                    "Could not retrieve card bin information using API client: ${e.message}"
                )
            }
        }
    }

    private fun transform(
        globalBrand: RemoteCardBrand?,
        response: CardBinResponse
    ): List<RemoteCardBrand> {
        // Safe-guard: If globalBrand is null and response is empty, return an empty list
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
