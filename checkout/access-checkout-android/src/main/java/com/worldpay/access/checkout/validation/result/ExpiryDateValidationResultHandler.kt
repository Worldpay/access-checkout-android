package com.worldpay.access.checkout.validation.result

import com.worldpay.access.checkout.client.validation.listener.AccessCheckoutExpiryDateValidationListener
import com.worldpay.access.checkout.validation.state.ExpiryDateFieldValidationStateManager

internal class ExpiryDateValidationResultHandler(
    private val validationListener: AccessCheckoutExpiryDateValidationListener,
    private val validationStateManager: ExpiryDateFieldValidationStateManager
) {

    private var notificationSent = false

    fun handleResult(isValid: Boolean) {
        if (hasStateChanged(isValid)) {
            notifyListener(isValid)
        }
    }

    fun handleFocusChange() {
        if (!notificationSent) {
            notifyListener(validationStateManager.expiryDateValidationState)
        }
    }

    private fun hasStateChanged(isValid : Boolean) = isValid != validationStateManager.expiryDateValidationState

    private fun notifyListener(isValid : Boolean) {
        validationListener.onExpiryDateValidated(isValid)
        validationStateManager.expiryDateValidationState = isValid

        if (validationStateManager.isAllValid()) {
            validationListener.onValidationSuccess()
        }

        notificationSent = true
    }

}
