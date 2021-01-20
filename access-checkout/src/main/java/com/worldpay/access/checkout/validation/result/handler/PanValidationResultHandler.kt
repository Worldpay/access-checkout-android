package com.worldpay.access.checkout.validation.result.handler

import androidx.lifecycle.LifecycleOwner
import com.worldpay.access.checkout.client.validation.listener.AccessCheckoutPanValidationListener
import com.worldpay.access.checkout.validation.result.state.PanFieldValidationStateManager

internal class PanValidationResultHandler(
    private val validationListener: AccessCheckoutPanValidationListener,
    private val validationStateManager: PanFieldValidationStateManager,
    lifecycleOwner : LifecycleOwner
) : AbstractValidationResultHandler(validationStateManager.panValidationState, lifecycleOwner) {

    override fun notifyListener(isValid : Boolean) {
        validationListener.onPanValidated(isValid)
        validationStateManager.panValidationState.validationState = isValid

        if (validationStateManager.isAllValid()) {
            validationListener.onValidationSuccess()
        }

        validationStateManager.panValidationState.notificationSent = true
    }

}
