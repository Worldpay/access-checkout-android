package com.worldpay.access.checkout.client.validation

import com.worldpay.access.checkout.api.configuration.CardConfigurationClientFactory
import com.worldpay.access.checkout.client.validation.config.CardValidationConfig
import com.worldpay.access.checkout.client.validation.config.CvvValidationConfig
import com.worldpay.access.checkout.client.validation.config.ValidationConfig
import com.worldpay.access.checkout.validation.controller.CardDetailsValidationController
import com.worldpay.access.checkout.validation.controller.CvvDetailsValidationController
import com.worldpay.access.checkout.validation.controller.FieldDecoratorFactory
import com.worldpay.access.checkout.validation.state.CardValidationStateManager
import com.worldpay.access.checkout.validation.state.CvcValidationStateManager
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
        val validationStateManager =
            CardValidationStateManager()
        val textWatcherFactory = TextWatcherFactory(
            accessCheckoutValidationListener = validationConfig.validationListener,
            validationStateManager = validationStateManager
        )

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
        val validationStateManager =
            CvcValidationStateManager()
        val textWatcherFactory = TextWatcherFactory(
            accessCheckoutValidationListener = validationConfig.validationListener,
            validationStateManager = validationStateManager
        )

        CvvDetailsValidationController(
            cvvEditText = validationConfig.cvv,
            fieldDecoratorFactory = FieldDecoratorFactory(
                textWatcherFactory
            )
        )
    }

}
