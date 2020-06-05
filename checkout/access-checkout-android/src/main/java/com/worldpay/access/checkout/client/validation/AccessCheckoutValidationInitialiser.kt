package com.worldpay.access.checkout.client.validation

import com.worldpay.access.checkout.api.configuration.CardConfigurationClientFactory
import com.worldpay.access.checkout.validation.CardValidationController
import com.worldpay.access.checkout.validation.ValidationResultHandler
import com.worldpay.access.checkout.validation.card.CardDetailComponents
import com.worldpay.access.checkout.validation.watchers.TextWatcherFactory

object AccessCheckoutValidationInitialiser {

    fun initialise(validationConfig: ValidationConfig) {
        initialiseCardValidation(validationConfig as CardValidationConfig)
    }

    private fun initialiseCardValidation(validationConfig: CardValidationConfig) {
        val cardDetailComponents = CardDetailComponents(
            pan = validationConfig.pan,
            expiryMonth = validationConfig.expiryMonth,
            expiryYear = validationConfig.expiryYear,
            cvv = validationConfig.cvv
        )

        val validationResultHandler =
            ValidationResultHandler(
                validationListener = validationConfig.validationListener
            )

        val textWatcherFactory = TextWatcherFactory(
            validationResultHandler = validationResultHandler,
            cardDetailComponents = cardDetailComponents
        )

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