package com.worldpay.access.checkout.validation.controller

import com.worldpay.access.checkout.api.Callback
import com.worldpay.access.checkout.api.configuration.CardConfiguration
import com.worldpay.access.checkout.api.configuration.CardConfigurationClient
import com.worldpay.access.checkout.api.configuration.DefaultCardRules.CARD_DEFAULTS
import com.worldpay.access.checkout.util.logging.LoggingUtils.debugLog
import com.worldpay.access.checkout.validation.decorators.CvcFieldDecorator
import com.worldpay.access.checkout.validation.decorators.ExpiryDateFieldDecorator
import com.worldpay.access.checkout.validation.decorators.PanFieldDecorator

internal class CardDetailsValidationController(
    private val panFieldDecorator : PanFieldDecorator,
    private val expiryDateFieldDecorator : ExpiryDateFieldDecorator,
    private val cvcFieldDecorator : CvcFieldDecorator,
    baseUrl : String,
    cardConfigurationClient : CardConfigurationClient
) {

    init {
        decorateFields(CardConfiguration(emptyList(), CARD_DEFAULTS))

        // fetch remote cardConfiguration - resets the cardConfiguration field if a remote one is found
        cardConfigurationClient.getCardConfiguration(baseUrl, getCardConfigurationCallback())
    }

    private fun decorateFields(cardConfiguration: CardConfiguration) {
        panFieldDecorator.decorate(cardConfiguration)
        expiryDateFieldDecorator.decorate(cardConfiguration)
        cvcFieldDecorator.decorate(cardConfiguration)
    }

    private fun getCardConfigurationCallback(): Callback<CardConfiguration> {
        return object : Callback<CardConfiguration> {
            override fun onResponse(error: Exception?, response: CardConfiguration?) {
                response?.let {cardConfig ->
                    debugLog(javaClass.simpleName, "Retrieved remote card configuration")
                    decorateFields(cardConfig)
                }
                error?.let {
                    debugLog(javaClass.simpleName,"Error while fetching card configuration: $it")
                }
            }
        }
    }

}
