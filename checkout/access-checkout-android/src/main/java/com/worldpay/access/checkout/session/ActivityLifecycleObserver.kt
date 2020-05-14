package com.worldpay.access.checkout.session

import androidx.lifecycle.Lifecycle
import androidx.lifecycle.LifecycleObserver
import androidx.lifecycle.LifecycleOwner
import androidx.lifecycle.OnLifecycleEvent
import com.worldpay.access.checkout.session.broadcast.SessionBroadcastManager
import com.worldpay.access.checkout.session.broadcast.SessionBroadcastManagerFactory
import com.worldpay.access.checkout.util.logging.LoggingUtils

internal class ActivityLifecycleObserver(
    private val tag: String,
    lifecycleOwner: LifecycleOwner,
    sessionBroadcastManagerFactory: SessionBroadcastManagerFactory
) : LifecycleObserver {

    private var sessionBroadcastManager: SessionBroadcastManager = sessionBroadcastManagerFactory.createInstance()

    init {
        lifecycleOwner.lifecycle.addObserver(this)
    }

    @OnLifecycleEvent(Lifecycle.Event.ON_START)
    internal fun startListener() {
        LoggingUtils.debugLog(tag, "On Start")
        sessionBroadcastManager.register()
    }

    @OnLifecycleEvent(Lifecycle.Event.ON_STOP)
    internal fun stopListener() {
        LoggingUtils.debugLog(tag, "On Stop")
        sessionBroadcastManager.unregister()
    }

}