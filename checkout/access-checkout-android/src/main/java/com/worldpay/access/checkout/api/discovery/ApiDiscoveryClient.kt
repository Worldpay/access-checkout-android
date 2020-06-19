package com.worldpay.access.checkout.api.discovery

import com.worldpay.access.checkout.api.AsyncTaskResult
import com.worldpay.access.checkout.api.Callback
import com.worldpay.access.checkout.client.api.exception.AccessCheckoutException
import com.worldpay.access.checkout.util.logging.LoggingUtils.debugLog
import java.util.concurrent.atomic.AtomicInteger

/**
 * This class is responsible for managing the discovery of the API endpoint for a service and
 * calls the callback with the discovered API endpoint and utilises caching using the [DiscoveryCache]
 *
 * @property apiDiscoveryAsyncTaskFactory [ApiDiscoveryAsyncTaskFactory] to retrieve an [ApiDiscoveryAsyncTask]
 * @property discoveryCache A [DiscoveryCache] for storing the discovered endpoints
 */
internal class ApiDiscoveryClient(
    private val apiDiscoveryAsyncTaskFactory: ApiDiscoveryAsyncTaskFactory,
    private val discoveryCache: DiscoveryCache = DiscoveryCache
) {

    private val currentAttempts = AtomicInteger(0)
    private val maxAttempts = 2

    /**
     * Asynchronously discovers the required API endpoint for the desired service.
     *
     * @param[baseUrl] [String] the base url for the API
     * @param[callback] [Callback] of [String] generic type that will be called with the response or error
     * @param[discoverLinks] A [DiscoverLinks] object which contains the information on the service to discover
     *
     * @return [String] Url of the discovered endpoint
     * @throws [AccessCheckoutException] This can be thrown in the event where no base url is supplied
     */
    fun discover(baseUrl: String, callback: Callback<String>, discoverLinks: DiscoverLinks) {
        if (baseUrl.isBlank()) {
            throw AccessCheckoutException("No URL supplied")
        }

        val asyncTaskResult = discoveryCache.getResult(discoverLinks)

        if (asyncTaskResult == null) {
            debugLog(javaClass.simpleName, "Discovering endpoint")

            currentAttempts.addAndGet(1)

            val asyncTaskResultCallback = getAsyncTaskResultCallback(callback, discoverLinks, baseUrl)
            val accessCheckoutDiscoveryAsyncTask = apiDiscoveryAsyncTaskFactory.getAsyncTask(asyncTaskResultCallback, discoverLinks)

            accessCheckoutDiscoveryAsyncTask.execute(baseUrl)
        } else {
            debugLog(javaClass.simpleName, "Task result was already present. Num of currentAttempts: ${currentAttempts.get()}")
            handleResult(
                asyncTaskResult = asyncTaskResult,
                callback = callback,
                discoverLinks = discoverLinks,
                baseUrl = baseUrl,
                doSaveResult = false
            )
        }
    }

    private fun getAsyncTaskResultCallback(
        callback: Callback<String>,
        discoverLinks: DiscoverLinks,
        baseUrl: String
    ): Callback<String> {
        return object : Callback<String> {
            override fun onResponse(error: Exception?, response: String?) {
                val asyncTaskResult = if (error != null) {
                    AsyncTaskResult(error)
                } else {
                    AsyncTaskResult(response as String)
                }

                handleResult(
                    asyncTaskResult = asyncTaskResult,
                    callback = callback,
                    discoverLinks = discoverLinks,
                    baseUrl = baseUrl,
                    doSaveResult = true
                )
            }
        }
    }

    private fun handleResult(
        asyncTaskResult: AsyncTaskResult<String>,
        callback: Callback<String>,
        discoverLinks: DiscoverLinks,
        baseUrl: String,
        doSaveResult: Boolean
    ) {
        asyncTaskResult.result?.let {
            if (doSaveResult) {
                discoveryCache.saveResult(discoverLinks, AsyncTaskResult(it))
            }
            callback.onResponse(null, it)
        }

        asyncTaskResult.error?.let {
            if (doSaveResult) {
                discoveryCache.saveResult(discoverLinks, AsyncTaskResult(it))
            }
            if (currentAttempts.get() < maxAttempts) {
                discoveryCache.clearResult(discoverLinks)
                discover(baseUrl, callback, discoverLinks)
            } else {
                callback.onResponse(it, null)
            }
        }
    }
}
