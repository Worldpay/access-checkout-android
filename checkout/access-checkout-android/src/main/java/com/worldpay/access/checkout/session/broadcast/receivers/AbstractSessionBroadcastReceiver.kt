package com.worldpay.access.checkout.session.broadcast.receivers

import android.content.BroadcastReceiver
import android.content.IntentFilter

abstract class AbstractSessionBroadcastReceiver: BroadcastReceiver() {

    abstract fun getIntentFilter(): IntentFilter

}