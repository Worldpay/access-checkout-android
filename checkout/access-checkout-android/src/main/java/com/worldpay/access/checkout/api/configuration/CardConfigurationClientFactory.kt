package com.worldpay.access.checkout.api.configuration

import com.worldpay.access.checkout.Card
import com.worldpay.access.checkout.api.Callback
import com.worldpay.access.checkout.logging.LoggingUtils
import com.worldpay.access.checkout.model.CardConfiguration
import com.worldpay.access.checkout.validation.AccessCheckoutCardValidator

/**
 * Creates a [CardConfigurationClient] instance
 */
object CardConfigurationClientFactory {

    @JvmStatic
    fun getRemoteConfiguration(cardValidator: Card, baseUrl: String) {
        CardConfigurationClientFactory.createClient().getCardConfiguration(baseUrl, object :
            Callback<CardConfiguration> {
            override fun onResponse(error: Exception?, response: CardConfiguration?) {
                response?.let { cardValidator.cardValidator = AccessCheckoutCardValidator(it) }
                error?.let { LoggingUtils.debugLog("MainActivity", "Error while fetching card configuration: $it") }
            }
        })
    }

    /**
     * @return a [CardConfigurationClient] instance
     */
    @JvmStatic
    internal fun createClient(): CardConfigurationClient {
        return CardConfigurationClientImpl()
    }
}