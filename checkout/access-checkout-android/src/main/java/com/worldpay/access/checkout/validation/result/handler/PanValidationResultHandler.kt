package com.worldpay.access.checkout.validation.result.handler

import androidx.lifecycle.*
import com.worldpay.access.checkout.client.validation.listener.AccessCheckoutPanValidationListener
import com.worldpay.access.checkout.validation.result.state.PanFieldValidationStateManager

internal class PanValidationResultHandler(
    private val validationListener: AccessCheckoutPanValidationListener,
    private val validationStateManager: PanFieldValidationStateManager,
    lifecycleOwner : LifecycleOwner
) : LifecycleObserver {

    init {
        lifecycleOwner.lifecycle.addObserver(this)
    }

    @OnLifecycleEvent(Lifecycle.Event.ON_START)
    internal fun onStart() {
        if (validationStateManager.panValidationState.notificationSent) {
            notifyListener(validationStateManager.panValidationState.validationState)
        }
    }

    fun handleResult(isValid: Boolean) {
        if (hasStateChanged(isValid)) {
            notifyListener(isValid)
        }
    }

    fun handleFocusChange() {
        if (!validationStateManager.panValidationState.notificationSent) {
            notifyListener(validationStateManager.panValidationState.validationState)
        }
    }

    private fun hasStateChanged(isValid : Boolean) = isValid != validationStateManager.panValidationState.validationState

    private fun notifyListener(isValid : Boolean) {
        validationListener.onPanValidated(isValid)
        validationStateManager.panValidationState.validationState = isValid

        if (validationStateManager.isAllValid()) {
            validationListener.onValidationSuccess()
        }

        validationStateManager.panValidationState.notificationSent = true
    }

}
