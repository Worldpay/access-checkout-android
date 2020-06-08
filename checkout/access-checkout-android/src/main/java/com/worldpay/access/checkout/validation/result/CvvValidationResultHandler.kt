package com.worldpay.access.checkout.validation.result

import com.worldpay.access.checkout.client.validation.AccessCheckoutCvvValidationListener
import com.worldpay.access.checkout.validation.ValidationResult

class CvvValidationResultHandler(
    private val validationListener: AccessCheckoutCvvValidationListener,
    private val validationStateManager: ValidationStateManager
) {

    fun handleResult(validationResult: ValidationResult) {
        validationListener.onCvvValidated(validationResult.complete)
        validationStateManager.cvvValidated.set(validationResult.complete)

        if (validationStateManager.isAllValid()) {
            validationListener.onValidationSuccess()
        }
    }

}