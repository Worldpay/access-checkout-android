package com.worldpay.access.checkout.client.validation

import com.worldpay.access.checkout.api.configuration.CardConfigurationClient
import com.worldpay.access.checkout.client.validation.config.CardValidationConfig
import com.worldpay.access.checkout.client.validation.config.CvcValidationConfig
import com.worldpay.access.checkout.client.validation.config.ValidationConfig
import com.worldpay.access.checkout.validation.configuration.CardConfigurationProvider
import com.worldpay.access.checkout.validation.decorators.FieldDecoratorFactory
import com.worldpay.access.checkout.validation.filters.AccessCheckoutInputFilterFactory
import com.worldpay.access.checkout.validation.listeners.focus.FocusChangeListenerFactory
import com.worldpay.access.checkout.validation.listeners.text.TextWatcherFactory
import com.worldpay.access.checkout.validation.result.handler.ResultHandlerFactory
import com.worldpay.access.checkout.validation.result.state.CardValidationStateManager
import com.worldpay.access.checkout.validation.result.state.CvcValidationStateManager
import java.net.URL

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
    fun initialise(
        validationConfig: ValidationConfig,
    ) {
        if (validationConfig is CardValidationConfig) {
            initialiseCardValidation(validationConfig)
        } else {
            initialiseCvcValidation(validationConfig as CvcValidationConfig)
        }
    }

    private fun initialiseCardValidation(
        validationConfig: CardValidationConfig,
    ) {
        val resultHandlerFactory = ResultHandlerFactory(
            accessCheckoutValidationListener = validationConfig.validationListener,
            fieldValidationStateManager = CardValidationStateManager(
                validationConfig.pan,
                validationConfig.expiryDate,
                validationConfig.cvc
            ),
            lifecycleOwner = validationConfig.lifecycleOwner
        )

        val textWatcherFactory = TextWatcherFactory(resultHandlerFactory)
        val focusChangeListenerFactory = FocusChangeListenerFactory(resultHandlerFactory)
        val accessCheckoutInputFilterFactory = AccessCheckoutInputFilterFactory()

        val fieldDecoratorFactory = FieldDecoratorFactory(
            textWatcherFactory,
            focusChangeListenerFactory,
            accessCheckoutInputFilterFactory
        )

        val panFieldDecorator = fieldDecoratorFactory.getPanDecorator(
            validationConfig.pan,
            validationConfig.cvc,
            validationConfig.acceptedCardBrands,
            validationConfig.enablePanFormatting,
            validationConfig.checkoutId,
        )

        val expiryDateFieldDecorator =
            fieldDecoratorFactory.getExpiryDateDecorator(validationConfig.expiryDate)
        val cvcFieldDecorator =
            fieldDecoratorFactory.getCvcDecorator(validationConfig.cvc, validationConfig.pan)


        CardConfigurationProvider.initialise(
            cardConfigurationClient = CardConfigurationClient(URL(validationConfig.baseUrl)),
            observers = listOf(panFieldDecorator, expiryDateFieldDecorator, cvcFieldDecorator)
        )
        panFieldDecorator.decorate()
        expiryDateFieldDecorator.decorate()
        cvcFieldDecorator.decorate()
    }

    private fun initialiseCvcValidation(validationConfig: CvcValidationConfig) {
        val resultHandlerFactory = ResultHandlerFactory(
            accessCheckoutValidationListener = validationConfig.validationListener,
            fieldValidationStateManager = CvcValidationStateManager(validationConfig.cvc),
            lifecycleOwner = validationConfig.lifecycleOwner
        )

        val textWatcherFactory = TextWatcherFactory(resultHandlerFactory)
        val focusChangeListenerFactory = FocusChangeListenerFactory(resultHandlerFactory)
        val accessCheckoutInputFilterFactory = AccessCheckoutInputFilterFactory()

        val fieldDecoratorFactory = FieldDecoratorFactory(
            textWatcherFactory,
            focusChangeListenerFactory,
            accessCheckoutInputFilterFactory,
        )

        val cvcFieldDecorator = fieldDecoratorFactory.getCvcDecorator(validationConfig.cvc, null)

        cvcFieldDecorator.decorate()
    }
}
