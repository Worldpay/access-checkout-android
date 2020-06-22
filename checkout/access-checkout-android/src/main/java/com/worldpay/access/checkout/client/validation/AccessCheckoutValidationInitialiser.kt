package com.worldpay.access.checkout.client.validation

import androidx.lifecycle.AndroidViewModel
import androidx.lifecycle.LiveData
import com.worldpay.access.checkout.api.configuration.CardConfigurationClientFactory
import com.worldpay.access.checkout.client.validation.config.CardValidationConfig
import com.worldpay.access.checkout.client.validation.config.CvcValidationConfig
import com.worldpay.access.checkout.client.validation.config.ValidationConfig
import com.worldpay.access.checkout.validation.controller.CardDetailsValidationController
import com.worldpay.access.checkout.validation.controller.CvcDetailsValidationController
import com.worldpay.access.checkout.validation.controller.FieldDecoratorFactory
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
        val fieldDecoratorFactory = FieldDecoratorFactory(textWatcherFactory, focusChangeListenerFactory)

        CardDetailsValidationController(
            panEditText = validationConfig.pan,
            expiryDateEditText = validationConfig.expiryDate,
            cvcEditText = validationConfig.cvc,
            baseUrl = validationConfig.baseUrl,
            cardConfigurationClient = CardConfigurationClientFactory.createClient(),
            fieldDecoratorFactory = fieldDecoratorFactory
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
        val fieldDecoratorFactory = FieldDecoratorFactory(textWatcherFactory, focusChangeListenerFactory)

        CvcDetailsValidationController(
            cvcEditText = validationConfig.cvc,
            fieldDecoratorFactory = fieldDecoratorFactory
        )
    }

}
