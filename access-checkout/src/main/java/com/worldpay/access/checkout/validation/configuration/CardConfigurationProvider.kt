package com.worldpay.access.checkout.validation.configuration

import android.util.Log
import com.worldpay.access.checkout.api.configuration.CardConfiguration
import com.worldpay.access.checkout.api.configuration.CardConfigurationClient
import com.worldpay.access.checkout.api.configuration.DefaultCardRules
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.runBlocking

internal object CardConfigurationProvider {

    private lateinit var cardConfigurationClient: CardConfigurationClient
    private var observers: List<CardConfigurationObserver> = emptyList()
    private var cardConfiguration: CardConfiguration? = null

    fun initialize(
        cardConfigurationClient: CardConfigurationClient,
        observers: List<CardConfigurationObserver>
    ) {
        this.cardConfigurationClient = cardConfigurationClient
        this.observers = observers

        runBlocking(Dispatchers.IO) {
            cardConfiguration = fetchCardConfiguration(cardConfigurationClient)

            for (observer in observers) {
                observer.update()
            }
        }
    }

    fun getCardConfiguration(): CardConfiguration {
        return cardConfiguration ?: CardConfiguration(emptyList(), DefaultCardRules.CARD_DEFAULTS)
    }

    private suspend fun fetchCardConfiguration(cardConfigurationClient: CardConfigurationClient): CardConfiguration {
        return try {
            cardConfigurationClient.getCardConfiguration()
        } catch (ex: Exception) {
            Log.d(
                javaClass.simpleName,
                "Error while fetching card configuration (setting defaults): $ex", ex
            )
            CardConfiguration(emptyList(), DefaultCardRules.CARD_DEFAULTS)
        }
    }

    //For testing purposes
    internal fun reset() {
        cardConfiguration = null
        observers = emptyList()
    }

}