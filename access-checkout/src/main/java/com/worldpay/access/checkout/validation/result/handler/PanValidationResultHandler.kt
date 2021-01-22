package com.worldpay.access.checkout.validation.result.handler

import androidx.lifecycle.LifecycleOwner
import com.worldpay.access.checkout.client.validation.listener.AccessCheckoutPanValidationListener
import com.worldpay.access.checkout.validation.result.state.PanFieldValidationStateManager

internal class PanValidationResultHandler(
    private val validationListener: AccessCheckoutPanValidationListener,
    private val validationStateManager: PanFieldValidationStateManager,
    lifecycleOwner : LifecycleOwner
) : AbstractValidationResultHandler(lifecycleOwner) {

    override fun notifyListener(isValid : Boolean) {
        validationListener.onPanValidated(isValid)

        if (isValid && validationStateManager.isAllValid()) {
            validationListener.onValidationSuccess()
        }
    }

    override fun getState() = validationStateManager.panValidationState

}
