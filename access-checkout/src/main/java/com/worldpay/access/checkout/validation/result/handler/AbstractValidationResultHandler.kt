package com.worldpay.access.checkout.validation.result.handler

import androidx.lifecycle.Lifecycle.Event.*
import androidx.lifecycle.LifecycleObserver
import androidx.lifecycle.LifecycleOwner
import androidx.lifecycle.OnLifecycleEvent
import com.worldpay.access.checkout.validation.result.state.FieldValidationState

internal abstract class AbstractValidationResultHandler(
    lifecycleOwner: LifecycleOwner
) : LifecycleObserver {

    init {
        lifecycleOwner.lifecycle.addObserver(this)
    }

    companion object {
        private var VALIDITY_STATE_MAP = mutableMapOf<Int, FieldValidationState?>()
    }

    private var inLifecycleEvent = false

    @OnLifecycleEvent(ON_RESUME)
    internal fun onResume() {
        inLifecycleEvent = false

        if (!VALIDITY_STATE_MAP.containsKey(getState().id)) {
            VALIDITY_STATE_MAP[getState().id] = null
        }
    }

    @OnLifecycleEvent(ON_PAUSE)
    internal fun onPause() {
        inLifecycleEvent = true
    }

    @OnLifecycleEvent(ON_STOP)
    internal fun onStop() {
        val stateFromStore = getStateFromStore()
        if (stateFromStore != null) {
            stateFromStore.notificationSent = false
        }
    }

    fun handleResult(isValid: Boolean, forceNotify: Boolean = false) {
        val stateHasChanged = getState().validationState != isValid
        val storedState = getStateFromStore()

        if (storedState != null && !storedState.notificationSent) {
            notify(isValid)
        } else if (stateHasChanged) {
            notify(isValid)
        } else if (forceNotify) {
            notify(isValid)
        }
    }

    fun handleFocusChange() {
        if (!getState().notificationSent && !inLifecycleEvent) {
            notify(getState().validationState)
        }
    }

    private fun notify(isValid: Boolean) {
        getState().validationState = isValid
        getState().notificationSent = true
        VALIDITY_STATE_MAP[getState().id] = getState()
        notifyListener(isValid)
    }

    private fun getStateFromStore(): FieldValidationState? {
        return VALIDITY_STATE_MAP[getState().id]
    }

    abstract fun notifyListener(isValid: Boolean)

    abstract fun getState(): FieldValidationState

}
