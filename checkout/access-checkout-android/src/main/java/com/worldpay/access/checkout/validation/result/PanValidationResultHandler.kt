package com.worldpay.access.checkout.validation.result

import com.worldpay.access.checkout.client.validation.listener.AccessCheckoutPanValidationListener
import com.worldpay.access.checkout.validation.state.PanFieldValidationStateManager

internal class PanValidationResultHandler(
    private val validationListener: AccessCheckoutPanValidationListener,
    private val validationStateManager: PanFieldValidationStateManager
) {

    private var notificationSent = false

    fun handleResult(isValid: Boolean) {
        if (hasStateChanged(isValid)) {
            notifyListener(isValid)
        }
    }

    fun handleFocusChange() {
        if (!notificationSent) {
            notifyListener(validationStateManager.panValidated)
        }
    }

    private fun hasStateChanged(isValid : Boolean) = isValid != validationStateManager.panValidated

    private fun notifyListener(isValid : Boolean) {
        validationListener.onPanValidated(isValid)
        validationStateManager.panValidated = isValid

        if (validationStateManager.isAllValid()) {
            validationListener.onValidationSuccess()
        }

        notificationSent = true
    }

}
