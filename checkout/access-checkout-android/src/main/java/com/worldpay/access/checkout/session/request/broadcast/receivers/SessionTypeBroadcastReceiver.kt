package com.worldpay.access.checkout.session.request.broadcast.receivers

import android.content.Context
import android.content.Intent
import android.content.IntentFilter

internal class SessionTypeBroadcastReceiver : AbstractSessionBroadcastReceiver() {

    companion object {
        const val NUMBER_OF_SESSION_TYPES = "number-of-session-types"
    }

    override fun onReceive(context: Context, intent: Intent) {
    }

    override fun getIntentFilter(): IntentFilter {
        return IntentFilter(javaClass.name)
    }

}