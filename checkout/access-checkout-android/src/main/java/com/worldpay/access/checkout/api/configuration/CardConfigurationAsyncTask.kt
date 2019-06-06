package com.worldpay.access.checkout.api.configuration

import android.os.AsyncTask
import com.worldpay.access.checkout.api.AccessCheckoutException
import com.worldpay.access.checkout.api.AccessCheckoutException.AccessCheckoutConfigurationException
import com.worldpay.access.checkout.api.AsyncTaskResult
import com.worldpay.access.checkout.api.AsyncTaskUtils.callbackOnTaskResult
import com.worldpay.access.checkout.api.Callback
import com.worldpay.access.checkout.model.CardConfiguration
import java.net.URL

internal class CardConfigurationAsyncTask(private val callback: Callback<CardConfiguration>,
                                          private val cardConfigurationClient: CardConfigurationClient = CardConfigurationClient()) :
    AsyncTask<String, Void, AsyncTaskResult<CardConfiguration>>() {


    override fun doInBackground(vararg params: String?): AsyncTaskResult<CardConfiguration> {
        return try {
            val baseURL = validateAndGetURL(params)
            val cardConfiguration = cardConfigurationClient.getCardConfiguration(baseURL)
            AsyncTaskResult(cardConfiguration)
        } catch (ex: AccessCheckoutConfigurationException) {
            AsyncTaskResult(ex)
        } catch (ex: Exception) {
            val accessCheckoutConfigurationException = AccessCheckoutConfigurationException("There was an error when trying to fetch the card configuration", ex)
            AsyncTaskResult(accessCheckoutConfigurationException)
        }
    }

    override fun onPostExecute(result: AsyncTaskResult<CardConfiguration>) {
        callbackOnTaskResult(callback, result)
    }

    private fun validateAndGetURL(params: Array<out String?>): String {
        if (params.isEmpty()) throw AccessCheckoutConfigurationException("Null URL specified")
        return params[0]?.let {
            if (it.isBlank()) {
                throw AccessCheckoutConfigurationException("Blank URL specified")
            }
            parseURL(it)
            it
        } ?: throw AccessCheckoutConfigurationException("Null URL specified")
    }

    private fun parseURL(url: String) {
        try {
            URL(url)
        } catch (ex: Exception) {
            throw AccessCheckoutConfigurationException("Invalid URL specified", ex)
        }
    }
}
