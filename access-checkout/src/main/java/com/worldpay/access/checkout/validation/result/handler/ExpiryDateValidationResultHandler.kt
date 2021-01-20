package com.worldpay.access.checkout.validation.result.handler

import androidx.lifecycle.LifecycleOwner
import com.worldpay.access.checkout.client.validation.listener.AccessCheckoutExpiryDateValidationListener
import com.worldpay.access.checkout.validation.result.state.ExpiryDateFieldValidationStateManager

internal class ExpiryDateValidationResultHandler(
    private val validationListener: AccessCheckoutExpiryDateValidationListener,
    private val validationStateManager: ExpiryDateFieldValidationStateManager,
    lifecycleOwner : LifecycleOwner
) : AbstractValidationResultHandler(validationStateManager.expiryDateValidationState, lifecycleOwner) {

    override fun notifyListener(isValid : Boolean) {
        validationListener.onExpiryDateValidated(isValid)
        validationStateManager.expiryDateValidationState.validationState = isValid

        if (validationStateManager.isAllValid()) {
            validationListener.onValidationSuccess()
        }

        validationStateManager.expiryDateValidationState.notificationSent = true
    }

}
