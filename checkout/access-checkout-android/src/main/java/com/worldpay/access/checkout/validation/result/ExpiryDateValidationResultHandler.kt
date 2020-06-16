package com.worldpay.access.checkout.validation.result

import com.worldpay.access.checkout.client.validation.listener.AccessCheckoutExpiryDateValidationListener
import com.worldpay.access.checkout.validation.state.ExpiryDateFieldValidationStateManager

internal class ExpiryDateValidationResultHandler(
    private val validationListener: AccessCheckoutExpiryDateValidationListener,
    private val validationStateManager: ExpiryDateFieldValidationStateManager
) {

    fun handleResult(isValid: Boolean) {
        validationListener.onExpiryDateValidated(isValid)
        validationStateManager.expiryDateValidated = isValid

        if (validationStateManager.isAllValid()) {
            validationListener.onValidationSuccess()
        }
    }

}
