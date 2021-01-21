package com.worldpay.access.checkout.validation.result.handler

import androidx.lifecycle.LifecycleOwner
import com.worldpay.access.checkout.client.validation.listener.AccessCheckoutPanValidationListener
import com.worldpay.access.checkout.validation.result.state.FieldValidationState
import com.worldpay.access.checkout.validation.result.state.PanFieldValidationStateManager

internal class PanValidationResultHandler(
    private val validationListener: AccessCheckoutPanValidationListener,
    private val validationStateManager: PanFieldValidationStateManager,
    lifecycleOwner : LifecycleOwner
) : AbstractValidationResultHandler(validationStateManager.panValidationState, lifecycleOwner) {

    override fun notifyListener(isValid : Boolean) {
        validationListener.onPanValidated(isValid)
        getState().validationState = isValid

        if (validationStateManager.isAllValid()) {
            validationListener.onValidationSuccess()
        }

        getState().notificationSent = true
    }

    override fun hasStateChanged(isValid: Boolean): Boolean {
        return validationStateManager.panValidationState.validationState != isValid
    }

    override fun getState() = validationStateManager.panValidationState

    override fun setState(fieldValidationState: FieldValidationState) {
        validationStateManager.panValidationState = fieldValidationState
    }

}
