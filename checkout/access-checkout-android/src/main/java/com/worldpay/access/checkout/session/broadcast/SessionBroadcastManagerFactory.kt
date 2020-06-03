package com.worldpay.access.checkout.session.broadcast

import com.worldpay.access.checkout.client.SessionResponseListener

internal class SessionBroadcastManagerFactory(
    private val localBroadcastManagerFactory: LocalBroadcastManagerFactory,
    private val externalSessionResponseListener: SessionResponseListener
) {

    fun createInstance(): SessionBroadcastManager = SessionBroadcastManager(localBroadcastManagerFactory, externalSessionResponseListener)

}
