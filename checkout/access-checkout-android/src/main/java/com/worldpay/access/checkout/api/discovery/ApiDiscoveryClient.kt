package com.worldpay.access.checkout.api.discovery

import com.worldpay.access.checkout.api.AccessCheckoutException.AccessCheckoutDiscoveryException
import com.worldpay.access.checkout.api.AsyncTaskResult
import com.worldpay.access.checkout.api.Callback
import com.worldpay.access.checkout.util.logging.LoggingUtils.debugLog
import java.util.concurrent.atomic.AtomicInteger

/**
 * [ApiDiscoveryClient] is responsible for managing the discovery of the API endpoint for a service
 * calls the callback with the discovered API endpoint and utilises caching.
 *
 * @param apiDiscoveryAsyncTaskFactory - a factory for [ApiDiscoveryAsyncTask]
 * @param discoveryCache - a cache for storing the discovered endpoints
 */
internal class ApiDiscoveryClient(
    private val apiDiscoveryAsyncTaskFactory: ApiDiscoveryAsyncTaskFactory,
    private val discoveryCache: DiscoveryCache = DiscoveryCache
) {

    private val currentAttempts = AtomicInteger(0)
    private val maxAttempts = 2

    companion object {
        private const val TAG = "AccessCheckoutDiscoveryClient"
    }

    /**
     * Asynchronously discovers the required API endpoint for the desired service.
     * Responds via a callback with either a [URL] or an [AccessCheckoutDiscoveryException]
     *
     * @param baseUrl - the base url for the API
     * @param callback - the callback via which the response is returned
     * @param discoverLinks - a [DiscoverLinks] object which contains the information on the service to discover
     */
    fun discover(baseUrl: String, callback: Callback<String>, discoverLinks: DiscoverLinks) {
        if (baseUrl.isBlank()) {
            throw AccessCheckoutDiscoveryException("No URL supplied")
        }

        val asyncTaskResult = discoveryCache.getResult(discoverLinks)

        if (asyncTaskResult == null) {
            debugLog(TAG, "Discovering endpoint")

            currentAttempts.addAndGet(1)
            val asyncTaskResultCallback = getAsyncTaskResultCallback(callback, discoverLinks, baseUrl)

            val accessCheckoutDiscoveryAsyncTask = apiDiscoveryAsyncTaskFactory.getAsyncTask(asyncTaskResultCallback, discoverLinks)
            accessCheckoutDiscoveryAsyncTask.execute(baseUrl)
        } else {
            debugLog(TAG, "Task result was already present. Num of currentAttempts: ${currentAttempts.get()}")
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
