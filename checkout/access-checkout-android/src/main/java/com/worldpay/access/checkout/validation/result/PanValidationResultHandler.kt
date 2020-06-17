package com.worldpay.access.checkout.validation.result

import com.worldpay.access.checkout.client.validation.listener.AccessCheckoutPanValidationListener
import com.worldpay.access.checkout.validation.state.PanFieldValidationStateManager

internal class PanValidationResultHandler(
    private val validationListener: AccessCheckoutPanValidationListener,
    private val validationStateManager: PanFieldValidationStateManager
) {

    fun handleResult(isValid: Boolean) {
        validationListener.onPanValidated(isValid)
        validationStateManager.panValidated = isValid

        if (validationStateManager.isAllValid()) {
            validationListener.onValidationSuccess()
        }
    }

}
