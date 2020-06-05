package com.worldpay.access.checkout.client.validation

import com.worldpay.access.checkout.api.configuration.CardConfigurationClientFactory
import com.worldpay.access.checkout.validation.CardValidationController
import com.worldpay.access.checkout.validation.ValidationResultHandler
import com.worldpay.access.checkout.validation.watchers.TextWatcherFactory

object AccessCheckoutValidationInitialiser {

    fun initialise(validationConfig: ValidationConfig) {
        initialiseCardValidation(validationConfig as CardValidationConfig)
    }

    private fun initialiseCardValidation(validationConfig: CardValidationConfig) {
        val validationResultHandler = ValidationResultHandler(validationConfig.validationListener)
        val textWatcherFactory = TextWatcherFactory(validationResultHandler)

        CardValidationController(
            panEditText = validationConfig.pan,
            expiryMonthEditText = validationConfig.expiryMonth,
            expiryYearEditText = validationConfig.expiryYear,
            cvvEditText = validationConfig.cvv,
            baseUrl = validationConfig.baseUrl,
            cardConfigurationClient = CardConfigurationClientFactory.createClient(),
            textWatcherFactory = textWatcherFactory
        )
    }

}