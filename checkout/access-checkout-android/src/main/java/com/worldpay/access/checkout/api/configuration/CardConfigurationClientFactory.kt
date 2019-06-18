package com.worldpay.access.checkout.api.configuration

/**
 * Creates a [CardConfigurationClient] instance
 */
object CardConfigurationClientFactory {

    /**
     * @return a [CardConfigurationClient] instance
     */
    fun createClient(): CardConfigurationClient {
        return CardConfigurationClientImpl()
    }
}