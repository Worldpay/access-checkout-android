package com.worldpay.access.checkout.api.configuration

import com.worldpay.access.checkout.api.Callback

internal class CardConfigurationClient(
    private val cardConfigurationAsyncTaskFactory: CardConfigurationAsyncTaskFactory = CardConfigurationAsyncTaskFactory()
) {

    fun getCardConfiguration(baseUrl: String, callback: Callback<CardConfiguration>) {
        val asyncTask = cardConfigurationAsyncTaskFactory.getAsyncTask(callback)
        asyncTask.execute(baseUrl)
    }
}
