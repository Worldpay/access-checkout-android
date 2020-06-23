package com.worldpay.access.checkout.client.validation

import com.worldpay.access.checkout.api.configuration.CardConfigurationClientFactory
import com.worldpay.access.checkout.client.validation.config.CardValidationConfig
import com.worldpay.access.checkout.client.validation.config.CvcValidationConfig
import com.worldpay.access.checkout.client.validation.config.ValidationConfig
import com.worldpay.access.checkout.validation.controller.CardDetailsValidationController
import com.worldpay.access.checkout.validation.controller.CvcDetailsValidationController
import com.worldpay.access.checkout.validation.decorators.FieldDecoratorFactory
import com.worldpay.access.checkout.validation.filters.LengthFilterFactory
import com.worldpay.access.checkout.validation.listeners.focus.FocusChangeListenerFactory
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
            initialiseCvcValidation(validationConfig as CvcValidationConfig)
        }
    }

    private fun initialiseCardValidation(validationConfig: CardValidationConfig) {
        val validationStateManager = CardValidationStateManager

        val resultHandlerFactory = ResultHandlerFactory(
            accessCheckoutValidationListener = validationConfig.validationListener,
            fieldValidationStateManager = validationStateManager,
            lifecycleOwner = validationConfig.lifecycleOwner
        )

        val textWatcherFactory = TextWatcherFactory(resultHandlerFactory)
        val focusChangeListenerFactory = FocusChangeListenerFactory(resultHandlerFactory)
        val lengthFilterFactory = LengthFilterFactory()

        val fieldDecoratorFactory = FieldDecoratorFactory(
            textWatcherFactory,
            focusChangeListenerFactory,
            lengthFilterFactory
        )

        CardDetailsValidationController(
            panFieldDecorator = fieldDecoratorFactory.getPanDecorator(validationConfig.pan, validationConfig.cvc),
            expiryDateFieldDecorator = fieldDecoratorFactory.getExpiryDateDecorator(validationConfig.expiryDate),
            cvcFieldDecorator = fieldDecoratorFactory.getCvcDecorator(validationConfig.cvc, validationConfig.pan),
            baseUrl = validationConfig.baseUrl,
            cardConfigurationClient = CardConfigurationClientFactory.createClient()
        )
    }

    private fun initialiseCvcValidation(validationConfig: CvcValidationConfig) {
        val validationStateManager = CvcValidationStateManager

        val resultHandlerFactory = ResultHandlerFactory(
            accessCheckoutValidationListener = validationConfig.validationListener,
            fieldValidationStateManager = validationStateManager,
            lifecycleOwner = validationConfig.lifecycleOwner
        )

        val textWatcherFactory = TextWatcherFactory(resultHandlerFactory)
        val focusChangeListenerFactory = FocusChangeListenerFactory(resultHandlerFactory)
        val lengthFilterFactory = LengthFilterFactory()

        val fieldDecoratorFactory = FieldDecoratorFactory(
            textWatcherFactory,
            focusChangeListenerFactory,
            lengthFilterFactory
        )

        CvcDetailsValidationController(
            cvcFieldDecorator = fieldDecoratorFactory.getCvcDecorator(validationConfig.cvc, null)
        )
    }

}
