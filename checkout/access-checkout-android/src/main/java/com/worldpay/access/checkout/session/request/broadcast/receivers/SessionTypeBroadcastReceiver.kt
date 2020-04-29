package com.worldpay.access.checkout.session.request.broadcast.receivers

import android.content.Context
import android.content.Intent
import android.content.IntentFilter
import java.util.concurrent.atomic.AtomicInteger

internal class SessionTypeBroadcastReceiver : AbstractSessionBroadcastReceiver() {

    companion object {
        const val NUMBER_OF_SESSION_TYPES = "number-of-session-types"
    }

    private var numberOfSessionTypes = AtomicInteger(0)

    override fun onReceive(context: Context, intent: Intent) {
        if (intent.action == NUM_OF_SESSION_TYPES_REQUESTED) {
            numberOfSessionTypes = AtomicInteger(intent.getIntExtra(NUMBER_OF_SESSION_TYPES, 0))
        }
    }

    override fun getIntentFilter(): IntentFilter {
        val intentFilter = IntentFilter()
        intentFilter.addAction(NUM_OF_SESSION_TYPES_REQUESTED)
        intentFilter.addAction(SESSION_TYPE_REQUEST_COMPLETE)
        return intentFilter
    }

}