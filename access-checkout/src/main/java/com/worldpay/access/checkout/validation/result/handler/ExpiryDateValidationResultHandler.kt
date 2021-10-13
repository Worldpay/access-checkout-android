package com.worldpay.access.checkout.validation.result.handler

import androidx.lifecycle.LifecycleOwner
import com.worldpay.access.checkout.client.validation.listener.AccessCheckoutExpiryDateValidationListener
import com.worldpay.access.checkout.validation.result.state.ExpiryDateFieldValidationStateManager

internal class ExpiryDateValidationResultHandler(
    private val validationListener: AccessCheckoutExpiryDateValidationListener,
    private val validationStateManager: ExpiryDateFieldValidationStateManager,
    lifecycleOwner: LifecycleOwner
) : AbstractValidationResultHandler() {

    init {
        lifecycleOwner.lifecycle.addObserver(this)
    }

    override fun notifyListener(isValid: Boolean) {
        validationListener.onExpiryDateValidated(isValid)

        if (isValid && validationStateManager.isAllValid()) {
            validationListener.onValidationSuccess()
        }
    }

    override fun getState() = validationStateManager.expiryDateValidationState
}
