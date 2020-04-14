package com.worldpay.access.checkout.client.checkout

import android.content.IntentFilter
import androidx.lifecycle.Lifecycle
import androidx.lifecycle.LifecycleObserver
import androidx.lifecycle.LifecycleOwner
import androidx.lifecycle.OnLifecycleEvent
import androidx.localbroadcastmanager.content.LocalBroadcastManager
import com.worldpay.access.checkout.api.LocalBroadcastManagerFactory
import com.worldpay.access.checkout.api.session.SessionReceiver
import com.worldpay.access.checkout.api.session.SessionRequestService
import com.worldpay.access.checkout.logging.LoggingUtils

internal class ActivityLifecycleEventHandler(
    private val tag: String,
    private val sessionReceiver: SessionReceiver,
    private val lifecycleOwner: LifecycleOwner,
    private val localBroadcastManagerFactory: LocalBroadcastManagerFactory
) : LifecycleObserver {

    private lateinit var localBroadcastManager: LocalBroadcastManager

    init {
        onCreateHostRegistration()
    }

    @OnLifecycleEvent(Lifecycle.Event.ON_CREATE)
    private fun onCreateHostRegistration() {
        LoggingUtils.debugLog(tag, "On Create")
        lifecycleOwner.lifecycle.addObserver(this)
    }

    @OnLifecycleEvent(Lifecycle.Event.ON_RESUME)
    internal fun startListener() {
        LoggingUtils.debugLog(tag, "On Resume")
        localBroadcastManager = localBroadcastManagerFactory.createInstance()
        localBroadcastManager.registerReceiver(
            sessionReceiver,
            IntentFilter(SessionRequestService.ACTION_GET_SESSION)
        )
    }

    @OnLifecycleEvent(Lifecycle.Event.ON_STOP)
    internal fun disconnectListener() {
        LoggingUtils.debugLog(tag, "On Stop")
        localBroadcastManager.unregisterReceiver(sessionReceiver)
    }

    @OnLifecycleEvent(Lifecycle.Event.ON_STOP)
    internal fun removeObserver() {
        lifecycleOwner.lifecycle.removeObserver(this)
    }

}