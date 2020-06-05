package com.worldpay.access.checkout.validation.result

import com.worldpay.access.checkout.api.configuration.CardBrand
import com.worldpay.access.checkout.client.validation.AccessCheckoutPanValidatedSuccessListener
import com.worldpay.access.checkout.validation.ValidationResult

class PanValidationResultHandler(
    private val validationListener: AccessCheckoutPanValidatedSuccessListener,
    private val validationStateManager: ValidationStateManager
) {

    fun handleResult(validationResult: ValidationResult, cardBrand: CardBrand?) {
        validationListener.onPanValidated(cardBrand, validationResult.complete)
        validationStateManager.panValidated.set(validationResult.complete)

        if (validationStateManager.isAllValid()) {
            validationListener.onValidationSuccess()
        }
    }

}