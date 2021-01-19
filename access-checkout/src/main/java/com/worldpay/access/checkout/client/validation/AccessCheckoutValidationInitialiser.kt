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

/**
 * Class that is responsible for initialising validation using a given [ValidationConfig]
 */
object AccessCheckoutValidationInitialiser {

    /**
     * This function should be used when wanting to initialise the validation using the [ValidationConfig]
     * provided.
     *
     * @param[validationConfig] [ValidationConfig] represents the configuration that should be used to initialise validation
     */
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

        val panFieldDecorator = fieldDecoratorFactory.getPanDecorator(validationConfig.pan, validationConfig.cvc, validationConfig.acceptedCardBrands)
        val expiryDateFieldDecorator = fieldDecoratorFactory.getExpiryDateDecorator(validationConfig.expiryDate)
        val cvcFieldDecorator = fieldDecoratorFactory.getCvcDecorator(validationConfig.cvc, validationConfig.pan)

        CardConfigurationProvider(
            baseUrl = validationConfig.baseUrl,
            cardConfigurationClient = CardConfigurationClient(),
            observers = listOf(panFieldDecorator, expiryDateFieldDecorator, cvcFieldDecorator)
        )

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
