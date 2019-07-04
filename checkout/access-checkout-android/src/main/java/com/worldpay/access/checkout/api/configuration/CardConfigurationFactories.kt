package com.worldpay.access.checkout.api.configuration

import com.worldpay.access.checkout.Card
import com.worldpay.access.checkout.api.Callback
import com.worldpay.access.checkout.logging.LoggingUtils
import com.worldpay.access.checkout.model.CardConfiguration
import com.worldpay.access.checkout.validation.AccessCheckoutCardValidator

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

/**
 * Responsible for loading the remote card configuration file that is hosted by Access Worldpay
 * and used for driving the card validation logic. When the card configuration has been
 * returned, it will then be set on the `CardValidator` assigned to the `Card` so that the
 * fields will start validating.
 */
object CardConfigurationFactory {
    @JvmStatic
    @JvmOverloads
    fun getRemoteCardConfiguration(
        card: Card,
        baseUrl: String,
        client: CardConfigurationClient = CardConfigurationClientFactory.createClient()
    ) {
        client.getCardConfiguration(baseUrl, object :
            Callback<CardConfiguration> {
            override fun onResponse(error: Exception?, response: CardConfiguration?) {
                response?.let { card.cardValidator = AccessCheckoutCardValidator(it) }
                error?.let { LoggingUtils.debugLog("MainActivity", "Error while fetching card configuration: $it") }
            }
        })
    }
}
