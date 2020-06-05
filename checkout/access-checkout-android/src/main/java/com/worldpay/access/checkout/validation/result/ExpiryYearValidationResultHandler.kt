package com.worldpay.access.checkout.validation.result

import com.worldpay.access.checkout.client.validation.AccessCheckoutExpiryDateValidatedSuccessListener
import com.worldpay.access.checkout.validation.ValidationResult

class ExpiryYearValidationResultHandler(
    private val validationListener: AccessCheckoutExpiryDateValidatedSuccessListener,
    private val validationStateManager: ValidationStateManager
) {

    fun handleResult(validationResult: ValidationResult) {
        validationListener.onExpiryDateValidated(validationResult.complete)
        validationStateManager.yearValidated.set(validationResult.complete)

        if (validationStateManager.isAllValid()) {
            validationListener.onValidationSuccess()
        }
    }

}