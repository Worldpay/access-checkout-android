package com.worldpay.access.checkout.validation.result

import com.worldpay.access.checkout.client.validation.listener.AccessCheckoutCvvValidationListener
import com.worldpay.access.checkout.validation.state.CvcFieldValidationStateManager

internal class CvvValidationResultHandler(
    private val validationListener: AccessCheckoutCvvValidationListener,
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
        validationListener.onCvvValidated(isValid)
        validationStateManager.cvcValidationState = isValid

        if (validationStateManager.isAllValid()) {
            validationListener.onValidationSuccess()
        }

        notificationSent = true
    }

}
