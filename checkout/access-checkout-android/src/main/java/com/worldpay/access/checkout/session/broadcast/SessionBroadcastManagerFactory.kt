package com.worldpay.access.checkout.session.broadcast

import com.worldpay.access.checkout.views.SessionResponseListener

internal class SessionBroadcastManagerFactory(
    private val localBroadcastManagerFactory: LocalBroadcastManagerFactory,
    private val externalSessionResponseListener: SessionResponseListener
) {

    fun createInstance(): SessionBroadcastManager = SessionBroadcastManager(localBroadcastManagerFactory, externalSessionResponseListener)

}
