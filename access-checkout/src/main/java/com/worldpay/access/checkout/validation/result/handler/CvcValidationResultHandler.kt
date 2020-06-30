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

    private var inLifecycleEvent = false

    private var deferredEvent: Boolean? = null

    init {
        lifecycleOwner.lifecycle.addObserver(this)
    }

    @OnLifecycleEvent(Lifecycle.Event.ON_START)
    internal fun onStart() {
        if (validationStateManager.cvcValidationState.notificationSent) {
            notifyListener(validationStateManager.cvcValidationState.validationState)
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
        if (!validationStateManager.cvcValidationState.notificationSent && !inLifecycleEvent) {
            notifyListener(validationStateManager.cvcValidationState.validationState)
        }
    }

    private fun hasStateChanged(isValid : Boolean) = isValid != validationStateManager.cvcValidationState.validationState

    private fun notifyListener(isValid : Boolean) {
        if (inLifecycleEvent) {
            deferredEvent = isValid
            return
        }

        deferredEvent = null
        validationListener.onCvcValidated(isValid)
        validationStateManager.cvcValidationState.validationState = isValid

        if (validationStateManager.isAllValid()) {
            validationListener.onValidationSuccess()
        }

        validationStateManager.cvcValidationState.notificationSent = true
    }

}
