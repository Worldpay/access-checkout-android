package com.worldpay.access.checkout.api.configuration

import com.worldpay.access.checkout.Card
import com.worldpay.access.checkout.api.Callback
import com.worldpay.access.checkout.model.CardConfiguration
import com.worldpay.access.checkout.util.logging.LoggingUtils
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
 * returned, it will then be set on the [CardValidator] assigned to the `Card` so that the
 * fields will start validating.
 */
object CardConfigurationFactory {

    /**
     * @param card the [Card] instance to apply the [CardConfiguration] to
     * @param baseUrl the base URL for the hosted card configuration
     * @param client (optional) the [CardConfigurationClient]
     */
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
