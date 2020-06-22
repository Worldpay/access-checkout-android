package com.worldpay.access.checkout.validation.result.handler

import androidx.lifecycle.Lifecycle
import androidx.lifecycle.LifecycleObserver
import androidx.lifecycle.LifecycleOwner
import androidx.lifecycle.OnLifecycleEvent
import com.worldpay.access.checkout.client.validation.listener.AccessCheckoutExpiryDateValidationListener
import com.worldpay.access.checkout.validation.result.state.ExpiryDateFieldValidationStateManager

internal class ExpiryDateValidationResultHandler(
    private val validationListener: AccessCheckoutExpiryDateValidationListener,
    private val validationStateManager: ExpiryDateFieldValidationStateManager,
    lifecycleOwner : LifecycleOwner
) : LifecycleObserver {

    init {
        lifecycleOwner.lifecycle.addObserver(this)
    }

    @OnLifecycleEvent(Lifecycle.Event.ON_START)
    internal fun reValidate() {
        if (validationStateManager.expiryDateValidationState.notificationSent) {
            notifyListener(validationStateManager.expiryDateValidationState.validationState)
        }
    }

    fun handleResult(isValid: Boolean) {
        if (hasStateChanged(isValid)) {
            notifyListener(isValid)
        }
    }

    fun handleFocusChange() {
        if (!validationStateManager.expiryDateValidationState.notificationSent) {
            notifyListener(validationStateManager.expiryDateValidationState.validationState)
        }
    }

    private fun hasStateChanged(isValid : Boolean) = isValid != validationStateManager.expiryDateValidationState.validationState

    private fun notifyListener(isValid : Boolean) {
        validationListener.onExpiryDateValidated(isValid)
        validationStateManager.expiryDateValidationState.validationState = isValid

        if (validationStateManager.isAllValid()) {
            validationListener.onValidationSuccess()
        }

        validationStateManager.expiryDateValidationState.notificationSent = true
    }

}
