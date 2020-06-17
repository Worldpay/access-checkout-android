package com.worldpay.access.checkout.api.configuration

/**
 * Creates a [CardConfigurationClient] instance
 */
internal object CardConfigurationClientFactory {

    /**
     * @return a [CardConfigurationClient] instance
     */
    @JvmStatic
    internal fun createClient(): CardConfigurationClient {
        return CardConfigurationClientImpl()
    }

}
