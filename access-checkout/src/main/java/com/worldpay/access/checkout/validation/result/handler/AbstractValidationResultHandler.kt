package com.worldpay.access.checkout.validation.result.handler

import androidx.lifecycle.Lifecycle
import androidx.lifecycle.LifecycleObserver
import androidx.lifecycle.LifecycleOwner
import androidx.lifecycle.OnLifecycleEvent
import com.worldpay.access.checkout.validation.result.state.FieldValidationState

internal abstract class AbstractValidationResultHandler(
    private var fieldValidationState: FieldValidationState,
    lifecycleOwner : LifecycleOwner
) : LifecycleObserver {

    companion object {
        private val state = mutableMapOf<Int, FieldValidationState>()
    }

    init {
        lifecycleOwner.lifecycle.addObserver(this)
    }

    private var inLifecycleEvent = false

    @OnLifecycleEvent(Lifecycle.Event.ON_DESTROY)
    internal fun onDestroy() {
        getState().validationState = false
        getState().notificationSent = false
        state.remove(fieldValidationState.id)
    }

    @OnLifecycleEvent(Lifecycle.Event.ON_RESUME)
    internal fun onResume() {
        inLifecycleEvent = false
        if (state[fieldValidationState.id] != null) {
            setState(state[fieldValidationState.id]!!)
            state.remove(fieldValidationState.id)
        }
        if (getState().notificationSent) {
            notifyListener(getState().validationState)
        }
    }

    @OnLifecycleEvent(Lifecycle.Event.ON_PAUSE)
    internal fun onPause() {
        inLifecycleEvent = true
        state[fieldValidationState.id] = getState()
    }

    fun handleResult(isValid: Boolean, forceNotify: Boolean = false) {
        if (forceNotify || hasStateChanged(isValid)) {
            notifyListener(isValid)
        }
    }

    fun handleFocusChange() {
        if (!getState().notificationSent && !inLifecycleEvent) {
            notifyListener(getState().validationState)
        }
    }

    abstract fun hasStateChanged(isValid: Boolean): Boolean

    abstract fun notifyListener(isValid: Boolean)

    abstract fun getState(): FieldValidationState

    abstract fun setState(fieldValidationState: FieldValidationState)
}
