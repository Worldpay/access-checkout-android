package com.worldpay.access.checkout.validation.configuration

import android.util.Log
import com.worldpay.access.checkout.api.configuration.CardConfiguration
import com.worldpay.access.checkout.api.configuration.CardConfigurationClient
import com.worldpay.access.checkout.api.configuration.DefaultCardRules
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.MainScope
import kotlinx.coroutines.launch

internal class CardConfigurationProvider(
    cardConfigurationClient: CardConfigurationClient,
    observers: List<CardConfigurationObserver>
) : CoroutineScope by MainScope() {

    companion object {
        private var cardConfiguration =
            CardConfiguration(emptyList(), DefaultCardRules.CARD_DEFAULTS)

        fun getCardConfiguration(): CardConfiguration {
            return cardConfiguration
        }
    }

    init {
        launch {
            try {
                cardConfiguration = cardConfigurationClient.getCardConfiguration()
                for (observer in observers) {
                    observer.update()
                }
            } catch (ex: Exception) {
                Log.d(
                    javaClass.simpleName,
                    "Error while fetching card configuration (setting defaults): $ex"
                )
                cardConfiguration = CardConfiguration(emptyList(), DefaultCardRules.CARD_DEFAULTS)
            }
        }
    }
}
