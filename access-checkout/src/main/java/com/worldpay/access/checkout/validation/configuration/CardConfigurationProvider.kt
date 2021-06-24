package com.worldpay.access.checkout.validation.configuration

import com.worldpay.access.checkout.api.Callback
import com.worldpay.access.checkout.api.configuration.CardConfiguration
import com.worldpay.access.checkout.api.configuration.CardConfigurationClient
import com.worldpay.access.checkout.api.configuration.DefaultCardRules
import com.worldpay.access.checkout.util.logging.LoggingUtils.debugLog

internal class CardConfigurationProvider(
    baseUrl: String,
    cardConfigurationClient: CardConfigurationClient,
    private val observers: List<CardConfigurationObserver>
) {

    companion object {
        private var cardConfiguration = CardConfiguration(emptyList(), DefaultCardRules.CARD_DEFAULTS)

        fun getCardConfiguration(): CardConfiguration {
            return cardConfiguration
        }
    }

    init {
        cardConfiguration = CardConfiguration(emptyList(), DefaultCardRules.CARD_DEFAULTS)

        cardConfigurationClient.getCardConfiguration(
            baseUrl = baseUrl,
            callback = getCardConfigurationCallback()
        )
    }

    private fun getCardConfigurationCallback(): Callback<CardConfiguration> {
        return object : Callback<CardConfiguration> {
            override fun onResponse(error: Exception?, response: CardConfiguration?) {
                response?.let { cardConfig ->
                    debugLog(javaClass.simpleName, "Retrieved remote card configuration")
                    cardConfiguration = cardConfig
                    for (observer in observers) {
                        observer.update()
                    }
                }
                error?.let {
                    debugLog(javaClass.simpleName, "Error while fetching card configuration: $it")
                }
            }
        }
    }
}
