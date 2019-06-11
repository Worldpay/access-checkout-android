package com.worldpay.access.checkout.api.configuration

import com.worldpay.access.checkout.api.Callback
import com.worldpay.access.checkout.model.CardConfiguration

interface CardConfigurationClient {
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