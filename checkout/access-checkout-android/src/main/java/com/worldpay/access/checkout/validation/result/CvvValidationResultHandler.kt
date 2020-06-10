package com.worldpay.access.checkout.validation.result

import com.worldpay.access.checkout.client.validation.listener.AccessCheckoutCvvValidationListener

class CvvValidationResultHandler(
    private val validationListener: AccessCheckoutCvvValidationListener,
    private val validationStateManager: ValidationStateManager
) {

    fun handleResult(validationResult: Boolean) {
        validationListener.onCvvValidated(validationResult)
        validationStateManager.cvvValidated.set(validationResult)

        if (validationStateManager.isAllValid()) {
            validationListener.onValidationSuccess()
        }
    }

}
