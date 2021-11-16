package com.worldpay.access.checkout.session

import android.util.Log
import androidx.lifecycle.Lifecycle
import androidx.lifecycle.LifecycleObserver
import androidx.lifecycle.LifecycleOwner
import androidx.lifecycle.OnLifecycleEvent
import com.worldpay.access.checkout.session.broadcast.SessionBroadcastManager
import com.worldpay.access.checkout.session.broadcast.SessionBroadcastManagerFactory

internal class ActivityLifecycleObserver(
    private val tag: String,
    lifecycleOwner: LifecycleOwner,
    sessionBroadcastManagerFactory: SessionBroadcastManagerFactory,
    private val sessionBroadcastManager: SessionBroadcastManager = sessionBroadcastManagerFactory.createInstance()
) : LifecycleObserver {

    companion object {
        var inLifeCycleState = false
        val messageQueue = mutableListOf<() -> Any>()

        fun sendToMessageQueue(fn: () -> Any) {
            messageQueue.add(fn)
        }

        fun processMessageQueue() {
            messageQueue.forEach { it.invoke() }
            messageQueue.clear()
        }
    }

    init {
        lifecycleOwner.lifecycle.addObserver(this)
    }

    @OnLifecycleEvent(Lifecycle.Event.ON_START)
    internal fun startListener() {
        Log.d(tag, "On Start")
        sessionBroadcastManager.register()
    }

    @OnLifecycleEvent(Lifecycle.Event.ON_STOP)
    internal fun stopListener() {
        Log.d(tag, "On Stop")
        sessionBroadcastManager.unregister()
    }

    @OnLifecycleEvent(Lifecycle.Event.ON_RESUME)
    internal fun onResume() {
        Log.d(tag, "On Resume")
        inLifeCycleState = false
        processMessageQueue()
    }

    @OnLifecycleEvent(Lifecycle.Event.ON_PAUSE)
    internal fun onPause() {
        Log.d(tag, "On Pause")
        inLifeCycleState = true
    }
}
