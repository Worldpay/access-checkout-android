package com.worldpay.access.checkout.validation.configuration

import android.util.Log
import com.worldpay.access.checkout.api.configuration.CardConfiguration
import com.worldpay.access.checkout.api.configuration.CardConfigurationClient
import com.worldpay.access.checkout.api.configuration.DefaultCardRules
import com.worldpay.access.checkout.util.coroutine.DispatchersProvider
import com.worldpay.access.checkout.util.coroutine.IDispatchersProvider
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.SupervisorJob
import kotlinx.coroutines.launch
import kotlinx.coroutines.withContext

internal class CardConfigurationProvider private constructor() {

    companion object {
        private lateinit var cardConfigurationClient: CardConfigurationClient

        val DEFAULT_CONFIG = CardConfiguration(emptyList(), DefaultCardRules.CARD_DEFAULTS)
        var savedCardConfiguration = DEFAULT_CONFIG

        fun initialise(
            cardConfigurationClient: CardConfigurationClient,
            observers: List<CardConfigurationObserver>,
            dispatcherProvider: IDispatchersProvider = DispatchersProvider.instance,
            onInitialized: () -> Unit = {}
        ) {
            savedCardConfiguration = DEFAULT_CONFIG
            this.cardConfigurationClient = cardConfigurationClient

            CoroutineScope(SupervisorJob() + dispatcherProvider.io).launch {
                try {
                    Log.d(javaClass.simpleName, "Fetching card configuration from client...")
                    val response = withContext(dispatcherProvider.io) {
                        cardConfigurationClient.getCardConfiguration()
                    }
                    Log.d(javaClass.simpleName, "Card configuration fetched successfully.")
                    savedCardConfiguration = response
                } catch (ex: Exception) {
                    Log.d(
                        javaClass.simpleName,
                        "Error while fetching card configuration (setting defaults): ${ex.message}"
                    )
                    savedCardConfiguration = DEFAULT_CONFIG
                } finally {
                    for (observer in observers) {
                        try {
                            observer.update() // Notify observers regardless of success or failure
                        } catch (ex: Exception) {
                            Log.d(
                                javaClass.simpleName,
                                "Error while updating observer: ${ex.message}"
                            )
                        }
                    }
                    onInitialized()
                }
            }
        }

        fun getCardConfiguration(): CardConfiguration {
            return savedCardConfiguration
        }
    }
}
