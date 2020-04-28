package com.worldpay.access.checkout.session.request.broadcast.receivers

import android.content.Context
import android.content.Intent
import android.content.IntentFilter

internal class SessionBroadcastReceiver : AbstractSessionBroadcastReceiver() {

    override fun onReceive(context: Context?, intent: Intent?) {
        TODO("Not yet implemented")
    }

    override fun getIntentFilter(): IntentFilter {
        return IntentFilter(javaClass.name)
    }

}
