package com.worldpay.access.checkout.api.discovery

import android.os.AsyncTask
import com.worldpay.access.checkout.api.*
import com.worldpay.access.checkout.api.AccessCheckoutException.*
import com.worldpay.access.checkout.api.AsyncTaskResult
import com.worldpay.access.checkout.api.AsyncTaskUtils.callbackOnTaskResult
import com.worldpay.access.checkout.api.HttpClient
import com.worldpay.access.checkout.api.serialization.Deserializer
import com.worldpay.access.checkout.logging.LoggingUtils.Companion.debugLog
import java.net.MalformedURLException
import java.net.URL

internal class AccessCheckoutDiscoveryAsyncTask(
    private val callback: Callback<String>,
    private val serviceRootDeserializer: Deserializer<String>,
    private val sessionsResourceDeserializer: Deserializer<String>,
    private val httpClient: HttpClient
) : AsyncTask<String, Any, AsyncTaskResult<String>>() {

    companion object {
        private const val TAG = "AccessCheckoutDiscoveryAsyncTask"
    }

    override fun doInBackground(vararg params: String?): AsyncTaskResult<String> {
        return try {
            debugLog(TAG, "Sending request to service discovery endpoint")
            val vtsServiceUrl = fetchLinkFromUrl(params[0], serviceRootDeserializer)
            val url = fetchLinkFromUrl(vtsServiceUrl, sessionsResourceDeserializer)
            debugLog(TAG, "Received response from service discovery endpoint")
            AsyncTaskResult(url)
        } catch (e: AccessCheckoutHttpException) {
            val errorMessage = "An error was thrown when trying to make a connection to the service"
            debugLog(TAG, errorMessage)
            AsyncTaskResult(AccessCheckoutDiscoveryException(errorMessage, e))
        } catch (e: Exception) {
            val errorMessage = "An error was thrown when trying to make a connection to the service"
            debugLog(TAG, e.message ?: errorMessage)
            AsyncTaskResult(e)
        }
    }

    override fun onPostExecute(result: AsyncTaskResult<String>) {
        callbackOnTaskResult(callback, result)
    }

    private fun fetchLinkFromUrl(url: String?, deserializer: Deserializer<String>): String {
        val httpUrl = try {
            URL(url)
        } catch (e: MalformedURLException) {
            debugLog(TAG, "Invalid URL supplied: $url")
            throw AccessCheckoutDiscoveryException("Invalid URL supplied: $url", e)
        }
        return httpClient.doGet(httpUrl, deserializer)
    }
}