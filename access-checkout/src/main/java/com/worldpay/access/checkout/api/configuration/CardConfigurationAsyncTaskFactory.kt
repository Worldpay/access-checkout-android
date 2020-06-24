package com.worldpay.access.checkout.api.configuration

import com.worldpay.access.checkout.api.Callback

internal class CardConfigurationAsyncTaskFactory {

    fun getAsyncTask(callback: Callback<CardConfiguration>): CardConfigurationAsyncTask {
        return CardConfigurationAsyncTask(callback)
    }

}
