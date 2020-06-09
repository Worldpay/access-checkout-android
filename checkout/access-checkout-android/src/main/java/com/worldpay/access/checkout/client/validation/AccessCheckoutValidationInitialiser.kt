package com.worldpay.access.checkout.client.validation

import com.worldpay.access.checkout.api.configuration.CardConfigurationClientFactory
import com.worldpay.access.checkout.validation.controller.CardDetailsValidationController
import com.worldpay.access.checkout.validation.controller.CvvDetailsValidationController
import com.worldpay.access.checkout.validation.controller.FieldDecoratorFactory
import com.worldpay.access.checkout.validation.watchers.TextWatcherFactory

object AccessCheckoutValidationInitialiser {

    fun initialise(validationConfig: ValidationConfig) {
        if (validationConfig is CardValidationConfig) {
            initialiseCardValidation(validationConfig)
        } else {
            initialiseCvvValidation(validationConfig as CvvValidationConfig)
        }
    }

    private fun initialiseCardValidation(validationConfig: CardValidationConfig) {
        val textWatcherFactory = TextWatcherFactory(validationConfig.validationListener)

        CardDetailsValidationController(
            panEditText = validationConfig.pan,
            expiryMonthEditText = validationConfig.expiryMonth,
            expiryYearEditText = validationConfig.expiryYear,
            cvvEditText = validationConfig.cvv,
            baseUrl = validationConfig.baseUrl,
            cardConfigurationClient = CardConfigurationClientFactory.createClient(),
            fieldDecoratorFactory = FieldDecoratorFactory(
                textWatcherFactory
            )
        )
    }

    private fun initialiseCvvValidation(validationConfig: CvvValidationConfig) {
        val textWatcherFactory = TextWatcherFactory(validationConfig.validationListener)

        CvvDetailsValidationController(
            cvvEditText = validationConfig.cvv,
            fieldDecoratorFactory = FieldDecoratorFactory(
                textWatcherFactory
            )
        )
    }

}
