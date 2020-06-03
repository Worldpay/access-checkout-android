package com.worldpay.access.checkout.session.broadcast

import androidx.localbroadcastmanager.content.LocalBroadcastManager
import com.worldpay.access.checkout.client.SessionResponseListener
import com.worldpay.access.checkout.session.broadcast.receivers.SessionBroadcastReceiver

internal class SessionBroadcastManager(
    localBroadcastManagerFactory: LocalBroadcastManagerFactory,
    externalSessionResponseListener: SessionResponseListener
) {

    private var localBroadcastManager: LocalBroadcastManager = localBroadcastManagerFactory.createInstance()

    private val broadcastReceivers = listOf(
        SessionBroadcastReceiver(externalSessionResponseListener)
    )

    fun register() {
        for (receiver in broadcastReceivers) {
            localBroadcastManager.registerReceiver(receiver, receiver.getIntentFilter())
        }
    }

    fun unregister() {
        for (receiver in broadcastReceivers) {
            localBroadcastManager.unregisterReceiver(receiver)
        }
    }

}
