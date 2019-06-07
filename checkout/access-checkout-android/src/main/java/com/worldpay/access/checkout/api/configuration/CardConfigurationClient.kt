package com.worldpay.access.checkout.api.configuration

import com.worldpay.access.checkout.api.Callback
import com.worldpay.access.checkout.model.CardConfiguration
import java.net.URL

open class CardConfigurationClient(callback: Callback<CardConfiguration>) {

    private val cardConfigurationAsyncTask: CardConfigurationAsyncTask = getAsyncTask(callback)

    fun getCardConfiguration(baseURL: String) {
        cardConfigurationAsyncTask.execute(baseURL)
    }

    private fun getAsyncTask(callback: Callback<CardConfiguration>): CardConfigurationAsyncTask {
        return CardConfigurationAsyncTask(callback)
    }
}