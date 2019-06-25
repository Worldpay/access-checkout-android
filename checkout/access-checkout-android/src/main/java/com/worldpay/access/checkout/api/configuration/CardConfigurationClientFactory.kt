package com.worldpay.access.checkout.api.configuration

/**
 * Creates a [CardConfigurationClient] instance
 */
object CardConfigurationClientFactory {

    /**
     * @return a [CardConfigurationClient] instance
     */
    @JvmStatic
    fun createClient(): CardConfigurationClient {
        return CardConfigurationClientImpl()
    }
}