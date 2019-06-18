package com.worldpay.access.checkout.api.configuration

import com.worldpay.access.checkout.api.Callback
import com.worldpay.access.checkout.model.CardConfiguration

internal class CardConfigurationAsyncTaskFactory {

    fun getAsyncTask(callback: Callback<CardConfiguration>): CardConfigurationAsyncTask {
        return CardConfigurationAsyncTask(callback)
    }

}
