package com.worldpay.access.checkout.api.configuration

import android.util.Log
import com.worldpay.access.checkout.api.HttpsClient
import com.worldpay.access.checkout.api.URLFactory
import com.worldpay.access.checkout.api.URLFactoryImpl
import com.worldpay.access.checkout.client.api.exception.AccessCheckoutException
import java.net.URL

internal class CardConfigurationClient(
    baseUrl: URL,
    urlFactory: URLFactory = URLFactoryImpl(),
    private val httpsClient: HttpsClient = HttpsClient(),
    private val cardConfigurationParser: CardConfigurationParser = CardConfigurationParser()
) {

    internal companion object {
        private const val CARD_CONFIGURATION_RESOURCE = "access-checkout/cardTypes.json"
    }

    private val cardConfigUrl = urlFactory.getURL("$baseUrl/$CARD_CONFIGURATION_RESOURCE")

    suspend fun getCardConfiguration(): CardConfiguration {
        try {
            val cardConfiguration = httpsClient.doGet(cardConfigUrl, cardConfigurationParser)
            Log.d(javaClass.simpleName, "Received card configuration: $cardConfiguration")
            return cardConfiguration
        } catch (ex: Exception) {
            val message = "There was an error when trying to fetch the card configuration"
            Log.d(javaClass.simpleName, "$message:", ex)
            throw AccessCheckoutException(message, ex)
        }
    }
}