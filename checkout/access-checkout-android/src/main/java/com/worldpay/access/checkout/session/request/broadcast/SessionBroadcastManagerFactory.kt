package com.worldpay.access.checkout.session.request.broadcast

internal class SessionBroadcastManagerFactory(private val localBroadcastManagerFactory: LocalBroadcastManagerFactory) {

    fun createInstance(): SessionBroadcastManager = SessionBroadcastManager(localBroadcastManagerFactory)

}
