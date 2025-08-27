package com.worldpay.access.checkout.api.discovery

import android.util.Log
import com.worldpay.access.checkout.api.HttpsClient
import com.worldpay.access.checkout.api.URLFactory
import com.worldpay.access.checkout.api.URLFactoryImpl
import com.worldpay.access.checkout.api.serialization.PlainResponseDeserializer
import com.worldpay.access.checkout.client.api.exception.AccessCheckoutException
import java.net.URL
import java.util.concurrent.atomic.AtomicInteger

/**
 * This class is responsible for managing the discovery of the API endpoint for a service and
 * calls the callback with the discovered API endpoint and utilises caching using the [DiscoveryCache]
 *
 * @property [httpsClient] An [HttpsClient] that is responsible for sending HTTP requests to the Access Worldpay services
 * @property discoveryCache A [DiscoveryCache] for storing the discovered endpoints
 */
internal class ApiDiscoveryClient(
    private val httpsClient: HttpsClient = HttpsClient(),
    private val urlFactory: URLFactory = URLFactoryImpl(),
    private val discoveryCache: DiscoveryCache = DiscoveryCache
) {

    private val currentAttempts = AtomicInteger(0)
    private val maxAttempts = 2

    /**
     * Asynchronously discovers the required API endpoint for the desired service.
     *
     * @param[baseUrl] [URL] the base url for the API
     * @param[discoverLinks] A [DiscoverLinks] object which contains the information on the service to discover
     *
     * @return [String] Url of the discovered endpoint
     * @throws [AccessCheckoutException] This can be thrown in the event where no base url is supplied
     */
    suspend fun discoverEndpoint(baseUrl: URL, discoverLinks: DiscoverLinks): URL {
        val endpoint = discoveryCache.getResult(discoverLinks)

        return if (endpoint != null) {
            Log.d(javaClass.simpleName, "Endpoint was cached: $endpoint")
            endpoint
        } else {
            doEndpointDiscovery(baseUrl, discoverLinks)
        }
    }

    private suspend fun doEndpointDiscovery(
        baseUrl: URL,
        discoverLinks: DiscoverLinks
    ): URL {
        return try {
            currentAttempts.addAndGet(1)

            val endpoint = discover(baseUrl, discoverLinks.endpoints)
            discoveryCache.saveResult(discoverLinks, endpoint)
            currentAttempts.set(0)

            endpoint
        } catch (ex: Exception) {
            if (currentAttempts.get() < maxAttempts) {
                discoverEndpoint(baseUrl, discoverLinks)
            } else {
                throw AccessCheckoutException("Could not discover session endpoint", ex)
            }
        }
    }

    private suspend fun discover(baseUrl: URL, endpoints: List<Endpoint>): URL {
        Log.d(javaClass.simpleName, "Sending request to service discovery endpoint")

        var resourceUrl = baseUrl
        for (endpoint in endpoints) {
            var response = discoveryCache.getResponse(resourceUrl)
            response?.let {
                Log.d(javaClass.simpleName, "Retrieved response from cache for $resourceUrl")
            }

            if (response == null) {
                response =
                    httpsClient.doGet(resourceUrl, PlainResponseDeserializer, endpoint.headers)
                discoveryCache.saveResponse(resourceUrl, response)
            }

            resourceUrl = urlFactory.getURL(endpoint.getDeserializer().deserialize(response))
        }

        Log.d(javaClass.simpleName, "Received response from service discovery endpoint")
        return resourceUrl
    }
}
