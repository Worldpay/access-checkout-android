package com.worldpay.access.checkout.validation.configuration

import android.util.Log
import com.worldpay.access.checkout.api.configuration.CardConfiguration
import com.worldpay.access.checkout.api.configuration.CardConfigurationClient
import com.worldpay.access.checkout.api.configuration.DefaultCardRules
import com.worldpay.access.checkout.util.coroutine.DispatchersProvider
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.SupervisorJob
import kotlinx.coroutines.launch

internal class CardConfigurationProvider private constructor() {

    companion object {
        private lateinit var cardConfigurationClient: CardConfigurationClient

        val DEFAULT_CONFIG = CardConfiguration(emptyList(), DefaultCardRules.CARD_DEFAULTS)
        var savedCardConfiguration = DEFAULT_CONFIG
        val scope: CoroutineScope =
            CoroutineScope(SupervisorJob() + DispatchersProvider.instance.main)

        fun initialise(
            cardConfigurationClient: CardConfigurationClient,
            observers: List<CardConfigurationObserver>,
            onInitialized: () -> Unit = {}
        ) {
            savedCardConfiguration = DEFAULT_CONFIG
            this.cardConfigurationClient = cardConfigurationClient

            scope.launch {
                try {
                    Log.d(javaClass.simpleName, "Fetching card configuration from client...")
                    val response = cardConfigurationClient.getCardConfiguration()
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
