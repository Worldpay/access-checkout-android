package com.worldpay.access.checkout.client.validation

import com.worldpay.access.checkout.api.configuration.CardConfigurationClient
import com.worldpay.access.checkout.client.validation.config.CardValidationConfig
import com.worldpay.access.checkout.client.validation.config.CvcValidationConfig
import com.worldpay.access.checkout.client.validation.config.ValidationConfig
import com.worldpay.access.checkout.validation.configuration.CardConfigurationProvider
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
        CardConfigurationProvider(
            baseUrl = validationConfig.baseUrl,
            cardConfigurationClient = CardConfigurationClient()
        )

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

        val panFieldDecorator = fieldDecoratorFactory.getPanDecorator(validationConfig.pan, validationConfig.cvc)
        val expiryDateFieldDecorator = fieldDecoratorFactory.getExpiryDateDecorator(validationConfig.expiryDate)
        val cvcFieldDecorator = fieldDecoratorFactory.getCvcDecorator(validationConfig.cvc, validationConfig.pan)

        panFieldDecorator.decorate()
        expiryDateFieldDecorator.decorate()
        cvcFieldDecorator.decorate()
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

        val cvcFieldDecorator = fieldDecoratorFactory.getCvcDecorator(validationConfig.cvc, null)

        cvcFieldDecorator.decorate()
    }

}
