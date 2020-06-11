package com.worldpay.access.checkout.validation.result

import com.worldpay.access.checkout.validation.state.ExpiryDateFieldValidationStateManager
import com.worldpay.access.checkout.client.validation.listener.AccessCheckoutExpiryDateValidationListener

class ExpiryDateValidationResultHandler(
    private val validationListener: AccessCheckoutExpiryDateValidationListener,
    private val validationStateManager: ExpiryDateFieldValidationStateManager
) {

    fun handleResult(isValid: Boolean) {
        validationListener.onExpiryDateValidated(isValid)
        validationStateManager.expiryDateValidated.set(isValid)

        if (validationStateManager.isAllValid()) {
            validationListener.onValidationSuccess()
        }
    }

}
