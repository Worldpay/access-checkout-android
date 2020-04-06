package com.worldpay.access.checkout.api.discovery

import com.worldpay.access.checkout.api.AccessCheckoutException.*
import com.worldpay.access.checkout.api.AsyncTaskResult
import com.worldpay.access.checkout.api.Callback
import com.worldpay.access.checkout.logging.LoggingUtils.debugLog
import java.util.concurrent.atomic.AtomicInteger

internal class AccessCheckoutDiscoveryClient(
    private val accessCheckoutDiscoveryAsyncTaskFactory: AccessCheckoutDiscoveryAsyncTaskFactory,
    private val discoveryCache: DiscoveryCache = DiscoveryCache
) {

    private val currentAttempts = AtomicInteger(0)
    private val maxAttempts = 2

    companion object {
        private const val TAG = "AccessCheckoutDiscoveryClient"
    }

    fun discover(baseUrl: String, callback: Callback<String>? = null, discoverLinks: DiscoverLinks) {
        if (baseUrl.isBlank()) {
            throw AccessCheckoutDiscoveryException("No URL supplied")
        }

        val asyncTaskResult = discoveryCache.getResult(discoverLinks)
        if (asyncTaskResult == null) {
            currentAttempts.addAndGet(1)
            debugLog(TAG, "Discovering endpoint")

            val asyncTaskResultCallback = object : Callback<String> {
                override fun onResponse(error: Exception?, response: String?) = handleAsyncTaskResponse(callback, response, error, discoverLinks)
            }
            val accessCheckoutDiscoveryAsyncTask = accessCheckoutDiscoveryAsyncTaskFactory.getAsyncTask(asyncTaskResultCallback, discoverLinks)
            accessCheckoutDiscoveryAsyncTask.execute(baseUrl)
        } else {
            debugLog(TAG, "Task result was already present. Num of currentAttempts: ${currentAttempts.get()}")
            asyncTaskResult.result?.let {
                callback?.onResponse(null, it)
            }

            asyncTaskResult.error?.let {
                if (currentAttempts.get() < maxAttempts) {
                    discoveryCache.clearResult(discoverLinks)
                    discover(baseUrl, callback, discoverLinks)
                } else {
                    callback?.onResponse(it, null)
                }
            }
        }
    }

    private fun handleAsyncTaskResponse(callback: Callback<String>?, response: String?, error: Exception?, discoverLinks: DiscoverLinks) {
        response?.let { sessionResponse ->
            discoveryCache.saveResult(discoverLinks, AsyncTaskResult(sessionResponse))
            callback?.onResponse(null, sessionResponse)
        }
        error?.let { sessionError ->
            discoveryCache.saveResult(discoverLinks, AsyncTaskResult(sessionError))
            callback?.onResponse(sessionError, null)
        }
    }


}
