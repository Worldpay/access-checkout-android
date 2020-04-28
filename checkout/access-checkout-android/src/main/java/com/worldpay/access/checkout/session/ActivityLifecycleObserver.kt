package com.worldpay.access.checkout.session

import androidx.lifecycle.Lifecycle
import androidx.lifecycle.LifecycleObserver
import androidx.lifecycle.LifecycleOwner
import androidx.lifecycle.OnLifecycleEvent
import com.worldpay.access.checkout.logging.LoggingUtils
import com.worldpay.access.checkout.session.request.broadcast.SessionBroadcastManager
import com.worldpay.access.checkout.session.request.broadcast.SessionBroadcastManagerFactory

internal class ActivityLifecycleObserver(
    private val tag: String,
    private val lifecycleOwner: LifecycleOwner,
    private val sessionBroadcastManagerFactory: SessionBroadcastManagerFactory
) : LifecycleObserver {

    private lateinit var sessionBroadcastManager: SessionBroadcastManager

    init {
        onCreateListener()
    }

    @OnLifecycleEvent(Lifecycle.Event.ON_CREATE)
    private fun onCreateListener() {
        LoggingUtils.debugLog(tag, "On Create")
        lifecycleOwner.lifecycle.addObserver(this)
    }

    @OnLifecycleEvent(Lifecycle.Event.ON_RESUME)
    internal fun resumeListener() {
        LoggingUtils.debugLog(tag, "On Resume")
        sessionBroadcastManager = sessionBroadcastManagerFactory.createInstance()
        sessionBroadcastManager.register()
    }

    @OnLifecycleEvent(Lifecycle.Event.ON_STOP)
    internal fun stopListener() {
        LoggingUtils.debugLog(tag, "On Stop")
        sessionBroadcastManager.unregister()
    }

    @OnLifecycleEvent(Lifecycle.Event.ON_STOP)
    internal fun onStopListener() {
        lifecycleOwner.lifecycle.removeObserver(this)
    }

}