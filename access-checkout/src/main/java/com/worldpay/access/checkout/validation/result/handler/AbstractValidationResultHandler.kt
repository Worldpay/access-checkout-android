package com.worldpay.access.checkout.validation.result.handler

import androidx.lifecycle.Lifecycle
import androidx.lifecycle.LifecycleObserver
import androidx.lifecycle.LifecycleOwner
import androidx.lifecycle.OnLifecycleEvent
import com.worldpay.access.checkout.validation.result.state.FieldValidationState

internal abstract class AbstractValidationResultHandler(
    private val fieldValidationState: FieldValidationState,
    lifecycleOwner : LifecycleOwner
) : LifecycleObserver {

    init {
        lifecycleOwner.lifecycle.addObserver(this)
    }

    companion object {
        private var validationState = false
        private var notificationSent = false
    }

    private var inLifecycleEvent = false

    @OnLifecycleEvent(Lifecycle.Event.ON_START)
    internal fun onStart() {
        if (fieldValidationState.notificationSent) {
            notifyListener(fieldValidationState.validationState)
        }
    }

    @OnLifecycleEvent(Lifecycle.Event.ON_RESUME)
    internal fun onResume() {
        inLifecycleEvent = false
        fieldValidationState.validationState = validationState
        fieldValidationState.notificationSent = notificationSent
        handleResult(
            isValid = fieldValidationState.validationState,
            forceNotify = true
        )
    }

    @OnLifecycleEvent(Lifecycle.Event.ON_PAUSE)
    internal fun onPause() {
        inLifecycleEvent = true
        validationState = fieldValidationState.validationState
        notificationSent = fieldValidationState.notificationSent
    }

    fun handleResult(isValid: Boolean, forceNotify: Boolean = false) {
        if (forceNotify || hasStateChanged(isValid)) {
            notifyListener(isValid)
        }
    }

    fun handleFocusChange() {
        if (!fieldValidationState.notificationSent && !inLifecycleEvent) {
            notifyListener(fieldValidationState.validationState)
        }
    }

    private fun hasStateChanged(isValid : Boolean) = isValid != fieldValidationState.validationState

    abstract fun notifyListener(isValid: Boolean)
}
