package com.worldpay.access.checkout.validation.result

import com.worldpay.access.checkout.client.validation.AccessCheckoutCvvValidatedSuccessListener
import com.worldpay.access.checkout.validation.ValidationResult

class CvvValidationResultHandler(
    private val validationListener: AccessCheckoutCvvValidatedSuccessListener,
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