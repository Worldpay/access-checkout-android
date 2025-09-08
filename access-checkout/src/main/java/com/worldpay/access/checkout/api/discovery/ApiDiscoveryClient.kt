package com.worldpay.access.checkout.api.discovery

import android.util.Log
import com.worldpay.access.checkout.api.HttpsClient
import com.worldpay.access.checkout.api.URLFactory
import com.worldpay.access.checkout.api.URLFactoryImpl
import com.worldpay.access.checkout.api.serialization.PlainResponseDeserializer
import com.worldpay.access.checkout.client.api.exception.AccessCheckoutException
import java.net.MalformedURLException
import java.net.URL
import java.util.concurrent.atomic.AtomicInteger

/**
 * This class is responsible for managing the discovery of the API endpoint for a service.
 * Its main method returns the discovered API endpoint and utilises caching using the [DiscoveryCache].
 * It must be initialised with the base URL using the initialise() method before
 * using the discoverEndpoint method.
 *
 * This class is NOT thread-safe, the discoverEndpoint() method cannot be called concurrently.
 *
 * @property baseUrl A [String] in the form of a valid URL representing the base URL of Access services
 * @property [httpsClient] An [HttpsClient] that is responsible for sending HTTP requests to the Access Worldpay services
 */
internal class ApiDiscoveryClient(
    private val baseUrl: URL,
    private val httpsClient: HttpsClient
) {
    companion object {
        private const val MAX_ATTEMPTS = 2

        private val urlFactory: URLFactory = URLFactoryImpl()
        private val discoveryCache: DiscoveryCache = DiscoveryCache
        private val currentAttempts = AtomicInteger(0)

        private var instance: ApiDiscoveryClient? = null

        internal fun initialise(baseUrlAsString: String, httpsClient: HttpsClient = HttpsClient()) {
            try {
                instance = ApiDiscoveryClient(URL(baseUrlAsString), httpsClient)
            } catch (e: MalformedURLException) {
                throw MalformedURLException("The base URL passed to the SDK is not a valid URL ($baseUrlAsString)")
            }
        }

        internal val isInitialised get() = instance != null

        internal fun reset() {
            instance = null
        }

        internal suspend fun discoverEndpoint(discoverLinks: DiscoverLinks): URL {
            if (!isInitialised) {
                throw IllegalStateException("ApiDiscoveryClient must be initialised before using it")
            }

            return instance!!.discoverEndpoint(discoverLinks)
        }
    }

    /**
     * Asynchronously discovers the required API endpoint for the desired service.
     *
     * @param[baseUrl] [URL] the base url for the API
     * @param[discoverLinks] A [DiscoverLinks] object which contains the information on the service to discover
     *
     * @return [String] Url of the discovered endpoint
     * @throws [AccessCheckoutException] This can be thrown in the event where no base url is supplied
     */
    private suspend fun discoverEndpoint(discoverLinks: DiscoverLinks): URL {
        val endpoint = discoveryCache.getResult(discoverLinks)

        return if (endpoint != null) {
            Log.d(javaClass.simpleName, "Endpoint was cached: $endpoint")
            endpoint
        } else {
            doEndpointDiscovery(discoverLinks)
        }
    }

    private suspend fun doEndpointDiscovery(
        discoverLinks: DiscoverLinks
    ): URL {
        return try {
            currentAttempts.addAndGet(1)

            val endpoint = discover(discoverLinks.endpoints)
            discoveryCache.saveResult(discoverLinks, endpoint)
            currentAttempts.set(0)

            endpoint
        } catch (ex: Exception) {
            if (currentAttempts.get() < MAX_ATTEMPTS) {
                discoverEndpoint(discoverLinks)
            } else {
                throw AccessCheckoutException("Could not discover endpoint", ex)
            }
        }
    }

    private suspend fun discover(endpoints: List<Endpoint>): URL {
        var resourceUrl = this.baseUrl

        for (endpoint in endpoints) {
            Log.d(javaClass.simpleName, "Discovering endpoint for ${endpoint.key}")

            var response = discoveryCache.getResponse(resourceUrl)
            response?.let {
                Log.d(javaClass.simpleName, "Retrieved response from cache for $resourceUrl")
            }

            if (response == null) {
                response =
                    this.httpsClient.doGet(resourceUrl, PlainResponseDeserializer, endpoint.headers)
                discoveryCache.saveResponse(resourceUrl, response)
            }

            resourceUrl = urlFactory.getURL(endpoint.getDeserializer().deserialize(response))
            Log.d(javaClass.simpleName, "Success. Found: $resourceUrl")
        }

        return resourceUrl
    }
}
