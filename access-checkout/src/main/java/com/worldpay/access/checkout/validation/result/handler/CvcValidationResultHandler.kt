package com.worldpay.access.checkout.validation.result.handler

import androidx.lifecycle.LifecycleOwner
import com.worldpay.access.checkout.client.validation.listener.AccessCheckoutCvcValidationListener
import com.worldpay.access.checkout.validation.result.state.CvcFieldValidationStateManager
import com.worldpay.access.checkout.validation.result.state.FieldValidationState

internal class CvcValidationResultHandler(
    private val validationListener: AccessCheckoutCvcValidationListener,
    private val validationStateManager: CvcFieldValidationStateManager,
    lifecycleOwner : LifecycleOwner
) : AbstractValidationResultHandler(validationStateManager.cvcValidationState, lifecycleOwner) {

    override fun notifyListener(isValid : Boolean) {
        validationListener.onCvcValidated(isValid)
        getState().validationState = isValid

        if (validationStateManager.isAllValid()) {
            validationListener.onValidationSuccess()
        }

        getState().notificationSent = true
    }

    override fun hasStateChanged(isValid: Boolean): Boolean {
        return validationStateManager.cvcValidationState.validationState != isValid
    }

    override fun getState() = validationStateManager.cvcValidationState

    override fun setState(fieldValidationState: FieldValidationState) {
        validationStateManager.cvcValidationState = fieldValidationState
    }

}
