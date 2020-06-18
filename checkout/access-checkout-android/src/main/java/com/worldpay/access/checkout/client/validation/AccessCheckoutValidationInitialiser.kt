package com.worldpay.access.checkout.client.validation

import com.worldpay.access.checkout.api.configuration.CardConfigurationClientFactory
import com.worldpay.access.checkout.client.validation.config.CardValidationConfig
import com.worldpay.access.checkout.client.validation.config.CvvValidationConfig
import com.worldpay.access.checkout.client.validation.config.ValidationConfig
import com.worldpay.access.checkout.validation.controller.CardDetailsValidationController
import com.worldpay.access.checkout.validation.controller.CvvDetailsValidationController
import com.worldpay.access.checkout.validation.controller.FieldDecoratorFactory
import com.worldpay.access.checkout.validation.listeners.text.TextWatcherFactory
import com.worldpay.access.checkout.validation.result.handler.ResultHandlerFactory
import com.worldpay.access.checkout.validation.result.state.CardValidationStateManager
import com.worldpay.access.checkout.validation.result.state.CvcValidationStateManager

object AccessCheckoutValidationInitialiser {

    @JvmStatic
    fun initialise(validationConfig: ValidationConfig) {
        if (validationConfig is CardValidationConfig) {
            initialiseCardValidation(validationConfig)
        } else {
            initialiseCvvValidation(validationConfig as CvvValidationConfig)
        }
    }

    private fun initialiseCardValidation(validationConfig: CardValidationConfig) {
        val validationStateManager = CardValidationStateManager()

        val resultHandlerFactory = ResultHandlerFactory(
            accessCheckoutValidationListener = validationConfig.validationListener,
            fieldValidationStateManager = validationStateManager
        )

        val textWatcherFactory = TextWatcherFactory(resultHandlerFactory)

        CardDetailsValidationController(
            panEditText = validationConfig.pan,
            expiryDateEditText = validationConfig.expiryDate,
            cvvEditText = validationConfig.cvv,
            baseUrl = validationConfig.baseUrl,
            cardConfigurationClient = CardConfigurationClientFactory.createClient(),
            fieldDecoratorFactory = FieldDecoratorFactory(
                textWatcherFactory
            )
        )
    }

    private fun initialiseCvvValidation(validationConfig: CvvValidationConfig) {
        val validationStateManager = CvcValidationStateManager()

        val resultHandlerFactory = ResultHandlerFactory(
            accessCheckoutValidationListener = validationConfig.validationListener,
            fieldValidationStateManager = validationStateManager
        )

        val textWatcherFactory = TextWatcherFactory(resultHandlerFactory)

        CvvDetailsValidationController(
            cvvEditText = validationConfig.cvv,
            fieldDecoratorFactory = FieldDecoratorFactory(
                textWatcherFactory
            )
        )
    }

}
