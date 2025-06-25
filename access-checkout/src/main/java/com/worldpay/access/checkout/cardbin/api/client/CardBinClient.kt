package com.worldpay.access.checkout.cardbin.api.client

import com.worldpay.access.checkout.api.HttpsClient
import com.worldpay.access.checkout.api.URLFactory
import com.worldpay.access.checkout.api.URLFactoryImpl
import com.worldpay.access.checkout.api.configuration.RemoteCardBrand
import com.worldpay.access.checkout.api.serialization.Deserializer
import com.worldpay.access.checkout.api.serialization.Serializer
import com.worldpay.access.checkout.cardbin.api.request.CardBinRequest
import com.worldpay.access.checkout.cardbin.api.response.CardBinResponse
import com.worldpay.access.checkout.cardbin.api.serialization.CardBinRequestSerializer
import com.worldpay.access.checkout.cardbin.api.serialization.CardBinResponseDeserializer
import com.worldpay.access.checkout.client.api.exception.AccessCheckoutException
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.Job
import kotlinx.coroutines.launch
import kotlinx.coroutines.suspendCancellableCoroutine
import java.net.URL
import kotlin.coroutines.resume
import kotlin.coroutines.resumeWithException
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
 */
internal class CardBinClient(
    baseUrl: URL,
    urlFactory: URLFactory = URLFactoryImpl(),
    private val httpsClient: HttpsClient = HttpsClient(),
    private val deserializer: Deserializer<CardBinResponse> = CardBinResponseDeserializer(),
    private val serializer: Serializer<CardBinRequest> = CardBinRequestSerializer(),
    private var currentJob: Job? = null
) {

    internal companion object {
        private const val CARD_BIN_ENDPOINT = "public/card/bindetails"

        // Header constants
        internal const val WP_API_VERSION = "WP-Api-Version"
        internal const val WP_API_VERSION_VALUE = "1"
        internal const val WP_CALLER_ID = "WP-CallerId"
        internal const val WP_CALLER_ID_VALUE = "checkoutandroid"
        internal const val WP_CONTENT_TYPE = "Content-Type"
        internal const val WP_CONTENT_TYPE_VALUE = "application/json"
    }

    // Coroutine scope for launching requests
    private val scope = CoroutineScope(Dispatchers.IO)

    // Holds the current in-flight request Job for cancellation
//    private var currentJob: Job? = null

    private val cardBinUrl = urlFactory.getURL("$baseUrl/$CARD_BIN_ENDPOINT")

    /**
     * Makes a network request to retrieve card scheme details for the given request.
     *
     * If a previous request is still in progress, it will be cancelled before starting the new one.
     * Only the latest request should complete and return a result.
     *
     * @param request The card BIN request payload.
     * @return The card BIN response from the API.
     * @throws Exception if the request fails or is cancelled.
     */
    suspend fun getCardBinResponse(request: CardBinRequest): CardBinResponse {
        // Safe-guard: Cancel any previous in-flight request before starting a new one
        currentJob?.cancel()

        return suspendCancellableCoroutine { continuation ->
            // Launch a new coroutine for the call to card-bin service and keep track of its Job
            currentJob = scope.launch {
                try {

                    val headers = hashMapOf(
                        WP_API_VERSION to WP_API_VERSION_VALUE,
                        WP_CALLER_ID to WP_CALLER_ID_VALUE,
                        WP_CONTENT_TYPE to WP_CONTENT_TYPE_VALUE
                    )

                    val response =
                        httpsClient.doPost(cardBinUrl, request, headers, serializer, deserializer)

                    // Resume the coroutine with the response
                    continuation.resume(response)
                } catch (_: Exception) {
                    //Otherwise raise new exception
                    continuation.resumeWithException(
                        //TODO: Do we need any details ?
                        AccessCheckoutException("Could not perform request to card-bin API.")
                    )
                }
            }
        }
    }

    suspend fun retryMechanism (
        maxAttempts: Int = 3, // parameter to control the number of retry attempts
        block: suspend () -> CardBinResponse // suspending lambda that performs the request
    ): CardBinResponse {
            var attempt = 0 // initialise the attempt counter at 0
            var lastException: Exception? = null // stores the last exception thrown, initialised with null

        while (attempt < maxAttempts) {
            try {
                return block() // attempt to execute the block of code
            } catch (e: Exception) {
                lastException = e
                attempt++
                if (attempt == maxAttempts) {
                    throw AccessCheckoutException(
                        "Failed after $maxAttempts attempted",
                        lastException
                    ) // rethrow the exception if the last attempt fails
                }
            }
        }
        throw AccessCheckoutException("Unexpected error occurred while fetching card schemes", lastException)
        }

    suspend fun fetchCardBinResponseWithRetry(
        client: RemoteCardBrand,
        request: CardBinResponse,
        maxAttempts: Int = 3
    ): CardBinResponse {
        return client.retryMechanism(maxAttempts) {
            client.getCardBinResponse(request) // Call the CardBinService
        }
    }
}
