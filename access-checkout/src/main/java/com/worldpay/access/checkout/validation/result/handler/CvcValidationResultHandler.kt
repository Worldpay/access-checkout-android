package com.worldpay.access.checkout.validation.result.handler

import androidx.lifecycle.LifecycleOwner
import com.worldpay.access.checkout.client.validation.listener.AccessCheckoutCvcValidationListener
import com.worldpay.access.checkout.validation.result.state.CvcFieldValidationStateManager

internal class CvcValidationResultHandler(
    private val validationListener: AccessCheckoutCvcValidationListener,
    private val validationStateManager: CvcFieldValidationStateManager,
    lifecycleOwner: LifecycleOwner
) : AbstractValidationResultHandler(lifecycleOwner) {

    override fun notifyListener(isValid: Boolean) {
        validationListener.onCvcValidated(isValid)

        if (isValid && validationStateManager.isAllValid()) {
            validationListener.onValidationSuccess()
        }
    }

    override fun getState() = validationStateManager.cvcValidationState
}
