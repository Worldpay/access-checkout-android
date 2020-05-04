package com.worldpay.access.checkout.api.configuration

import android.os.AsyncTask
import com.worldpay.access.checkout.api.*
import com.worldpay.access.checkout.api.AccessCheckoutException.AccessCheckoutConfigurationException
import com.worldpay.access.checkout.api.AsyncTaskUtils.callbackOnTaskResult
import com.worldpay.access.checkout.model.CardConfiguration
import com.worldpay.access.checkout.util.logging.LoggingUtils.debugLog
import java.net.URL

internal class CardConfigurationAsyncTask(private val callback: Callback<CardConfiguration>,
                                          private val urlFactory: URLFactory = URLFactoryImpl(),
                                          private val httpClient: HttpClient = HttpClient(),
                                          private val cardConfigurationParser: CardConfigurationParser = CardConfigurationParser()
) :
    AsyncTask<String, Void, AsyncTaskResult<CardConfiguration>>() {

    companion object {
        private const val TAG = "CardConfigurationAsyncTask"
        private const val CARD_CONFIGURATION_RESOURCE = "access-checkout/cardTypes.json"
    }

    override fun doInBackground(vararg params: String?): AsyncTaskResult<CardConfiguration> {
        return try {
            validateURL(params)
            val url = urlFactory.getURL("${params[0]}/$CARD_CONFIGURATION_RESOURCE")
            val cardConfiguration = httpClient.doGet(url, cardConfigurationParser)
            debugLog(TAG, "Received card configuration: $cardConfiguration")
            AsyncTaskResult(cardConfiguration)
        } catch (ex: AccessCheckoutConfigurationException) {
            debugLog(TAG, "AccessCheckoutException thrown when fetching card configuration: $ex")
            AsyncTaskResult(ex)
        } catch (ex: Exception) {
            val message = "There was an error when trying to fetch the card configuration"
            debugLog(TAG, "$message: $ex")
            val accessCheckoutConfigurationException = AccessCheckoutConfigurationException(message, ex)
            AsyncTaskResult(accessCheckoutConfigurationException)
        }
    }

    override fun onPostExecute(result: AsyncTaskResult<CardConfiguration>) {
        callbackOnTaskResult(callback, result)
    }

    private fun validateURL(params: Array<out String?>) {
        val url = params[0]
        if (url.isNullOrBlank()) {
            throw AccessCheckoutConfigurationException("Empty URL specified")
        }
        try {
            URL(url)
        } catch (ex: Exception) {
            throw AccessCheckoutConfigurationException("Invalid URL specified", ex)
        }
    }

}
