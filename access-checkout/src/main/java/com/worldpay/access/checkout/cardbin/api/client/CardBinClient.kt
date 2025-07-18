package com.worldpay.access.checkout.cardbin.api.client

import android.util.Log
import com.worldpay.access.checkout.api.HttpsClient
import com.worldpay.access.checkout.api.URLFactory
import com.worldpay.access.checkout.api.URLFactoryImpl
import com.worldpay.access.checkout.api.serialization.Deserializer
import com.worldpay.access.checkout.api.serialization.Serializer
import com.worldpay.access.checkout.cardbin.api.request.CardBinRequest
import com.worldpay.access.checkout.cardbin.api.response.CardBinResponse
import com.worldpay.access.checkout.cardbin.api.serialization.CardBinRequestSerializer
import com.worldpay.access.checkout.cardbin.api.serialization.CardBinResponseDeserializer
import com.worldpay.access.checkout.client.api.exception.AccessCheckoutException
import com.worldpay.access.checkout.client.api.exception.ClientErrorException
import java.net.URL

/**
 * Client for retrieving card scheme details using a card BIN.
 *
 * This class manages a single in-flight request at a time. If a new request is made while another is in progress,
 * the previous request is cancelled and only the latest request will complete.
 *
 * @property baseUrl The base URL for the API endpoint.
 * @property urlFactory Factory to build the full endpoint URL.
 * @property httpsClient HTTP client to perform the network request.
 * @property deserializer Deserializes the response from the API.
 * @property serializer Serializes the request to the API.
 * @property cacheManager handles cache actions.
 */
internal class CardBinClient(
    baseUrl: URL,
    urlFactory: URLFactory = URLFactoryImpl(),
    private val httpsClient: HttpsClient = HttpsClient(),
    private val deserializer: Deserializer<CardBinResponse> = CardBinResponseDeserializer(),
    private val serializer: Serializer<CardBinRequest> = CardBinRequestSerializer(),
    private val cacheManager: CardBinCacheManager = CardBinCacheManager()
) {
    companion object {
        const val WP_API_VERSION = "WP-Api-Version"
        const val WP_API_VERSION_VALUE = "1"
        const val WP_CALLER_ID = "WP-CallerId"
        const val WP_CALLER_ID_VALUE = "checkoutandroid"
        const val WP_CONTENT_TYPE = "Content-Type"
        const val WP_CONTENT_TYPE_VALUE = "application/json"
    }

    private val CARD_BIN_ENDPOINT = "public/card/bindetails"

    /** Maximum number of retry attempts for card bin requests */
    private val MAX_ATTEMPTS = 3

    private val cardBinUrl = urlFactory.getURL("$baseUrl/$CARD_BIN_ENDPOINT")

    /**
     * Makes a network request to retrieve card scheme details for the given request.
     *
     * This method no longer handles coroutine cancellation. It simply performs the network request.
     *
     * @param request The card BIN request payload.
     * @return The card BIN response from the API.
     * @throws Exception if the request fails.
     */
    private suspend fun getCardBinResponse(request: CardBinRequest): CardBinResponse {
        val headers = hashMapOf(
            WP_API_VERSION to WP_API_VERSION_VALUE,
            WP_CALLER_ID to WP_CALLER_ID_VALUE,
            WP_CONTENT_TYPE to WP_CONTENT_TYPE_VALUE
        )

        return httpsClient.doPost(cardBinUrl, request, headers, serializer, deserializer)
    }

    /**
     * Fetches card BIN details with a retry mechanism.
     *
     * @param request The request to fetch card BIN details.
     * @return The card BIN response from the API.
     */
    suspend fun fetchCardBinResponseWithRetry(request: CardBinRequest): CardBinResponse {

        // Generate the cache key using the first 12 digits of the PAN
        val cacheKey = cacheManager.getCacheKey(request.cardNumber)


        // Return cached response if available
        cacheManager.getCachedResponse(cacheKey)?.let { cachedResponse ->
            return cachedResponse
        }

        val response = withRetry<CardBinResponse>(
            MAX_ATTEMPTS,
            action = { getCardBinResponse(request) },
            onError = { e -> if (e.cause is ClientErrorException) throw e }
        )

        // Only save to cache if the response is not null
        cacheManager.putInCache(cacheKey, response)

        return response
    }

    /**
     * Executes the given action with a retry mechanism.
     *
     * @param maxAttempts The maximum number of retry attempts.
     * @param onError A callback invoked when an exception occurs during an attempt.
     * @param action The action to execute, which may throw an exception.
     * @return The result of the action if it succeeds within the allowed attempts.
     * @throws AccessCheckoutException If all retry attempts are exhausted, wrapping the last exception encountered.
     */
    private suspend fun <T> withRetry(
        maxAttempts: Int,
        onError: (Exception) -> Unit,
        action: suspend () -> T
    ): T {
        var attempt = 0
        var lastException: Exception? = null

        while (attempt < maxAttempts) {
            try {
                return action()
            } catch (e: Exception) {
                if (e is kotlinx.coroutines.CancellationException) throw e // Stop retries on coroutine cancellation
                lastException = e
                onError(e)
                attempt++
                Log.d(javaClass.simpleName, "[${attempt}/$maxAttempts] Could not retrieve response Retrying...", e)
            }
        }
        throw AccessCheckoutException("Failed after $maxAttempts attempts", lastException)
    }
}