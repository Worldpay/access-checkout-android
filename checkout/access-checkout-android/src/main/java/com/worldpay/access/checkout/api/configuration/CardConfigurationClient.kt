package com.worldpay.access.checkout.api.configuration

import com.worldpay.access.checkout.api.Callback

/**
 * A client to fetch a [CardConfiguration]
 */
internal interface CardConfigurationClient {

    /**
     * @param baseURL the base URL of the server to fetch the config from
     * @param callback a callback function which will be used to return the [CardConfiguration] back to
     */
    fun getCardConfiguration(baseURL: String, callback: Callback<CardConfiguration>)
}

internal class CardConfigurationClientImpl(
    private val cardConfigurationAsyncTaskFactory: CardConfigurationAsyncTaskFactory = CardConfigurationAsyncTaskFactory()) :
    CardConfigurationClient {

    override fun getCardConfiguration(baseURL: String, callback: Callback<CardConfiguration>) {
        val asyncTask = cardConfigurationAsyncTaskFactory.getAsyncTask(callback)
        asyncTask.execute(baseURL)
    }
}
