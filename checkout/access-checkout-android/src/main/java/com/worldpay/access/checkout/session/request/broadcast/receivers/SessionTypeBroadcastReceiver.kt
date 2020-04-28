package com.worldpay.access.checkout.session.request.broadcast.receivers

import android.content.Context
import android.content.Intent
import android.content.IntentFilter

internal class SessionTypeBroadcastReceiver : AbstractSessionBroadcastReceiver() {

    companion object {
        const val NUMBER_OF_SESSION_TYPES = "number-of-session-types"
        const val ACTION_GET_NUMBER_OF_SESSION_TYPES = "get-number-of-session-types"
    }

    override fun onReceive(context: Context, intent: Intent) {
        TODO("Not yet implemented")
    }

    override fun getIntentFilter(): IntentFilter {
        return IntentFilter("expect-num-sessions")
    }

}