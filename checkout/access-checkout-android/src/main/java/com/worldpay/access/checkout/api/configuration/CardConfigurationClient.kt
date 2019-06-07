package com.worldpay.access.checkout.api.configuration

import com.worldpay.access.checkout.api.Callback
import com.worldpay.access.checkout.model.CardConfiguration
import java.net.URL

class CardConfigurationClient {

    fun getCardConfiguration(baseURL: String, callback: Callback<CardConfiguration>) {
        val cardConfigurationAsyncTask = CardConfigurationAsyncTask(callback)
        cardConfigurationAsyncTask.execute(baseURL)
    }
}