package com.worldpay.access.checkout.validation.result.handler

import androidx.lifecycle.LifecycleOwner
import com.worldpay.access.checkout.client.validation.listener.AccessCheckoutExpiryDateValidationListener
import com.worldpay.access.checkout.validation.result.state.ExpiryDateFieldValidationStateManager
import com.worldpay.access.checkout.validation.result.state.FieldValidationState

internal class ExpiryDateValidationResultHandler(
    private val validationListener: AccessCheckoutExpiryDateValidationListener,
    private val validationStateManager: ExpiryDateFieldValidationStateManager,
    lifecycleOwner : LifecycleOwner
) : AbstractValidationResultHandler(validationStateManager.expiryDateValidationState, lifecycleOwner) {

    override fun notifyListener(isValid : Boolean) {
        validationListener.onExpiryDateValidated(isValid)
        getState().validationState = isValid

        if (validationStateManager.isAllValid()) {
            validationListener.onValidationSuccess()
        }

        getState().notificationSent = true
    }

    override fun hasStateChanged(isValid: Boolean): Boolean {
        return validationStateManager.expiryDateValidationState.validationState != isValid
    }

    override fun getState() = validationStateManager.expiryDateValidationState

    override fun setState(fieldValidationState: FieldValidationState) {
        validationStateManager.expiryDateValidationState = fieldValidationState
    }

}
