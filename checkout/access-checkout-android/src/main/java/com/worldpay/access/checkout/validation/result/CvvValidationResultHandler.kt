package com.worldpay.access.checkout.validation.result

import com.worldpay.access.checkout.client.validation.listener.AccessCheckoutCvvValidationListener
import com.worldpay.access.checkout.validation.state.CvcFieldValidationStateManager

internal class CvvValidationResultHandler(
    private val validationListener: AccessCheckoutCvvValidationListener,
    private val validationStateManager: CvcFieldValidationStateManager
) {

    fun handleResult(validationResult: Boolean) {
        validationListener.onCvvValidated(validationResult)
        validationStateManager.cvvValidated = validationResult

        if (validationStateManager.isAllValid()) {
            validationListener.onValidationSuccess()
        }
    }

}
