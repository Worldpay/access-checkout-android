package com.worldpay.access.checkout.api.configuration

object CardConfigurationClientFactory {

    fun createClient(): CardConfigurationClient {
        return CardConfigurationClientImpl()
    }
}