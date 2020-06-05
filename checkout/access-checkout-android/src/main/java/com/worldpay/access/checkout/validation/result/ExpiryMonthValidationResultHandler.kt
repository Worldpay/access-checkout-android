package com.worldpay.access.checkout.validation.result

import com.worldpay.access.checkout.client.validation.AccessCheckoutExpiryDateValidatedSuccessListener
import com.worldpay.access.checkout.validation.ValidationResult

class ExpiryMonthValidationResultHandler(
    private val validationListener: AccessCheckoutExpiryDateValidatedSuccessListener,
    private val validationStateManager: ValidationStateManager
) {

    fun handleResult(validationResult: ValidationResult) {
        validationListener.onExpiryDateValidated(validationResult.complete)
        validationStateManager.monthValidated.set(validationResult.complete)

        if (validationStateManager.isAllValid()) {
            validationListener.onValidationSuccess()
        }
    }

}