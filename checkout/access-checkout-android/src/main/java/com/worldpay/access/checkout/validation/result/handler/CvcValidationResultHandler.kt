package com.worldpay.access.checkout.validation.result.handler

import com.worldpay.access.checkout.client.validation.listener.AccessCheckoutCvcValidationListener
import com.worldpay.access.checkout.validation.result.state.CvcFieldValidationStateManager

internal class CvcValidationResultHandler(
    private val validationListener: AccessCheckoutCvcValidationListener,
    private val validationStateManager: CvcFieldValidationStateManager
) {

    private var notificationSent = false

    fun handleResult(isValid: Boolean) {
        if (hasStateChanged(isValid)) {
            notifyListener(isValid)
        }
    }

    fun handleFocusChange() {
        if (!notificationSent) {
            notifyListener(validationStateManager.cvcValidationState)
        }
    }

    private fun hasStateChanged(isValid : Boolean) = isValid != validationStateManager.cvcValidationState

    private fun notifyListener(isValid : Boolean) {
        validationListener.onCvcValidated(isValid)
        validationStateManager.cvcValidationState = isValid

        if (validationStateManager.isAllValid()) {
            validationListener.onValidationSuccess()
        }

        notificationSent = true
    }

}
