package com.worldpay.access.checkout.api.configuration

import com.worldpay.access.checkout.api.HttpClient
import com.worldpay.access.checkout.api.serialization.Deserializer
import com.worldpay.access.checkout.model.CardConfiguration
import java.net.URL

internal class CardConfigurationClient(private val httpClient: HttpClient,
                                       private val cardConfigurationDeserializer: Deserializer<CardConfiguration>) {

    companion object {
        private const val CARD_CONFIGURATION_RESOURCE = "access-checkout/cardConfiguration.json"
    }

    fun getCardConfiguration(baseURL: String): CardConfiguration {
        return httpClient.doGet(URL("$baseURL/$CARD_CONFIGURATION_RESOURCE"), cardConfigurationDeserializer)
    }
}