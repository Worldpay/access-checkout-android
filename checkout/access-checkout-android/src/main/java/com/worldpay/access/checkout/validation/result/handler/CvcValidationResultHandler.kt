package com.worldpay.access.checkout.validation.result.handler

import androidx.lifecycle.Lifecycle
import androidx.lifecycle.LifecycleObserver
import androidx.lifecycle.LifecycleOwner
import androidx.lifecycle.OnLifecycleEvent
import com.worldpay.access.checkout.client.validation.listener.AccessCheckoutCvcValidationListener
import com.worldpay.access.checkout.validation.result.state.CvcFieldValidationStateManager

internal class CvcValidationResultHandler(
    private val validationListener: AccessCheckoutCvcValidationListener,
    private val validationStateManager: CvcFieldValidationStateManager,
    lifecycleOwner : LifecycleOwner
) : LifecycleObserver {

    init {
        lifecycleOwner.lifecycle.addObserver(this)
    }

    @OnLifecycleEvent(Lifecycle.Event.ON_START)
    internal fun onStart() {
        if (validationStateManager.cvcValidationState.notificationSent) {
            notifyListener(validationStateManager.cvcValidationState.validationState)
        }
    }

    fun handleResult(isValid: Boolean) {
        if (hasStateChanged(isValid)) {
            notifyListener(isValid)
        }
    }

    fun handleFocusChange() {
        if (!validationStateManager.cvcValidationState.notificationSent) {
            notifyListener(validationStateManager.cvcValidationState.validationState)
        }
    }

    private fun hasStateChanged(isValid : Boolean) = isValid != validationStateManager.cvcValidationState.validationState

    private fun notifyListener(isValid : Boolean) {
        validationListener.onCvcValidated(isValid)
        validationStateManager.cvcValidationState.validationState = isValid

        if (validationStateManager.isAllValid()) {
            validationListener.onValidationSuccess()
        }

        validationStateManager.cvcValidationState.notificationSent = true
    }

}
