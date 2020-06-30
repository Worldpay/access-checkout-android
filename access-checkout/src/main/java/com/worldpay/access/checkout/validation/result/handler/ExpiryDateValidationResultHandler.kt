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

    private var inLifecycleEvent = false

    private var deferredEvent: Boolean? = null

    init {
        lifecycleOwner.lifecycle.addObserver(this)
    }

    @OnLifecycleEvent(Lifecycle.Event.ON_START)
    internal fun onStart() {
        if (validationStateManager.expiryDateValidationState.notificationSent) {
            notifyListener(validationStateManager.expiryDateValidationState.validationState)
        }
    }

    @OnLifecycleEvent(Lifecycle.Event.ON_RESUME)
    internal fun onResume() {
        inLifecycleEvent = false
        if (deferredEvent != null) {
            notifyListener(deferredEvent!!)
        }
    }

    @OnLifecycleEvent(Lifecycle.Event.ON_PAUSE)
    internal fun onPause() {
        inLifecycleEvent = true
    }


    fun handleResult(isValid: Boolean) {
        if (hasStateChanged(isValid)) {
            notifyListener(isValid)
        }
    }

    fun handleFocusChange() {
        if (!validationStateManager.expiryDateValidationState.notificationSent && !inLifecycleEvent) {
            notifyListener(validationStateManager.expiryDateValidationState.validationState)
        }
    }

    private fun hasStateChanged(isValid : Boolean) = isValid != validationStateManager.expiryDateValidationState.validationState

    private fun notifyListener(isValid : Boolean) {
        if (inLifecycleEvent) {
            deferredEvent = isValid
            return
        }

        deferredEvent = null
        validationListener.onExpiryDateValidated(isValid)
        validationStateManager.expiryDateValidationState.validationState = isValid

        if (validationStateManager.isAllValid()) {
            validationListener.onValidationSuccess()
        }

        validationStateManager.expiryDateValidationState.notificationSent = true
    }

}
